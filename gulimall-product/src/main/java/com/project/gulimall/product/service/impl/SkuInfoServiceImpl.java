package com.project.gulimall.product.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.project.gulimall.product.config.MyThreadConfig;
import com.project.gulimall.product.domain.entity.SkuImagesEntity;
import com.project.gulimall.product.domain.entity.SpuInfoDescEntity;
import com.project.gulimall.product.domain.vo.SkuItemSaleAttrVo;
import com.project.gulimall.product.domain.vo.SkuItemVo;
import com.project.gulimall.product.domain.vo.SpuItemAttrGroupVo;
import com.project.gulimall.product.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

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
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

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

        CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {
            // 1. sku 基本信息获取
            SkuInfoEntity skuInfoEntity = getById(skuId);
            if (skuInfoEntity != null) {
                skuItemVo.setSkuInfo(skuInfoEntity);
            }
            return skuInfoEntity;
        }, threadPoolExecutor);

        CompletableFuture<Void> saleAttrFuture = infoFuture.thenAcceptAsync(info -> {
            // 3. 获取 spu 销售属性组合
            List<SkuItemSaleAttrVo> skuItemSaleAttrVos = skuSaleAttrValueService.getSaleAttrsBySpuId(info.getSpuId());
            if (skuItemSaleAttrVos != null && !skuItemSaleAttrVos.isEmpty()) {
                skuItemVo.setSaleAttr(skuItemSaleAttrVos);
            }
        }, threadPoolExecutor);

        CompletableFuture<Void> spuDescribeFuture = infoFuture.thenAcceptAsync(info -> {
            // 4. 获取 spu 描述属性
            SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(info.getSpuId());
            if (spuInfoDescEntity != null) {
                skuItemVo.setSpuInfoDesc(spuInfoDescEntity);
            }
        }, threadPoolExecutor);

        CompletableFuture<Void> baseAttrFuture = infoFuture.thenAcceptAsync(info -> {
            // 5. 获取 spu 规格参数
            List<SpuItemAttrGroupVo> spuItemAttrGroupVos = attrGroupService.getAttrGroupWithAttrsBySpuId(info.getSpuId(), info.getCatalogId());
            if (spuItemAttrGroupVos != null && !spuItemAttrGroupVos.isEmpty()) {
                skuItemVo.setGroupAttrs(spuItemAttrGroupVos);
            }
        }, threadPoolExecutor);

        CompletableFuture<Void> imageFuture = CompletableFuture.runAsync(() -> {
            // 2. sku 图片信息
            List<SkuImagesEntity> SkuImagesEntities =  skuImagesService.getImagesBySkuId(skuId);
            if (SkuImagesEntities != null && !SkuImagesEntities.isEmpty()) {
                skuItemVo.setImages(SkuImagesEntities);
            }
        }, threadPoolExecutor);

        // 等待所有任务都完成
        try {
            CompletableFuture.allOf(saleAttrFuture, spuDescribeFuture, baseAttrFuture, imageFuture).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return skuItemVo;
    }

    @Override
    public Map<Long, BigDecimal> getCurrentCartItemPriceMap(List<Long> skuIds) {
        HashMap<Long, BigDecimal> priceMap = new HashMap<>();
        for (Long skuId : skuIds) {
            SkuInfoEntity skuInfoEntity = getById(skuId);
            priceMap.put(skuInfoEntity.getSkuId(), skuInfoEntity.getPrice());
        }
        return priceMap;
    }

}