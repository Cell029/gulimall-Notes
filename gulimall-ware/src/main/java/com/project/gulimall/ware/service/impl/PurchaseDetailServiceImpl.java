package com.project.gulimall.ware.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.common.utils.PageUtils;
import com.project.common.utils.Query;

import com.project.gulimall.ware.dao.PurchaseDetailDao;
import com.project.gulimall.ware.domain.entity.PurchaseDetailEntity;
import com.project.gulimall.ware.service.PurchaseDetailService;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<PurchaseDetailEntity> queryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and((wrapper) -> {
                wrapper.eq("purchase_id", key).or().eq("sku_id", key);
            });
        }
        String status = (String) params.get("status");
        if (!StringUtils.isEmpty(status)) {
            queryWrapper.eq("status", status);
        }
        String wareId = (String) params.get("wareId");
        if (!StringUtils.isEmpty(wareId) && !"0".equals(wareId)) {
            queryWrapper.eq("ware_id", wareId);
        }
        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils(page);
    }

    @Override
    public List<PurchaseDetailEntity> listDetailByPurchaseId(Long purchaseId) {
        List<PurchaseDetailEntity> purchaseDetailEntities = this.list(new QueryWrapper<PurchaseDetailEntity>().eq("purchase_id", purchaseId));
        return purchaseDetailEntities;
    }

}