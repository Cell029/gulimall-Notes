package com.project.gulimall.seckill.feign;

import com.project.common.utils.R;
import com.project.gulimall.seckill.config.SeckillFeignServiceFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "gulimall-coupon", path = "/coupon/seckillsession", fallback = SeckillFeignServiceFallBack.class)
public interface CouponFeignService {
    @GetMapping("/latest3DaySession")
    R getLatest3DaySession();
}
