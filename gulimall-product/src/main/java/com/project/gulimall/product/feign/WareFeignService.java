package com.project.gulimall.product.feign;

import com.project.common.utils.R;
import com.project.gulimall.product.domain.vo.SkuHasStockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("gulimall-ware")
public interface WareFeignService {

    @GetMapping("/ware/waresku/haveStock")
    R<List<SkuHasStockVo>> getSkusHaveStock(@RequestBody List<Long> skuIds);

}


