package com.project.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.common.utils.PageUtils;
import com.project.gulimall.ware.domain.entity.PurchaseEntity;
import com.project.gulimall.ware.domain.vo.MergeVo;
import com.project.gulimall.ware.domain.vo.PurchaseDoneVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageUnreceive(Map<String, Object> params);

    void mergePurchase(MergeVo mergeVo);

    void received(List<Long> purchaseIds);

    void done(PurchaseDoneVo purchaseDoneVo);
}

