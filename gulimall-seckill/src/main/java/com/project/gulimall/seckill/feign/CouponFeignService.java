package com.project.gulimall.seckill.feign;

import com.project.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "gulimall-coupon", path = "/coupon/seckillsession")
public interface CouponFeignService {
    @GetMapping("/latest3DaySession")
    R getLatest3DaySession();
}
