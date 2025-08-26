package com.project.gulimall.product.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.project.gulimall.product.domain.entity.SkuImagesEntity;
import com.project.gulimall.product.domain.entity.SpuInfoDescEntity;
import com.project.gulimall.product.domain.vo.SkuItemSaleAttrVo;
import com.project.gulimall.product.domain.vo.SkuItemVo;
import com.project.gulimall.product.domain.vo.SpuItemAttrGroupVo;
import com.project.gulimall.product.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.common.utils.PageUtils;
import com.project.common.utils.Query;
import com.project.gulimall.product.dao.SkuInfoDao;
import com.project.gulimall.product.domain.entity.SkuInfoEntity;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    private SkuInfoDao skuInfoDao;
    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private SpuInfoDescService spuInfoDescService;
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        this.save(skuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> queryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and(wrapper -> {
                queryWrapper.eq("sku_id", key).or().like("sku_name", key);
            });
        }
        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId) && !"0".equals(catelogId)) {
            queryWrapper.eq("catalog_id", catelogId);
        }
        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equals(brandId)) {
            queryWrapper.eq("brand_id", brandId);
        }
        String min = (String) params.get("min");
        if (!StringUtils.isEmpty(min)) {
            BigDecimal bigDecimal = new BigDecimal(min);
            if (bigDecimal.compareTo(new BigDecimal("0")) > 0) {
                // 大于等于
                queryWrapper.ge("price", min);
            }
        }
        String max = (String) params.get("max");
        if (!StringUtils.isEmpty(max)) {
            BigDecimal bigDecimal = new BigDecimal(max);
            if (bigDecimal.compareTo(new BigDecimal("0")) > 0) {
                // 小于等于
                queryWrapper.le("price", max);
            }
        }
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getSkusBySpuId(Long spuId) {
        return this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
    }

    /**
     * 商品详情页 sku 基本信息获取
     */
    @Override
    public SkuItemVo item(Long skuId) {
        SkuItemVo skuItemVo = new SkuItemVo();
        Long spuId = 0L;
        Long catalogId = 0L;
        // 1. sku 基本信息获取
        SkuInfoEntity skuInfoEntity = getById(skuId);
        if (skuInfoEntity != null) {
            skuItemVo.setSkuInfo(skuInfoEntity);
            spuId = skuInfoEntity.getSpuId();
            catalogId = skuInfoEntity.getCatalogId();
        }

        // 2. sku 图片信息
        List<SkuImagesEntity> SkuImagesEntities =  skuImagesService.getImagesBySkuId(skuId);
        if (SkuImagesEntities != null && !SkuImagesEntities.isEmpty()) {
            skuItemVo.setImages(SkuImagesEntities);
        }
        // 3. 获取 spu 销售属性组合
        List<SkuItemSaleAttrVo> skuItemSaleAttrVos = skuSaleAttrValueService.getSaleAttrsBySpuId(spuId);
        if (skuItemSaleAttrVos != null && !skuItemSaleAttrVos.isEmpty()) {
            skuItemVo.setSaleAttr(skuItemSaleAttrVos);
        }

        // 4. 获取 spu 描述属性
        SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(spuId);
        if (spuInfoDescEntity != null) {
            skuItemVo.setSpuInfoDesc(spuInfoDescEntity);
        }
        // 5. 获取 spu 规格参数
        List<SpuItemAttrGroupVo> spuItemAttrGroupVos = attrGroupService.getAttrGroupWithAttrsBySpuId(spuId, catalogId);
        if (spuItemAttrGroupVos != null && !spuItemAttrGroupVos.isEmpty()) {
            skuItemVo.setGroupAttrs(spuItemAttrGroupVos);
        }
        return skuItemVo;
    }

}