package com.project.gulimall.order.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class OrderConfirmVo {
    // 收货地址，表 ums_member_receive_address
    private List<MemberAddressVo> address;

    // 所有选中的购物项
    private List<OrderItemVo> items;

    // 积分
    private Integer integration;

    // 库存
    private Map<Long, Boolean> stocks;

    // 商品总数
    private Integer count;

    public Integer getCount() {
        count = 0;
        if(!items.isEmpty()) {
            for (OrderItemVo item : items) {
                count += item.getCount();
            }
        }
        return count;
    }

    // 订单总额
    private BigDecimal total;

    public BigDecimal getTotal() {
        total = new BigDecimal("0.00");
        if(!items.isEmpty()) {
           for (OrderItemVo item : items) {
               total = total.add(item.getPrice().multiply(new BigDecimal(item.getCount().toString())));
           }
        }
        return total;
    }

    // 应付价格
    private BigDecimal payPrice;

    public BigDecimal getPayPrice() {
        payPrice = getTotal();
        return payPrice;
    }

    // 防重令牌
    private String orderToken;
}
