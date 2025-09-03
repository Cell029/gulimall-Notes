package com.project.gulimall.order.feign;

import com.project.common.to.CartItemConfirmTo;
import com.project.gulimall.order.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient("gulimall-cart")
public interface CartFeignService {
    @GetMapping("/{userId}/getCurrentUserCartItems")
    List<OrderItemVo> getCurrentUserCartItems(@PathVariable Long userId);
}
