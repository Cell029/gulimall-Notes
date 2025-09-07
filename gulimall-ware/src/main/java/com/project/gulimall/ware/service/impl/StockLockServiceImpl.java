package com.project.gulimall.ware.service.impl;

import com.project.common.exception.NoStockException;
import com.project.common.to.mq.StockDetailTo;
import com.project.common.to.mq.StockLockedTo;
import com.project.gulimall.ware.dao.WareSkuDao;
import com.project.gulimall.ware.domain.entity.WareOrderTaskDetailEntity;
import com.project.gulimall.ware.domain.entity.WareOrderTaskEntity;
import com.project.gulimall.ware.domain.vo.OrderItemVo;
import com.project.gulimall.ware.domain.vo.OrderLockResult;
import com.project.gulimall.ware.domain.vo.SkuWareHasStock;
import com.project.gulimall.ware.domain.vo.WareSkuLockVo;
import com.project.gulimall.ware.service.StockLockService;
import com.project.gulimall.ware.service.WareOrderTaskDetailService;
import com.project.gulimall.ware.service.WareOrderTaskService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockLockServiceImpl implements StockLockService {

    @Autowired
    private WareOrderTaskService wareOrderTaskService;
    @Autowired
    private WareOrderTaskDetailService wareOrderTaskDetailService;
    @Autowired
    private WareSkuDao wareSkuDao;

    @Transactional
    @Override
    public OrderLockResult orderLockStock(WareSkuLockVo wareSkuLockVo) {

        List<StockLockedTo> pendingMessages = new ArrayList<>(); // 收集消息数据
        /**
         * 保存库存工作单详情
         */
        WareOrderTaskEntity wareOrderTaskEntity = new WareOrderTaskEntity();
        wareOrderTaskEntity.setOrderSn(wareSkuLockVo.getOrderSn());
        wareOrderTaskService.save(wareOrderTaskEntity);

        // 1. 找到每个商品在哪个仓库都有库存
        List<OrderItemVo> lockItems = wareSkuLockVo.getLockItems();
        List<SkuWareHasStock> skuWareHasStocks = lockItems.stream().map(lockItem -> {
            SkuWareHasStock skuWareHasStock = new SkuWareHasStock();
            Long skuId = lockItem.getSkuId();
            skuWareHasStock.setSkuId(skuId);
            skuWareHasStock.setNum(lockItem.getCount());
            List<Long> wareIds = wareSkuDao.listWareIdHasSkuStock(skuId);
            skuWareHasStock.setWareIds(wareIds);
            return skuWareHasStock;
        }).collect(Collectors.toList());
        // 2. 锁定库存
        for (SkuWareHasStock skuWareHasStock : skuWareHasStocks) {
            Boolean skuStockLocked = false;
            Long skuId = skuWareHasStock.getSkuId();
            List<Long> wareIds = skuWareHasStock.getWareIds();
            if (wareIds.isEmpty()) {
                throw new NoStockException(skuId);
            }

            for (Long wareId : wareIds) {
                Long count = wareSkuDao.lockSkuStock(skuId, wareId, skuWareHasStock.getNum());
                if (count == 1) {
                    // 库存锁定成功
                    skuStockLocked = true;
                    // 保存当前商品的库存操作单详情
                    WareOrderTaskDetailEntity wareOrderTaskDetailEntity = new WareOrderTaskDetailEntity(null, skuId, "", skuWareHasStock.getNum(), wareOrderTaskEntity.getId(), wareId, 1);
                    wareOrderTaskDetailService.save(wareOrderTaskDetailEntity);

                    StockLockedTo stockLockedTo = new StockLockedTo();
                    stockLockedTo.setTaskId(wareOrderTaskEntity.getId());
                    StockDetailTo stockDetailTo = new StockDetailTo();
                    BeanUtils.copyProperties(wareOrderTaskDetailEntity, stockDetailTo);
                    stockLockedTo.setStockDetailTo(stockDetailTo);
                    pendingMessages.add(stockLockedTo);
                    break;
                } else {
                    // 当前仓库库存锁定失败，尝试下一个仓库
                }
            }
            if (!skuStockLocked) {
                // 当前仓库都库存不足，没法锁住
                throw new NoStockException(skuId);
            }
        }
        // 3. 锁定成功
        return new OrderLockResult(true, pendingMessages);
    }
}
