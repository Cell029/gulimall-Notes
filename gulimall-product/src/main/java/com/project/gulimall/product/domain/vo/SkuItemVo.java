package com.project.gulimall.product.domain.vo;

import com.project.gulimall.product.domain.entity.SkuImagesEntity;
import com.project.gulimall.product.domain.entity.SkuInfoEntity;
import com.project.gulimall.product.domain.entity.SpuInfoDescEntity;
import lombok.Data;
import java.util.List;

@Data
public class SkuItemVo {

    // sku 基本信息
    private SkuInfoEntity skuInfo;

    // sku 图片信息
    private List<SkuImagesEntity> images;

    // 获取 spu 的销售属性组合
    private List<SkuItemSaleAttrVo> saleAttr;

    // 获取 spu 的介绍
    private SpuInfoDescEntity spuInfoDesc;

    // 获取 spu 规格参数信息
    List<SpuItemAttrGroupVo> groupAttrs;

    // 是否有货
    Boolean hasStock = true;

}
