package com.project.gulimall.ware.dao;

import com.project.gulimall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品库存
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {
	
}
