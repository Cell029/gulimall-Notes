package com.project.gulimall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.project.gulimall.coupon.domain.entity.SeckillSkuRelationEntity;
import com.project.gulimall.coupon.service.SeckillSkuRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.common.utils.PageUtils;
import com.project.common.utils.Query;
import com.project.gulimall.coupon.dao.SeckillSessionDao;
import com.project.gulimall.coupon.domain.entity.SeckillSessionEntity;
import com.project.gulimall.coupon.service.SeckillSessionService;


@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {

    @Autowired
    private SeckillSkuRelationService seckillSkuRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SeckillSessionEntity> page = this.page(
                new Query<SeckillSessionEntity>().getPage(params),
                new QueryWrapper<SeckillSessionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SeckillSessionEntity> getLatest3DaySession() {
        // 获取开始时间和结束时间
        LocalDateTime startTime = LocalDate.now().atStartOfDay();
        LocalDateTime endTime = LocalDate.now().plusDays(2).atTime(23, 59, 59);

        List<SeckillSessionEntity> seckillSessionEntities = list(new LambdaQueryWrapper<SeckillSessionEntity>()
                .between(SeckillSessionEntity::getStartTime, startTime, endTime));
        if (seckillSessionEntities.isEmpty()) {
            return Collections.emptyList();
        }

        // 获取秒杀活动场次的 id，也就是商品关联活动表的 PromotionSessionId
        List<Long> ids = seckillSessionEntities.stream()
                .map(SeckillSessionEntity::getId)
                .collect(Collectors.toList());

        // 一次查询所有关联商品
        Map<Long, List<SeckillSkuRelationEntity>> skuMap = seckillSkuRelationService
                .list(new LambdaQueryWrapper<SeckillSkuRelationEntity>()
                        .in(SeckillSkuRelationEntity::getPromotionSessionId, ids))
                .stream()
                .collect(Collectors.groupingBy(SeckillSkuRelationEntity::getPromotionSessionId));

        return seckillSessionEntities.stream()
                .map(session -> {
                    session.setRelationSkus(skuMap.getOrDefault(session.getId(), Collections.emptyList()));
                    return session;
                }).collect(Collectors.toList());
    }

}