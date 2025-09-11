package com.project.gulimall.seckill.config;

import com.project.common.exception.BizCodeEnum;
import com.project.common.utils.R;
import com.project.gulimall.seckill.feign.CouponFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SeckillFeignServiceFallBack implements CouponFeignService {
    @Override
    public R getLatest3DaySession() {
        log.info("熔断方法调用...getLatest3DaySession");
        return R.error(BizCodeEnum.TOO_MANY_REQUEST.getCode(),BizCodeEnum.TOO_MANY_REQUEST.getMsg());
    }
}
