package com.project.gulimall.ware.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson.TypeReference;
import com.project.common.utils.R;
import com.project.gulimall.ware.domain.vo.FareVo;
import com.project.gulimall.ware.domain.vo.MemberAddressVo;
import com.project.gulimall.ware.feign.MemberFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.common.utils.PageUtils;
import com.project.common.utils.Query;

import com.project.gulimall.ware.dao.WareInfoDao;
import com.project.gulimall.ware.domain.entity.WareInfoEntity;
import com.project.gulimall.ware.service.WareInfoService;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Autowired
    private MemberFeignService memberFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareInfoEntity> queryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper
                    .eq("id", key)
                    .or().like("name", key)
                    .or().like("address", key)
                    .or().like("areacode", key);
        }
        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils(page);
    }

    @Override
    public FareVo getFare(Long addrId) {
        FareVo fareVo = new FareVo();
        R r = memberFeignService.info(addrId);
        MemberAddressVo memberReceiveAddress = r.getData("memberReceiveAddress", new TypeReference<MemberAddressVo>() {
        });
        if (memberReceiveAddress != null) {
            String phone = memberReceiveAddress.getPhone();
            String subString =  phone.substring(phone.length() - 1, phone.length());
            BigDecimal fare = new BigDecimal(subString);
            fareVo.setAddress(memberReceiveAddress);
            fareVo.setFare(fare);
            return fareVo;
        }
        return null;
    }

}