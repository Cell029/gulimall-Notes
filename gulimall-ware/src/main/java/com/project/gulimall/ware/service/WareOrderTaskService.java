package com.project.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.common.utils.PageUtils;
import com.project.gulimall.ware.domain.entity.WareOrderTaskEntity;

import java.util.Map;

/**
 * 库存工作单
 */
public interface WareOrderTaskService extends IService<WareOrderTaskEntity> {

    PageUtils queryPage(Map<String, Object> params);

    WareOrderTaskEntity getByOrderSn(String orderSn);

    WareOrderTaskEntity getOrderTaskByOrderSn(String orderSn);

}

