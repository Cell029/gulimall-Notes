package com.project.gulimall.product.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.common.utils.PageUtils;
import com.project.common.utils.Query;

import com.project.gulimall.product.dao.ProductAttrValueDao;
import com.project.gulimall.product.domain.entity.ProductAttrValueEntity;
import com.project.gulimall.product.service.ProductAttrValueService;
import org.springframework.transaction.annotation.Transactional;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    @Autowired
    private ProductAttrValueDao productAttrValueDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveProductAttr(List<ProductAttrValueEntity> productAttrValueEntities) {
        this.saveBatch(productAttrValueEntities);
    }

    @Override
    public List<ProductAttrValueEntity> baseAttrListForSpu(Long spuId) {
        List<ProductAttrValueEntity> productAttrValueEntities = productAttrValueDao.selectList(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
        return productAttrValueEntities;
    }

    @Transactional
    @Override
    public void updateSpuAttr(Long spuId, List<ProductAttrValueEntity> productAttrValueEntities) {
        // 1. 删除该 spuId 之前对应的所有属性
        productAttrValueDao.delete(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
        // 2. 插入新的数据
        List<ProductAttrValueEntity> productAttrValueEntities1 = productAttrValueEntities.stream().map(productAttrValueEntity -> {
            productAttrValueEntity.setSpuId(spuId);
            return productAttrValueEntity;
        }).collect(Collectors.toList());
        this.saveBatch(productAttrValueEntities1);
    }

}