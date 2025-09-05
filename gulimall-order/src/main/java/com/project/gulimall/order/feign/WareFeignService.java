package com.project.gulimall.order.feign;

import com.project.common.utils.R;
import com.project.gulimall.order.domain.vo.WareSkuLockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("gulimall-ware")
public interface WareFeignService {
    @GetMapping("/ware/waresku/haveStock")
    R getSkusHaveStock(@RequestParam("skuIds") List<Long> skuIds);

    @GetMapping("/ware/waresku/fare")
    R getFare(@RequestParam("addrId") Long addrId);

    @PostMapping("/ware/waresku/lock/order")
    R orderLockStock(@RequestBody WareSkuLockVo wareSkuLockVo);
}
