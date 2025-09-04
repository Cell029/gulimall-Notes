package com.project.gulimall.order.domain.to;

import com.project.gulimall.order.domain.entity.OrderEntity;
import com.project.gulimall.order.domain.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderCreateTo {
    private OrderEntity order;
    private List<OrderItemEntity> orderItems;
}
