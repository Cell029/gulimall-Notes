package com.project.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.common.utils.PageUtils;
import com.project.gulimall.coupon.entity.SpuBoundsEntity;

import java.util.Map;

/**
 * 商品spu积分设置
 */
public interface SpuBoundsService extends IService<SpuBoundsEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

