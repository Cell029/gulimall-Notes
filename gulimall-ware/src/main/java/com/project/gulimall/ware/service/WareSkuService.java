package com.project.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.common.to.mq.StockLockedTo;
import com.project.common.utils.PageUtils;
import com.project.gulimall.ware.domain.entity.WareSkuEntity;
import com.project.gulimall.ware.domain.vo.LockStockResult;
import com.project.gulimall.ware.domain.vo.OrderLockResult;
import com.project.gulimall.ware.domain.vo.SkuHasStockVo;
import com.project.gulimall.ware.domain.vo.WareSkuLockVo;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);

    List<SkuHasStockVo> getSkusHaveStock(List<Long> skuIds);

    // OrderLockResult orderLockStock(WareSkuLockVo wareSkuLockVo);

    void unLockStock(StockLockedTo stockLockedTo);

    Boolean orderLockStockWithMessage(WareSkuLockVo wareSkuLockVo);

}

