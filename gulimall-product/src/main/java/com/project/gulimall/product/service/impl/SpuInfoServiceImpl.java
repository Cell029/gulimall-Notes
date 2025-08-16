package com.project.gulimall.product.service.impl;

import com.project.common.to.SkuReductionTo;
import com.project.common.to.SpuBoundsTo;
import com.project.common.utils.R;
import com.project.gulimall.product.dao.SpuInfoDescDao;
import com.project.gulimall.product.domain.entity.*;
import com.project.gulimall.product.domain.vo.*;
import com.project.gulimall.product.feign.CouponFeignService;
import com.project.gulimall.product.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.common.utils.PageUtils;
import com.project.common.utils.Query;
import com.project.gulimall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDao spuInfoDao;
    @Autowired
    private SpuInfoDescService spuInfoDescService;
    @Autowired
    private SpuImagesService spuImagesService;
    @Autowired
    private AttrService attrService;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private SkuInfoService skuInfoService;
    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    private CouponFeignService couponFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo vo) {
        // 1. 保存 spu 的基本信息，表 pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(vo, spuInfoEntity);
        this.saveBaseSpuInfo(spuInfoEntity);

        // 2. 保存 spu 的描述图片集合，表 pms_spu_info_desc
        List<String> decriptList = vo.getDecript();
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        // 拼接 decriptList 集合中的商品介绍的图片的 url，用 “，” 隔开
        spuInfoDescEntity.setDecript(String.join(",", decriptList));
        // 将数据存入表 pms_spu_info_desc
        spuInfoDescService.saveSpuInfoDesc(spuInfoDescEntity);

        // 3. 保存 spu 的图片集，表 pms_spu_images
        List<String> images = vo.getImages();
        // 传入 spu 的 id 和图片集
        spuImagesService.saveImages(spuInfoEntity.getId(), images);

        // 4. 保存 spu 的规格参数，表 pms_product_attr_value
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        List<ProductAttrValueEntity> ProductAttrValueEntities = baseAttrs.stream().map(baseAtr -> {
            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
            productAttrValueEntity.setAttrId(baseAtr.getAttrId());
            AttrEntity attrEntity = attrService.getById(baseAtr.getAttrId());
            productAttrValueEntity.setAttrName(attrEntity.getAttrName());
            productAttrValueEntity.setAttrValue(baseAtr.getAttrValues());
            productAttrValueEntity.setQuickShow(baseAtr.getShowDesc());
            productAttrValueEntity.setSpuId(spuInfoEntity.getId());
            return productAttrValueEntity;
        }).collect(Collectors.toList());
        productAttrValueService.saveProductAttr(ProductAttrValueEntities);

        // 5. 保存 spu 的积分信息，表 gulimall_sms -> sms_spu_bounds
        Bounds bounds = vo.getBounds();
        SpuBoundsTo spuBoundsTo = new SpuBoundsTo();
        BeanUtils.copyProperties(bounds, spuBoundsTo);
        spuBoundsTo.setSpuId(spuInfoEntity.getId());

        R r = couponFeignService.saveSpuBounds(spuBoundsTo);
        if (r.getCode() != 0) {
            log.error("远程保存 spu 级分信息失败！");
        }

        // 6. 保存当前 spu 对应的所有 sku 信息
        List<Skus> skusList = vo.getSkus();
        if (skusList != null && !skusList.isEmpty()) {
            skusList.forEach(skus -> {
                String defaultImg = "";
                for (Images image : skus.getImages()) {
                    if (image.getDefaultImg() == 1) {
                        defaultImg = image.getImgUrl();
                    }
                }
                /**
                 * 只有这四个字段一样
                 * private String skuName;
                 * private BigDecimal price;
                 * private String skuTitle;
                 * private String skuSubtitle;
                 */
                // 6.1 保存 sku 的基本信息，表 pms_sku_info
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(skus, skuInfoEntity);
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                skuInfoService.saveSkuInfo(skuInfoEntity);

                // 6.2 保存 sku 的图片信息，表 pms_sku_images
                Long skuId = skuInfoEntity.getSkuId();
                List<SkuImagesEntity> skuImagesEntities = skus.getImages().stream().map(img -> {
                            SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                            skuImagesEntity.setSkuId(skuId);
                            skuImagesEntity.setImgUrl(img.getImgUrl());
                            skuImagesEntity.setDefaultImg(img.getDefaultImg());
                            return skuImagesEntity;
                        })
                        .filter(img -> {
                            // 返回 true 就是需要，返回 false 就是不需要，也就不会保存进数据库
                            return !StringUtils.isEmpty(img.getImgUrl());
                        })
                        .collect(Collectors.toList());
                skuImagesService.saveBatch(skuImagesEntities);

                // 6.3 保存 sku 的销售规格参数信息，表 pms_sku_sale_attr_value
                List<Attr> attrList = skus.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attrList.stream().map(attr -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(attr, skuSaleAttrValueEntity);
                    skuSaleAttrValueEntity.setSkuId(skuId);
                    return skuSaleAttrValueEntity;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);

                // 6.4 保存 sku 的优惠、满减等信息，表 gulimall_sms -> sms_sku_ladder、sms_sku_full_reduction、sms_member_price
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(skus, skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                // 有满几件打折或者满减优惠时才远程调用
                if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) > 0) {
                    R r1 = couponFeignService.saveSkuReduction(skuReductionTo);
                    if (r1.getCode() != 0) {
                        log.error("远程保存 sku 优惠信息失败！");
                    }
                }
            });
        }

    }

    /**
     * 保存 spu 的基本信息，表 pms_spu_info
     */
    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        spuInfoDao.insert(spuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        log.info("接收到的查询参数: {}", params);
        QueryWrapper<SpuInfoEntity> queryWrapper = new QueryWrapper<>();
        // 模糊检索
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and(wrapper -> {
                wrapper.eq("id", key).or().like("spu_name", key);
            });
        }
        // 状态条件
        String status = (String) params.get("status");
        if (!StringUtils.isEmpty(status)) {
            queryWrapper.eq("publish_status", status);
        }
        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equals(brandId)) {
            queryWrapper.eq("brand_id", brandId);
        }
        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId) && !"0".equals(catelogId)) {
            queryWrapper.eq("catalog_id", catelogId);
        }
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils(page);
    }


}