package com.project.gulimall.product.dao;

import com.project.gulimall.product.domain.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品属性
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {
	
}
