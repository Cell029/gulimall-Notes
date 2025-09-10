package com.project.gulimall.seckill.scheduled;

import com.project.gulimall.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


/**
 * 秒杀商品定时上架
 */
@Slf4j
@Service
@EnableScheduling
public class SeckillSkuScheduled {

    @Autowired
    private SeckillService seckillService;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 每天晚上 3:00 上架最近三天需要秒杀的商品
     * 当天 00:00:00 - 23:59:59
     * 明天 00:00:00 - 23:59:59
     * 后天 00:00:00 - 23:59:59
     */
    @Scheduled(cron = "*/5 * * * * ?")
    public void uploadSeckillSkuLatest3Days() {
        // 1. 重复上架无需处理
        log.info("上架秒杀商品...");
        // 添加分布式锁
        String upload_lock = "seckill:upload:lock";
        RLock rLock = redissonClient.getLock(upload_lock);
        rLock.lock(10, TimeUnit.SECONDS);
        try {
            seckillService.uploadSeckillSkuLatest3Days();
        } finally {
            rLock.unlock();
        }
    }
}
