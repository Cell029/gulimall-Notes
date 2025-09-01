package com.project.gulimall.cart.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.common.constant.CartConstant;
import com.project.common.utils.R;
import com.project.gulimall.cart.domain.to.LoginUserInfoTo;
import com.project.gulimall.cart.domain.vo.Cart;
import com.project.gulimall.cart.domain.vo.CartItem;
import com.project.gulimall.cart.domain.vo.SkuInfoVo;
import com.project.gulimall.cart.feign.ProductFeignService;
import com.project.gulimall.cart.interceptor.CartInterceptor;
import com.project.gulimall.cart.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Slf4j
@Service("cartService")
public class CartServiceImpl implements CartService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ProductFeignService productFeignService;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;
    @Autowired
    private ObjectMapper objectMapper;


    @Override
    public CartItem addToCart(Long skuId, Integer num) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        // 判断 Redis 中是否有相同的商品
        String redisSku = (String) cartOps.get(skuId.toString());
        if (StringUtils.isEmpty(redisSku)) {
            // 购物车中没有此商品
            CartItem cartItem = new CartItem();
            // 1. 远程查询当前要添加的商品信息
            CompletableFuture<Void> getSkuInfoFuture = CompletableFuture.runAsync(() -> {
                R r = productFeignService.getSkuInfo(skuId);
                if (r.getCode() == 0) {
                    SkuInfoVo skuInfo = r.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                    });
                    cartItem.setSkuId(skuId);
                    cartItem.setCheck(true);
                    cartItem.setCount(num);
                    cartItem.setImage(skuInfo.getSkuDefaultImg());
                    cartItem.setTitle(skuInfo.getSkuTitle());
                    cartItem.setPrice(skuInfo.getPrice());
                }
            }, threadPoolExecutor);

            // 2. 远程查询 sku 组合信息
            CompletableFuture<Void> getSkuSaleAttrValueListFuture = CompletableFuture.runAsync(() -> {
                List<String> skuSaleAttrValueList = productFeignService.getSkuSaleAttrValueList(skuId);
                cartItem.setSkuAttr(skuSaleAttrValueList);
            }, threadPoolExecutor);
            // 等待异步任务完成
            try {
                CompletableFuture.allOf(getSkuInfoFuture, getSkuSaleAttrValueListFuture).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }

            // 3. 将 CartItem 对象写进 Redis
            try {
                cartOps.put(skuId.toString(), objectMapper.writeValueAsString(cartItem));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return cartItem;
        } else {
            // 有此商品，修改数量即可
            CartItem cartItem;
            try {
                cartItem = objectMapper.readValue(redisSku, CartItem.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            cartItem.setCount(cartItem.getCount() + num);
            try {
                cartOps.put(skuId.toString(), objectMapper.writeValueAsString(cartItem));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return cartItem;
        }
    }

    @Override
    public CartItem getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String redisSku = (String) cartOps.get(skuId.toString());
        CartItem cartItem;
        try {
            cartItem = objectMapper.readValue(redisSku, CartItem.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return cartItem;
    }

    @Override
    public Cart getCart() {
        Cart cart = new Cart();
        LoginUserInfoTo userInfo = CartInterceptor.threadLocal.get();
        String tempCartKey = CartConstant.CART_PREFIX + userInfo.getUserKey();
        // 最终返回的购物车数据
        List<CartItem> cartItems = new ArrayList<>();
        if (userInfo.getUserId() != null) {
            // 登录用户
            String userCartKey = CartConstant.CART_PREFIX + userInfo.getUserId();
            // 1. 获取临时购物车
            List<CartItem> tempItems = getCartItems(tempCartKey);
            // 2. 如果临时购物车有数据，就合并到登录购物车
            if (tempItems != null) {
                for (CartItem item : tempItems) {
                    addToCart(item.getSkuId(), item.getCount());
                }
                // 3. 清空临时购物车
                stringRedisTemplate.delete(tempCartKey);
            }
            // 4. 查询合并后的登录购物车
            cartItems = getCartItems(userCartKey);
        } else {
            // 未登录，直接查临时购物车
            cartItems = getCartItems(tempCartKey);
        }
        cart.setCartItems(cartItems);
        return cart;
    }

    private List<CartItem> getCartItems(String cartKey) {
        BoundHashOperations<String, Object, Object> ops = stringRedisTemplate.boundHashOps(cartKey);
        List<Object> values = ops.values();
        if (values == null || values.isEmpty()) return new ArrayList<>();
        return values.stream()
                .filter(Objects::nonNull) // 过滤掉 null
                .map(obj -> {
            try {
                return objectMapper.readValue((String) obj, CartItem.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }


    /**
     * 获取要操作的购物车
     */
    private BoundHashOperations<String, Object, Object> getCartOps() {
        // 1. 判断是否为登录用户进行购物车操作
        LoginUserInfoTo loginUserInfoTo = CartInterceptor.threadLocal.get();
        String cartKey = "";
        if (loginUserInfoTo.getUserId() != null) {
            // 已登录
            cartKey = CartConstant.CART_PREFIX + loginUserInfoTo.getUserId();
        } else {
            cartKey = CartConstant.CART_PREFIX + loginUserInfoTo.getUserKey();
        }
        // 2. 绑定 redis 操作的 key
        return stringRedisTemplate.boundHashOps(cartKey);
    }
}
