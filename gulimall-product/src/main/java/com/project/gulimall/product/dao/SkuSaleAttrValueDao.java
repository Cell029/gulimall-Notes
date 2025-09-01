package com.project.gulimall.product.dao;

import com.project.gulimall.product.domain.entity.SkuSaleAttrValueEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.project.gulimall.product.domain.vo.SkuItemSaleAttrVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * sku销售属性&值
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {

    List<SkuItemSaleAttrVo> getSaleAttrsBySpuId(@Param("spuId") Long spuId);

    List<String> getSkuSaleAttrValueList(@Param("skuId")Long skuId);
}
