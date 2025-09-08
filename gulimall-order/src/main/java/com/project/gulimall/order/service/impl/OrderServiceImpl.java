package com.project.gulimall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.project.common.constant.OrderConstant;
import com.project.common.domain.vo.MemberResponseVo;
import com.project.common.exception.NoStockException;
import com.project.common.to.mq.OrderReleaseTo;
import com.project.common.utils.R;
import com.project.gulimall.order.domain.entity.OrderItemEntity;
import com.project.gulimall.order.domain.to.OrderCreateTo;
import com.project.gulimall.order.domain.vo.*;
import com.project.gulimall.order.enume.OrderStatusEnum;
import com.project.gulimall.order.feign.CartFeignService;
import com.project.gulimall.order.feign.MemberFeignService;
import com.project.gulimall.order.feign.ProductFeignService;
import com.project.gulimall.order.feign.WareFeignService;
import com.project.gulimall.order.interceptor.LoginUserInterceptor;
import com.project.gulimall.order.service.OrderItemService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
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
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private RabbitTemplate rabbitTemplate;

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

    @Transactional
    @Override
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo orderSubmitVo) {
        // 将页面传递的数据放入 ThreadLocal
        threadLocal.set(orderSubmitVo);

        SubmitOrderResponseVo submitOrderResponseVo = new SubmitOrderResponseVo();
        submitOrderResponseVo.setCode(0);
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
        String redisTokenKey = OrderConstant.USER_ORDER_TOKEN_PREFIX + memberResponseVo.getId();
        Long result = stringRedisTemplate.execute(redisScript, Collections.singletonList(redisTokenKey), orderToken);
        if (result == 0L) {
            // 令牌验证失败
            submitOrderResponseVo.setCode(1);
            return submitOrderResponseVo;
        } else {
            // 验证成功
            // 1. 创建订单、订单项等信息
            OrderCreateTo orderCreateTo = creatOrder();
            // 2. 验价
            BigDecimal payAmount = orderCreateTo.getOrder().getPayAmount();
            if (Math.abs(payAmount.subtract(orderSubmitVo.getPayPrice()).doubleValue()) < 0.01) {
                // 3. 金额对比成功，保存订单
                saveOrder(orderCreateTo);
                // 4. 库存锁定，只要有异常就回滚订单数据
                WareSkuLockVo wareSkuLockVo = new WareSkuLockVo();
                wareSkuLockVo.setOrderSn(orderCreateTo.getOrder().getOrderSn());
                List<OrderItemVo> orderItemVoList = orderCreateTo.getOrderItems().stream().map(item -> {
                    OrderItemVo orderItemVo = new OrderItemVo();
                    orderItemVo.setSkuId(item.getSkuId());
                    orderItemVo.setCount(item.getSkuQuantity());
                    orderItemVo.setTitle(item.getSkuName());
                    return orderItemVo;
                }).collect(Collectors.toList());
                wareSkuLockVo.setLockItems(orderItemVoList);
                // 远程锁定库存
                R r = wareFeignService.orderLockStock(wareSkuLockVo);
                if (r.getCode() == 0) {
                    // 库存锁定成功
                    submitOrderResponseVo.setOrder(orderCreateTo.getOrder());
                    // rabbitTemplate.convertAndSend("order-event-exchange", "order.create.order", orderCreateTo.getOrder());

                    // 在事务提交后发送消息
                    TransactionSynchronizationManager.registerSynchronization(
                            new TransactionSynchronization() {
                                @Override
                                public void afterCommit() {
                                    try {
                                        rabbitTemplate.convertAndSend(
                                                "order-event-exchange",
                                                "order.create.order",
                                                orderCreateTo.getOrder()
                                        );
                                    } catch (Exception e) {
                                        log.error("订单消息发送失败", e);
                                    }
                                }
                            }
                    );
                    return submitOrderResponseVo;
                } else {
                    // 库存锁定失败
                    throw new NoStockException();
                }
            } else {
                submitOrderResponseVo.setCode(2);
                return submitOrderResponseVo;
            }
        }
    }

    @Override
    public OrderEntity getByOrderSn(String orderSn) {
        return getOne(new LambdaQueryWrapper<OrderEntity>().eq(OrderEntity::getOrderSn, orderSn));
    }

    @Override
    public void closeOrder(OrderEntity orderEntity) {
        // 查询当前订单的最新状态
        OrderEntity orderEntityNew = getById(orderEntity.getId());
        OrderReleaseTo orderReleaseTo = new OrderReleaseTo();
        BeanUtils.copyProperties(orderEntityNew, orderReleaseTo);
        if (Objects.equals(orderEntityNew.getStatus(), OrderStatusEnum.CREATE_NEW.getCode())) {
            // 执行关单
            OrderEntity orderEntityUpdateStatus = new OrderEntity();
            orderEntityUpdateStatus.setId(orderEntity.getId());
            orderEntityUpdateStatus.setStatus(OrderStatusEnum.CANCLED.getCode());
            updateById(orderEntityUpdateStatus);
            // 给 MQ 发送消息
            rabbitTemplate.convertAndSend("order-event-exchange", "order.release.other", orderReleaseTo);
        }
    }

    /**
     * 获取当前订单的支付数据
     * @param orderSn
     * @return
     */
    @Override
    public PayVo getOrderPay(String orderSn) {
        PayVo payVo = new PayVo();
        OrderEntity orderEntity = getByOrderSn(orderSn);
        BigDecimal bigDecimal = orderEntity.getPayAmount().setScale(2, RoundingMode.HALF_UP);
        payVo.setTotal_amount(bigDecimal.toString());
        payVo.setOut_trade_no(orderSn);
        List<OrderItemEntity> orderItemEntities = orderItemService.list(new LambdaQueryWrapper<OrderItemEntity>().eq(OrderItemEntity::getOrderSn, orderSn));
        OrderItemEntity orderItemEntity = orderItemEntities.get(0);
        payVo.setSubject(orderItemEntity.getSkuName());
        payVo.setBody(orderItemEntity.getSkuAttrsVals());
        return payVo;
    }

    /**
     * 保存订单数据
     */
    private void saveOrder(OrderCreateTo orderCreateTo) {
        OrderEntity orderEntity = orderCreateTo.getOrder();
        orderEntity.setModifyTime(new Date());
        save(orderEntity);
        List<OrderItemEntity> orderItemEntities = orderCreateTo.getOrderItems();
        orderItemService.saveBatch(orderItemEntities);
    }

    private OrderCreateTo creatOrder() {
        OrderCreateTo orderCreateTo = new OrderCreateTo();
        // 1. 生成订单号
        String orderSn = IdWorker.getTimeId().substring(0, 32);
        // 创建订单号
        OrderEntity orderEntity = buildOrder(orderSn);

        // 2. 获取到所有的订单项
        List<OrderItemEntity> orderItemEntities = buildOrderItems(orderSn);

        // 3. 计算价格、积分等信息
        computePrice(orderEntity, orderItemEntities);

        orderCreateTo.setOrder(orderEntity);
        orderCreateTo.setOrderItems(orderItemEntities);
        return orderCreateTo;
    }

    private void computePrice(OrderEntity orderEntity, List<OrderItemEntity> orderItemEntities) {
        BigDecimal totalPrice = new BigDecimal("0.0");
        BigDecimal totalPromotionAmount = new BigDecimal("0.0");
        BigDecimal totalCouponAmount = new BigDecimal("0.0");
        BigDecimal totalIntegrationAmount = new BigDecimal("0.0");
        // 积分
        BigDecimal totalGift = new BigDecimal("0.0");
        BigDecimal totalGrowth = new BigDecimal("0.0");
        // 订单总额，叠加每一个订单项的总额
        for (OrderItemEntity orderItemEntity : orderItemEntities) {
            BigDecimal realAmount = orderItemEntity.getRealAmount();
            totalPromotionAmount = totalPromotionAmount.add(orderItemEntity.getPromotionAmount());
            totalCouponAmount = totalCouponAmount.add(orderItemEntity.getCouponAmount());
            totalIntegrationAmount = totalIntegrationAmount.add(orderItemEntity.getIntegrationAmount());
            totalPrice = totalPrice.add(realAmount);
            totalGift = totalGift.add(BigDecimal.valueOf(orderItemEntity.getGiftIntegration()));
            totalGrowth = totalGrowth.add(BigDecimal.valueOf(orderItemEntity.getGiftGrowth()));
        }
        // 1. 订单价格相关
        orderEntity.setTotalAmount(totalPrice);
        // 应付总额
        orderEntity.setPayAmount(totalPrice.add(orderEntity.getFreightAmount()));
        orderEntity.setPromotionAmount(totalPromotionAmount);
        orderEntity.setCouponAmount(totalCouponAmount);
        orderEntity.setIntegrationAmount(totalIntegrationAmount);

        // 设置积分等信息
        orderEntity.setIntegration(totalGift.intValue());
        orderEntity.setGrowth(totalGrowth.intValue());

        // 设置订单删除状态
        orderEntity.setDeleteStatus(0); // 未删除
    }

    private OrderEntity buildOrder(String orderSn) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(orderSn);
        orderEntity.setMemberId(LoginUserInterceptor.threadLocal.get().getId());
        orderEntity.setMemberUsername(LoginUserInterceptor.threadLocal.get().getUsername());
        // 获取收获地址信息
        OrderSubmitVo orderSubmitVo = threadLocal.get();
        R fare = wareFeignService.getFare(orderSubmitVo.getAddrId());
        FareVo fareVo = fare.getData(new TypeReference<FareVo>() {
        });
        // 设置运费
        orderEntity.setFreightAmount(fareVo.getFare());
        // 设置收获人信息
        orderEntity.setReceiverProvince(fareVo.getAddress().getProvince());
        orderEntity.setReceiverCity(fareVo.getAddress().getCity());
        orderEntity.setReceiverRegion(fareVo.getAddress().getRegion());
        orderEntity.setReceiverDetailAddress(fareVo.getAddress().getDetailAddress());
        orderEntity.setReceiverName(fareVo.getAddress().getName());
        orderEntity.setReceiverPhone(fareVo.getAddress().getPhone());
        orderEntity.setReceiverPostCode(fareVo.getAddress().getPostCode());

        // 设置订单的相关状态信息
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        orderEntity.setAutoConfirmDay(7);



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
        return Collections.emptyList();
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
        orderItemEntity.setGiftGrowth(cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getCount())).intValue());
        orderItemEntity.setGiftIntegration(cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getCount())).intValue());
        // 5. 订单项价格信息
        orderItemEntity.setPromotionAmount(new BigDecimal("0.0"));
        orderItemEntity.setCouponAmount(new BigDecimal("0.0"));
        orderItemEntity.setIntegrationAmount(new BigDecimal("0.0"));
        // 当前订单项的实际金额
        BigDecimal originPrice = orderItemEntity.getSkuPrice().multiply(BigDecimal.valueOf(orderItemEntity.getSkuQuantity()));
        BigDecimal newPrice = originPrice.subtract(orderItemEntity.getPromotionAmount())
                .subtract(orderItemEntity.getCouponAmount())
                .subtract(orderItemEntity.getIntegrationAmount());
        orderItemEntity.setRealAmount(newPrice);
        return orderItemEntity;
    }

}



