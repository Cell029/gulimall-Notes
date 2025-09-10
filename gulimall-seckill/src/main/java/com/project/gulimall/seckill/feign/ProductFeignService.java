package com.project.gulimall.seckill.feign;

import com.project.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "gulimall-product", path = "/product/skuinfo")
public interface ProductFeignService {
    @RequestMapping("/info/{skuId}")
    R getSkuInfo(@PathVariable("skuId") Long skuId);
}
