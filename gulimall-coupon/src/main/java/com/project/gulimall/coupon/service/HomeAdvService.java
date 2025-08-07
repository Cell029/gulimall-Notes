package com.project.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.common.utils.PageUtils;
import com.project.gulimall.coupon.entity.HomeAdvEntity;

import java.util.Map;

/**
 * 首页轮播广告
 */
public interface HomeAdvService extends IService<HomeAdvEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

