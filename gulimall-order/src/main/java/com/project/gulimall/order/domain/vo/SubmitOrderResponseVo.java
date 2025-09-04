package com.project.gulimall.order.domain.vo;

import com.project.gulimall.order.domain.entity.OrderEntity;
import lombok.Data;

@Data
public class SubmitOrderResponseVo {
    private OrderEntity order;
    private Integer code; // 0 成功
}
