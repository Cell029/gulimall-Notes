package com.project.gulimall.ware.dao;

import com.project.gulimall.ware.domain.entity.WareInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 仓库信息
 */
@Mapper
public interface WareInfoDao extends BaseMapper<WareInfoEntity> {
	
}
