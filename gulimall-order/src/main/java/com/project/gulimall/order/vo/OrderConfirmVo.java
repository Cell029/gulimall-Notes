package com.project.gulimall.order.vo;

import com.project.common.to.CartItemConfirmTo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderConfirmVo {
    // 收货地址，表 ums_member_receive_address
    private List<MemberAddressVo> address;

    // 所有选中的购物项
    private List<OrderItemVo> items;

    // 积分
    private Integer integration;

    // 订单总额
    private BigDecimal total;

    public BigDecimal getTotal() {
        BigDecimal bigDecimal = new BigDecimal("0.00");
        if(!items.isEmpty()) {
           for (OrderItemVo item : items) {
               bigDecimal = bigDecimal.add(item.getPrice().multiply(new BigDecimal(item.getCount().toString())));
           }
        }
        return bigDecimal;
    }

    // 应付价格
    private BigDecimal payPrice;

    public BigDecimal getPayPrice() {
        return getTotal();
    }

    // 防重令牌
    private String orderToken;
}
