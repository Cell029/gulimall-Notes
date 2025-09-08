package com.project.gulimall.ware.feign;

import com.project.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("gulimall-order")
public interface OrderFeignService {
    @GetMapping(value = "/order/order/status/{orderSn}",  produces = "application/json")
    R getOrderStatus(@PathVariable String orderSn);
}
