package com.project.gulimall.order.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class WareSkuLockVo {
    private String orderSn; // 订单号
    private List<OrderItemVo> lockItems; // 要锁住的所有库存商品信息
}
