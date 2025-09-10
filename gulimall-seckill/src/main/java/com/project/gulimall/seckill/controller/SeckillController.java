package com.project.gulimall.seckill.controller;

import com.project.common.utils.R;
import com.project.gulimall.seckill.domain.to.SeckillSkuRedisTo;
import com.project.gulimall.seckill.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    /**
     * 返回当前时间可以参与秒杀活动的商品信息
     */
    @GetMapping("/currentSeckillSkus")
    public R getCurrentSeckillSkus() {
        List<SeckillSkuRedisTo> seckillSkuRedisTos = seckillService.getCurrentSeckillSkus();
        return R.ok().setData("seckillSkuRedisTos", seckillSkuRedisTos);
    }

    @GetMapping("/sku/seckill/{skuId}")
    public R getSkuSeckillInfo(@PathVariable Long skuId) {
        SeckillSkuRedisTo seckillSkuRedisTo = seckillService.getSkuSeckillInfo(skuId);
        return R.ok().setData("seckillSkuRedisTo", seckillSkuRedisTo);
    }
}





