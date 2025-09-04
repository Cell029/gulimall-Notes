package com.project.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.common.utils.PageUtils;
import com.project.gulimall.order.domain.entity.OrderSettingEntity;

import java.util.Map;

/**
 * 订单配置信息
 */
public interface OrderSettingService extends IService<OrderSettingEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

