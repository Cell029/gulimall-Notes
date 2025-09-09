package com.project.gulimall.member.feign;

import com.project.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient("gulimall-order")
public interface OrderFeignService {
    @GetMapping("/order/order/listWithItem")
    R listWithItem(@RequestParam Map<String, Object> params);
}
