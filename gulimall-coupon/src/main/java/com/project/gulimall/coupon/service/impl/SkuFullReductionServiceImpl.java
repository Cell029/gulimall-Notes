package com.project.gulimall.coupon.service.impl;

import com.project.common.to.MemberPrice;
import com.project.common.to.SkuReductionTo;
import com.project.gulimall.coupon.domain.entity.MemberPriceEntity;
import com.project.gulimall.coupon.domain.entity.SkuLadderEntity;
import com.project.gulimall.coupon.service.MemberPriceService;
import com.project.gulimall.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.common.utils.PageUtils;
import com.project.common.utils.Query;

import com.project.gulimall.coupon.dao.SkuFullReductionDao;
import com.project.gulimall.coupon.domain.entity.SkuFullReductionEntity;
import com.project.gulimall.coupon.service.SkuFullReductionService;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    private SkuLadderService skuLadderService;
    @Autowired
    private MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuReduction(SkuReductionTo skuReductionTo) {
        // 保存 sku 的优惠、满减等信息，表 gulimall_sms -> sms_sku_ladder、sms_sku_full_reduction、sms_member_price
        // 1. 满几件优惠多少，sms_sku_ladder
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        skuLadderEntity.setSkuId(skuReductionTo.getSkuId());
        skuLadderEntity.setFullCount(skuReductionTo.getFullCount());
        skuLadderEntity.setDiscount(skuReductionTo.getDiscount());
        skuLadderEntity.setAddOther(skuReductionTo.getCountStatus());
        if (skuReductionTo.getFullCount() > 0) {
            skuLadderService.save(skuLadderEntity);
        }

        // 2. 保存满减打折，sms_sku_full_reduction
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuReductionTo, skuFullReductionEntity);
        if (skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) > 0) {
            this.save(skuFullReductionEntity);
        }

        // 3. 保存会员价，sms_member_price
        List<MemberPrice> memberPriceList = skuReductionTo.getMemberPrice();
        if (memberPriceList != null && !memberPriceList.isEmpty()) {
            List<MemberPriceEntity> memberPriceEntities = memberPriceList.stream().map(memberPrice -> {
                        MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
                        memberPriceEntity.setSkuId(skuReductionTo.getSkuId());
                        memberPriceEntity.setMemberLevelId(memberPrice.getId());
                        memberPriceEntity.setMemberLevelName(memberPrice.getName());
                        memberPriceEntity.setMemberPrice(memberPrice.getPrice());
                        memberPriceEntity.setAddOther(1);
                        return memberPriceEntity;
                    })
                    .filter(memberPrice -> {
                        return memberPrice.getMemberPrice().compareTo(new BigDecimal("0")) >= 0;
                    })
                    .collect(Collectors.toList());
            if(!memberPriceEntities.isEmpty()){
                memberPriceService.saveBatch(memberPriceEntities);
            }
        }

    }

}