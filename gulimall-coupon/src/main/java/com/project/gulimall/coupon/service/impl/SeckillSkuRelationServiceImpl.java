package com.project.gulimall.coupon.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.common.utils.PageUtils;
import com.project.common.utils.Query;

import com.project.gulimall.coupon.dao.SeckillSkuRelationDao;
import com.project.gulimall.coupon.domain.entity.SeckillSkuRelationEntity;
import com.project.gulimall.coupon.service.SeckillSkuRelationService;


@Service("seckillSkuRelationService")
public class SeckillSkuRelationServiceImpl extends ServiceImpl<SeckillSkuRelationDao, SeckillSkuRelationEntity> implements SeckillSkuRelationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<SeckillSkuRelationEntity> seckillSkuRelationEntityQueryWrapper = new QueryWrapper<SeckillSkuRelationEntity>();
        String promotionSessionId = (String) params.get("promotionSessionId");
        if (!StringUtils.isEmpty(promotionSessionId)) {
            seckillSkuRelationEntityQueryWrapper.eq("promotion_session_id", promotionSessionId);
        }
        IPage<SeckillSkuRelationEntity> page = this.page(
                new Query<SeckillSkuRelationEntity>().getPage(params),
                seckillSkuRelationEntityQueryWrapper
        );
        return new PageUtils(page);
    }

}