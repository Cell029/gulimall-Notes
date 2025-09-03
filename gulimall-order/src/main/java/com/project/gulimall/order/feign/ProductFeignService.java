package com.project.gulimall.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@FeignClient("gulimall-product")
public interface ProductFeignService {

}
