package com.project.gulimall.ware.service.impl;

import com.project.common.constant.WareConstant;
import com.project.gulimall.ware.domain.entity.PurchaseDetailEntity;
import com.project.gulimall.ware.domain.vo.MergeVo;
import com.project.gulimall.ware.domain.vo.PurchaseDoneVo;
import com.project.gulimall.ware.domain.vo.PurchaseItemDoneVo;
import com.project.gulimall.ware.service.PurchaseDetailService;
import com.project.gulimall.ware.service.WareSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.common.utils.PageUtils;
import com.project.common.utils.Query;
import com.project.gulimall.ware.dao.PurchaseDao;
import com.project.gulimall.ware.domain.entity.PurchaseEntity;
import com.project.gulimall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    private PurchaseDetailService purchaseDetailService;
    @Autowired
    private WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnreceive(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().eq("status", 0).or().eq("status", 1)
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void mergePurchase(MergeVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();
        // 如果没有采购单 id，则需要新建采购单
        if (purchaseId == null) {
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
            this.save(purchaseEntity);
            // mybatis-plus 会自动把主键 id 返回给实体类上标注了 @TableId(type = IdType.AUTO) 的字段
            purchaseId = purchaseEntity.getId();
        }
        // 确认采购单状态，只有状态是新建或已分配才可以合并
        PurchaseEntity purchaseEntity = this.getById(purchaseId);
        if (purchaseEntity.getStatus().equals(WareConstant.PurchaseStatusEnum.CREATED.getCode())
                || purchaseEntity.getStatus().equals(WareConstant.PurchaseStatusEnum.ASSIGN.getCode())) {
            List<Long> itemIds = mergeVo.getItems();
            if (itemIds != null && !itemIds.isEmpty()) {
                Long finalPurchaseId = purchaseId;
                List<PurchaseDetailEntity> purchaseDetailEntities = itemIds.stream().map(itemId -> {
                    PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
                    purchaseDetailEntity.setId(itemId);
                    purchaseDetailEntity.setPurchaseId(finalPurchaseId);
                    purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGN.getCode());
                    return purchaseDetailEntity;
                }).collect(Collectors.toList());
                purchaseDetailService.updateBatchById(purchaseDetailEntities);
            }
        }
    }

    @Transactional
    @Override
    public void received(List<Long> purchaseIds) {
        // 1. 确认当前采购单是新建或者已分配状态
        List<PurchaseEntity> purchaseEntities = purchaseIds.stream().map(purchaseId -> {
            PurchaseEntity purchaseEntity = this.getById(purchaseId);
            return purchaseEntity;
        }).filter(purchaseEntity -> {
            return purchaseEntity.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode() || purchaseEntity.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGN.getCode();
        }).map(purchaseEntity -> {
            Date now = new Date();
            purchaseEntity.setUpdateTime(now);
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode());
            return purchaseEntity;
        }).collect(Collectors.toList());
        if (purchaseEntities.isEmpty()) {
            return; // 没有可处理的采购单
        }
        // 2. 改变采购单的状态
        this.updateBatchById(purchaseEntities);
        // 3. 改变采购需求的状态
        purchaseEntities.forEach(purchaseEntity -> {
            List<PurchaseDetailEntity> purchaseDetailEntities = purchaseDetailService.listDetailByPurchaseId(purchaseEntity.getId());
            purchaseDetailEntities.forEach(purchaseDetailEntity -> {
                purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
            });
            purchaseDetailService.updateBatchById(purchaseDetailEntities);
        });
    }

    @Override
    public void done(PurchaseDoneVo purchaseDoneVo) {
        Long purchaseId = purchaseDoneVo.getPurchaseId();
        // 2. 改变采购需求状态
        Boolean flag = true;
        List<PurchaseItemDoneVo> items = purchaseDoneVo.getItems();
        List<PurchaseDetailEntity> purchaseDetailEntities = new ArrayList<>();
        for (PurchaseItemDoneVo item : items) {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            // 采购失败
            if (item.getStatus() == WareConstant.PurchaseDetailStatusEnum.ERROR.getCode()) {
                flag = false;
                purchaseDetailEntity.setStatus(item.getStatus());
            } else { // 采购成功
                purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.FINISH.getCode());
                // 3. 将成功采购的商品进行入库（加库存数量）
                PurchaseDetailEntity entity = purchaseDetailService.getById(item.getPurchaseDetailId());
                wareSkuService.addStock(entity.getSkuId(), entity.getWareId(), entity.getSkuNum());
            }
            purchaseDetailEntity.setId(item.getPurchaseDetailId());
            purchaseDetailEntities.add(purchaseDetailEntity);
        }
        purchaseDetailService.updateBatchById(purchaseDetailEntities);
        // 1. 改变采购单状态
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(purchaseId);
        purchaseEntity.setStatus(flag ? WareConstant.PurchaseStatusEnum.FINISH.getCode() : WareConstant.PurchaseStatusEnum.ERROR.getCode());
        this.updateById(purchaseEntity);
    }

}