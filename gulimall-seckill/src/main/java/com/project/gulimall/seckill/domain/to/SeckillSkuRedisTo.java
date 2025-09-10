package com.project.gulimall.seckill.domain.to;

import com.project.gulimall.seckill.domain.vo.SkuInfoVo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class SeckillSkuRedisTo {
    /**
     * 活动id
     */
    private Long promotionId;
    /**
     * 活动场次id
     */
    private Long promotionSessionId;
    /**
     * 商品id
     */
    private Long skuId;
    /**
     * 秒杀价格
     */
    private BigDecimal seckillPrice;
    /**
     * 秒杀总量
     */
    private BigDecimal seckillCount;
    /**
     * 每人限购数量
     */
    private BigDecimal seckillLimit;
    /**
     * 排序
     */
    private Integer seckillSort;
    /**
     * 秒杀开始结束时间
     */
    private Long startTime;
    private Long endTime;
    /**
     * 秒杀随机码
     */
    private String randomCode;
    /**
     * sku 详细信息
     */
    private SkuInfoVo skuInfo;
}
