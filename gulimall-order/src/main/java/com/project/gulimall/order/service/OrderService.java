package com.project.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.common.utils.PageUtils;
import com.project.gulimall.order.entity.OrderEntity;
import com.project.gulimall.order.vo.OrderConfirmVo;

import java.util.Map;

/**
 * 订单
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    OrderConfirmVo confirmOrder();

}

