package com.project.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.common.utils.PageUtils;
import com.project.gulimall.ware.domain.entity.PurchaseDetailEntity;

import java.util.List;
import java.util.Map;

/**
 * 
 */
public interface PurchaseDetailService extends IService<PurchaseDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<PurchaseDetailEntity> listDetailByPurchaseId(Long purchaseId);
}

