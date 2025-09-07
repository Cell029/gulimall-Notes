package com.project.gulimall.ware.service;

import com.project.gulimall.ware.domain.vo.OrderLockResult;
import com.project.gulimall.ware.domain.vo.WareSkuLockVo;

public interface StockLockService {
    OrderLockResult orderLockStock(WareSkuLockVo wareSkuLockVo);
}
