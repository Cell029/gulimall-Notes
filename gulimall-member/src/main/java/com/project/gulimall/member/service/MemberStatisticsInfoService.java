package com.project.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.common.utils.PageUtils;
import com.project.gulimall.member.entity.MemberStatisticsInfoEntity;

import java.util.Map;

/**
 * 会员统计信息
 */
public interface MemberStatisticsInfoService extends IService<MemberStatisticsInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

