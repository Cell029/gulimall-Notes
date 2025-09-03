package com.project.gulimall.cart.feign;

import com.project.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@FeignClient("gulimall-product")
public interface ProductFeignService {
    @RequestMapping("/product/skuinfo/info/{skuId}")
    R getSkuInfo(@PathVariable("skuId") Long skuId);

    @GetMapping("/product/skusaleattrvalue/stringlist/{skuId}")
    List<String> getSkuSaleAttrValueList(@PathVariable("skuId") Long skuId);

    /**
     * 查询最新的商品价格
     */
    @GetMapping("/product/skuinfo/getCurrentCartItemPriceMap")
    Map<Long, BigDecimal> getCurrentCartItemPriceMap(@RequestParam("skuIds") List<Long> skuIds);
}
