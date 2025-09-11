package com.project.gulimall.seckill.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.api.utils.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.common.domain.vo.MemberResponseVo;
import com.project.common.to.mq.SeckillOrderTo;
import com.project.common.utils.R;
import com.project.gulimall.seckill.domain.to.SeckillSkuRedisTo;
import com.project.gulimall.seckill.domain.vo.SeckillSessionWithSkus;
import com.project.gulimall.seckill.domain.vo.SeckillVo;
import com.project.gulimall.seckill.domain.vo.SkuInfoVo;
import com.project.gulimall.seckill.feign.CouponFeignService;
import com.project.gulimall.seckill.feign.ProductFeignService;
import com.project.gulimall.seckill.interceptor.LoginUserInterceptor;
import com.project.gulimall.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private CouponFeignService couponFeignService;
    @Autowired
    private ProductFeignService productFeignService;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final String SESSION_CACHE_PREFIX = "seckill:sessions:";
    private final String SKU_CACHE_PREFIX = "seckill:skus:";
    private final String SKU_STOCK_SEMAPHORE = "seckill:stock:"; // 后面拼接商品随机码

    @Override
    public void uploadSeckillSkuLatest3Days() {
        // 1. 扫描最近三天需要参与秒杀的活动
        try {
            R r = couponFeignService.getLatest3DaySession();
            if (r.getCode() == 0) {
                // 上架的商品数据
                List<SeckillSessionWithSkus> seckillSessionList = r.getData("seckillSession", new TypeReference<List<SeckillSessionWithSkus>>() {
                });
                // 缓存到 Redis
                // 1. 缓存活动信息
                saveSessionInfos(seckillSessionList);
                // 2. 缓存活动相关联的商品信息
                saveSessionSkuInfos(seckillSessionList);
            }
        } catch (Exception e) {
            log.error("秒杀商品上架异常", e);
            throw new RuntimeException("秒杀商品上架失败", e);
        }
    }

    /**
     * 当前时间可以参与活动的秒杀商品信息
     */
    @Override
    public List<SeckillSkuRedisTo> getCurrentSeckillSkus() {
        // 1. 确定当前时间属于哪个秒杀场次
        long now = new Date().getTime();
        // 查到 Redis 中所有场次的 key
        Set<String> keys = stringRedisTemplate.keys(SESSION_CACHE_PREFIX + "*");
        for (String key : keys) {
            String replace = key.replace(SESSION_CACHE_PREFIX, "");
            String[] timeInterval = replace.split("_");
            long startTime = Long.parseLong(timeInterval[0]);
            long endTime = Long.parseLong(timeInterval[1]);
            if (now >= startTime && now <= endTime) {
                // 2. 获取这个秒杀场次关联的所有秒杀商品信息
                List<String> range = stringRedisTemplate.opsForList().range(key, 0, -1);
                BoundHashOperations<String, String, String> boundHashOperations = stringRedisTemplate.boundHashOps(SKU_CACHE_PREFIX);
                List<String> objects = boundHashOperations.multiGet(range);
                if (objects != null && !objects.isEmpty()) {
                    List<SeckillSkuRedisTo> seckillSkuRedisTos = objects.stream().map(item -> {
                        try {
                            return objectMapper.readValue(item, SeckillSkuRedisTo.class);
                        } catch (JsonProcessingException e) {
                            log.error("秒杀商品反序列化失败", e);
                            return null;
                        }
                    }).collect(Collectors.toList());
                    return seckillSkuRedisTos;
                }
                break;
            }
        }
        return Collections.emptyList();
    }

    @Override
    public SeckillSkuRedisTo getSkuSeckillInfo(Long skuId) {
        // 1. 找到所有需要参与秒杀商品的 key
        BoundHashOperations<String, String, String> boundHashOperations = stringRedisTemplate.boundHashOps(SKU_CACHE_PREFIX);
        Set<String> keys = boundHashOperations.keys();
        if (keys != null && !keys.isEmpty()) {
            // String regx = "\\d_" + skuId;
            String target = "_" + skuId;
            for (String key : keys) {
                if (/*Pattern.matches(regx, key)*/key.contains(target)) {
                    String json = boundHashOperations.get(key);
                    SeckillSkuRedisTo seckillSkuRedisTo = null;
                    try {
                        seckillSkuRedisTo = objectMapper.readValue(json, SeckillSkuRedisTo.class);
                    } catch (JsonProcessingException e) {
                        log.error("获取秒杀商品失败：", e);
                    }
                    // 处理随机码
                    if (seckillSkuRedisTo != null) {
                        long now = new Date().getTime();
                        Long startTime = seckillSkuRedisTo.getStartTime();
                        Long endTime = seckillSkuRedisTo.getEndTime();
                        if (now < startTime || now > endTime) {
                            seckillSkuRedisTo.setRandomCode(null);
                        }
                        return seckillSkuRedisTo;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public String kill(String killId, String key, Integer num) {
        MemberResponseVo memberResponseVo = LoginUserInterceptor.threadLocal.get();

        // 获取当前秒杀商品的详细信息
        BoundHashOperations<String, String, String> boundHashOperations = stringRedisTemplate.boundHashOps(SKU_CACHE_PREFIX);
        String json = boundHashOperations.get(killId);
        if (!StringUtils.isEmpty(json)) {
            SeckillSkuRedisTo seckillSkuRedisTo = null;
            try {
                seckillSkuRedisTo = objectMapper.readValue(json, SeckillSkuRedisTo.class);
            } catch (JsonProcessingException e) {
                log.error("获取秒杀商品信息出错", e);
            }
            // 校验合法性
            if (seckillSkuRedisTo != null) {
                Long startTime = seckillSkuRedisTo.getStartTime();
                Long endTime = seckillSkuRedisTo.getEndTime();
                long now = new Date().getTime();
                long ttl = endTime - now;
                // 1. 校验时间是否在秒杀活动时间范围内
                if (now >= startTime && now <= endTime) {
                    // 2. 校验随机码是否正确
                    String randomCode = seckillSkuRedisTo.getRandomCode();
                    String sessionIdAndSkuId = seckillSkuRedisTo.getPromotionSessionId() + "_" + seckillSkuRedisTo.getSkuId();
                    if (randomCode.equals(key) && sessionIdAndSkuId.equals(killId)) {
                        // 3. 验证购物数量是否合理
                        if (num <= seckillSkuRedisTo.getSeckillLimit().intValue()) {
                            // 4. 验证该用户是否已经购买过，买过就在 Redis 中存储 userId_SessionId_skuId
                            String hasBoughtKey = memberResponseVo.getId() + "_" + seckillSkuRedisTo.getPromotionSessionId() + "_" + seckillSkuRedisTo.getSkuId();
                            // 设置自动过期时间，TTL 为当前时间和活动过期时间的时间差
                            Boolean b = stringRedisTemplate.opsForValue().setIfAbsent(hasBoughtKey, num.toString(), ttl, TimeUnit.MILLISECONDS);
                            if (b != null && b) {
                                // 占位成功，说明没购买过该秒杀商品
                                RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + randomCode);
                                boolean tryAcquire = semaphore.tryAcquire(num);
                                if (tryAcquire) {
                                    // 获取到信号量，证明秒杀成功
                                    // 发送 MQ 消息，让订单服务完成下单操作
                                    String orderSn = IdWorker.getTimeId().substring(0, 32);
                                    SeckillOrderTo seckillOrderTo = new SeckillOrderTo();
                                    seckillOrderTo.setOrderSn(orderSn);
                                    seckillOrderTo.setPromotionSessionId(seckillSkuRedisTo.getPromotionSessionId());
                                    seckillOrderTo.setMemberId(memberResponseVo.getId());
                                    seckillOrderTo.setNum(num);
                                    seckillOrderTo.setSkuId(seckillSkuRedisTo.getSkuId());
                                    seckillOrderTo.setSeckillPrice(seckillSkuRedisTo.getSeckillPrice());
                                    rabbitTemplate.convertAndSend("order-event-exchange", "order.seckill", seckillOrderTo);
                                    return orderSn;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    // 1. 缓存活动信息
    private void saveSessionInfos(List<SeckillSessionWithSkus> seckillSessionList) {
        seckillSessionList.forEach(SeckillSessionWithSkus -> {
            long startTime = SeckillSessionWithSkus.getStartTime().getTime();
            long endTime = SeckillSessionWithSkus.getEndTime().getTime();
            String key = SESSION_CACHE_PREFIX + startTime + "_" + endTime;
            Boolean hasKey = stringRedisTemplate.hasKey(key);
            if (!hasKey) {
                List<String> skuIds = SeckillSessionWithSkus.getRelationSkus().stream()
                        .map(skuItem -> skuItem.getPromotionSessionId() + "_" + skuItem.getSkuId().toString()).collect(Collectors.toList());
                // 活动信息作为 key，场次 id + 商品 id 作为 value
                stringRedisTemplate.opsForList().leftPushAll(key, skuIds);
            }

        });
    }

    // 2. 缓存活动相关联的商品信息
    private void saveSessionSkuInfos(List<SeckillSessionWithSkus> seckillSessionList) {
        seckillSessionList.forEach(SeckillSessionWithSkus -> {
            BoundHashOperations<String, Object, Object> ops = stringRedisTemplate.boundHashOps(SKU_CACHE_PREFIX);
            SeckillSessionWithSkus.getRelationSkus().forEach(skuItem -> {
                String randomCode = UUID.randomUUID().toString().replace("-", "");
                if (Boolean.FALSE.equals(ops.hasKey(skuItem.getPromotionSessionId() + "_" + skuItem.getSkuId().toString()))) {
                    // 缓存商品
                    SeckillSkuRedisTo seckillSkuRedisTo = new SeckillSkuRedisTo();
                    // 1. sku 的秒杀信息
                    BeanUtils.copyProperties(skuItem, seckillSkuRedisTo);
                    // 2. sku 的基本数据
                    R r = productFeignService.getSkuInfo(skuItem.getSkuId());
                    if (r.getCode() == 0) {
                        SkuInfoVo skuInfo = r.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                        });
                        seckillSkuRedisTo.setSkuInfo(skuInfo);
                    }
                    // 3. 设置当前商品的秒杀时间信息
                    seckillSkuRedisTo.setStartTime(SeckillSessionWithSkus.getStartTime().getTime());
                    seckillSkuRedisTo.setEndTime(SeckillSessionWithSkus.getEndTime().getTime());
                    // 4. 设置商品的随机码
                    seckillSkuRedisTo.setRandomCode(randomCode);
                    // 缓存数据
                    try {
                        String json = objectMapper.writeValueAsString(seckillSkuRedisTo);
                        ops.put(skuItem.getPromotionSessionId() + "_" + skuItem.getSkuId().toString(), json);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }

                    // 5. 设置信号量
                    RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + randomCode);
                    // 用商品的秒杀数量作为信号量
                    semaphore.trySetPermits(skuItem.getSeckillCount().intValue());
                }
            });
        });
    }


}
