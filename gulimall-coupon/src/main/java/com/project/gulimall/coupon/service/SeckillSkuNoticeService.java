package com.project.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.common.utils.PageUtils;
import com.project.gulimall.coupon.entity.SeckillSkuNoticeEntity;

import java.util.Map;

/**
 * 秒杀商品通知订阅
 */
public interface SeckillSkuNoticeService extends IService<SeckillSkuNoticeEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

