package com.project.gulimall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.project.common.constant.OrderConstant;
import com.project.common.domain.vo.MemberResponseVo;
import com.project.common.utils.R;
import com.project.gulimall.order.domain.entity.OrderItemEntity;
import com.project.gulimall.order.domain.to.OrderCreateTo;
import com.project.gulimall.order.domain.vo.*;
import com.project.gulimall.order.feign.CartFeignService;
import com.project.gulimall.order.feign.MemberFeignService;
import com.project.gulimall.order.feign.ProductFeignService;
import com.project.gulimall.order.feign.WareFeignService;
import com.project.gulimall.order.interceptor.LoginUserInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.common.utils.PageUtils;
import com.project.common.utils.Query;
import com.project.gulimall.order.dao.OrderDao;
import com.project.gulimall.order.domain.entity.OrderEntity;
import com.project.gulimall.order.service.OrderService;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    private ThreadLocal<OrderSubmitVo> threadLocal = new ThreadLocal<>();

    @Autowired
    private MemberFeignService memberFeignService;
    @Autowired
    private CartFeignService cartFeignService;
    @Autowired
    private WareFeignService wareFeignService;
    @Autowired
    private ProductFeignService productFeignService;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 查询订单确认页需要用到的数据
     */
    @Override
    public OrderConfirmVo confirmOrder() {
        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();
        MemberResponseVo memberResponseVo = LoginUserInterceptor.threadLocal.get();
        System.out.println("主线程..." + Thread.currentThread().getName() + ": " + Thread.currentThread().getId());

        // 获取之前的请求
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        CompletableFuture<Void> getAddressFuture = CompletableFuture.runAsync(() -> {
            System.out.println("member线程..." + Thread.currentThread().getName() + ": " + Thread.currentThread().getId());
            // 共享之前的请求数据
            RequestContextHolder.setRequestAttributes(requestAttributes);
            // 1. 远程查询当前用户的所有收获地址
            try {
                List<MemberAddressVo> address = memberFeignService.getAddressByUserId(memberResponseVo.getId());
                orderConfirmVo.setAddress(address);
            } finally {
                RequestContextHolder.resetRequestAttributes(); // 关键：清理
            }
        }, threadPoolExecutor);

        CompletableFuture<Void> getCurrentUserCartFuture = CompletableFuture.runAsync(() -> {
            System.out.println("cart线程..." + Thread.currentThread().getName() + ": " + Thread.currentThread().getId());
            // 共享之前的请求数据
            RequestContextHolder.setRequestAttributes(requestAttributes);
            // 2. 远程查询购物项信息
            try {
                List<OrderItemVo> items = cartFeignService.getCurrentUserCartItems(memberResponseVo.getId());
                orderConfirmVo.setItems(items);
            } finally {
                RequestContextHolder.resetRequestAttributes(); // 关键：清理
            }
        }, threadPoolExecutor).thenRunAsync(() -> {
            List<OrderItemVo> items = orderConfirmVo.getItems();
            List<Long> skuIds = items.stream().map(OrderItemVo::getSkuId).collect(Collectors.toList());
            R r = wareFeignService.getSkusHaveStock(skuIds);
            List<SkuHasStockVo> data = r.getData(new TypeReference<List<SkuHasStockVo>>() {
            });
            if (!data.isEmpty()) {
                orderConfirmVo.setStocks(data.stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHasStock)));
            }
        });

        // 3. 查询用户积分
        Integer integration = memberResponseVo.getIntegration();

        orderConfirmVo.setIntegration(integration);
        // 4. 总价在实体类中自动计算，这里无需赋值

        // 5. 防重令牌的添加
        String token = UUID.randomUUID().toString().replace("-", "");
        stringRedisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberResponseVo.getId(), token, 30, TimeUnit.MINUTES);
        orderConfirmVo.setOrderToken(token);

        try {
            CompletableFuture.allOf(getAddressFuture, getCurrentUserCartFuture).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        return orderConfirmVo;
    }

    @Override
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo orderSubmitVo) {
        // 将页面传递的数据放入 ThreadLocal
        threadLocal.set(orderSubmitVo);

        SubmitOrderResponseVo submitOrderResponseVo = new SubmitOrderResponseVo();
        MemberResponseVo memberResponseVo = LoginUserInterceptor.threadLocal.get();
        // 1. 验证令牌
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(
                "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                        "   redis.call('del', KEYS[1]); " +
                        "   return 1; " +
                        "else " +
                        "   return 0; " +
                        "end"
        ); // 返回 0 校验失败；返回 1 校验成功
        redisScript.setResultType(Long.class);

        String orderToken = orderSubmitVo.getOrderToken();
        String redisToken = stringRedisTemplate.opsForValue().get(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberResponseVo.getId());
        Long result = stringRedisTemplate.execute(redisScript, Collections.singletonList(redisToken), orderToken);
        if (result == 0L) {
            // 令牌验证失败
            submitOrderResponseVo.setCode(1);
            return submitOrderResponseVo;
        } else {
            // TODO 验证成功
            OrderCreateTo orderCreateTo = creatOrder();

        }
        return submitOrderResponseVo;
    }

    private OrderCreateTo creatOrder() {
        OrderCreateTo orderCreateTo = new OrderCreateTo();
        // 1. 生成订单号
        String orderSn = IdWorker.getTimeId();
        // 创建订单号
        OrderEntity orderEntity = buildOrder(orderSn);
        orderCreateTo.setOrder(orderEntity);

        // 2. 获取到所有的订单项
        List<OrderItemEntity> orderItemEntities = buildOrderItems(orderSn);
        orderCreateTo.setOrderItems(orderItemEntities);

        return orderCreateTo;
    }

    private OrderEntity buildOrder(String orderSn) {

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(orderSn);
        // 获取收获地址信息
        OrderSubmitVo orderSubmitVo = threadLocal.get();
        R fare = wareFeignService.getFare(orderSubmitVo.getAddrId());
        FareVo fareVo = fare.getData(new TypeReference<FareVo>() {
        });
        // 设置运费
        orderEntity.setFreightAmount(fareVo.getFare());
        orderEntity.setReceiverProvince(fareVo.getAddress().getProvince());
        orderEntity.setReceiverCity(fareVo.getAddress().getCity());
        orderEntity.setReceiverRegion(fareVo.getAddress().getRegion());
        orderEntity.setReceiverDetailAddress(fareVo.getAddress().getDetailAddress());
        orderEntity.setReceiverName(fareVo.getAddress().getName());
        orderEntity.setReceiverPhone(fareVo.getAddress().getPhone());
        orderEntity.setReceiverPostCode(fareVo.getAddress().getPostCode());

        return orderEntity;
    }

    /**
     * 构建所有订单项数据
     */
    private List<OrderItemEntity> buildOrderItems(String orderSn) {
        List<OrderItemVo> currentUserCartItems = cartFeignService.getCurrentUserCartItems(LoginUserInterceptor.threadLocal.get().getId());
        if (!currentUserCartItems.isEmpty()) {
            List<OrderItemEntity> orderItemEntities = currentUserCartItems.stream().map(cartItem -> {
                OrderItemEntity orderItemEntity = buildOneOrderItem(cartItem);
                orderItemEntity.setOrderSn(orderSn);
                return orderItemEntity;
            }).collect(Collectors.toList());
            return orderItemEntities;
        }
        return null;
    }

    /**
     * 构建某一个订单项数据
     */
    private OrderItemEntity buildOneOrderItem(OrderItemVo cartItem) {
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        // 1. 订单信息（由 buildOrder(String orderSn) 完成）
        // 2. 商品的 spu 信息
        R r = productFeignService.getSpuInfoBySkuId(cartItem.getSkuId());
        if (r.getCode() == 0) {
            SpuInfoVo spuInfoEntity = r.getData(new TypeReference<SpuInfoVo>() {
            });
            orderItemEntity.setSpuId(spuInfoEntity.getId());
            orderItemEntity.setSpuName(spuInfoEntity.getSpuName());
            orderItemEntity.setSpuBrand(spuInfoEntity.getBrandId().toString());
            orderItemEntity.setCategoryId(spuInfoEntity.getCatalogId());
        }
        // 3. 商品 sku 信息
        orderItemEntity.setSkuId(cartItem.getSkuId());
        orderItemEntity.setSkuName(cartItem.getTitle());
        orderItemEntity.setSkuPic(cartItem.getImage());
        orderItemEntity.setSkuPrice(cartItem.getPrice());
        String skuAttr = StringUtils.collectionToDelimitedString(cartItem.getSkuAttr(), ";");
        orderItemEntity.setSkuAttrsVals(skuAttr);
        orderItemEntity.setSkuQuantity(cartItem.getCount());
        // 4. 积分信息
        orderItemEntity.setGiftGrowth(cartItem.getPrice().intValue());
        orderItemEntity.setGiftIntegration(cartItem.getPrice().intValue());

        return orderItemEntity;
    }

}



