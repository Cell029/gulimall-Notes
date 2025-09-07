package com.project.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.common.utils.PageUtils;
import com.project.gulimall.order.domain.entity.OrderEntity;
import com.project.gulimall.order.domain.vo.OrderConfirmVo;
import com.project.gulimall.order.domain.vo.OrderSubmitVo;
import com.project.gulimall.order.domain.vo.SubmitOrderResponseVo;

import java.util.Map;

/**
 * 订单
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    OrderConfirmVo confirmOrder();

    SubmitOrderResponseVo submitOrder(OrderSubmitVo orderSubmitVo);

    OrderEntity getByOrderSn(String orderSn);
}

