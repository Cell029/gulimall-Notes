package com.project.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.common.to.es.SkuEsModel;
import com.project.common.utils.PageUtils;
import com.project.common.utils.R;
import com.project.gulimall.product.domain.entity.SpuInfoDescEntity;
import com.project.gulimall.product.domain.entity.SpuInfoEntity;
import com.project.gulimall.product.domain.vo.SpuSaveVo;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;
import java.util.Map;

/**
 * spu信息
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(SpuSaveVo vo);

    void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity);

    PageUtils queryPageByCondition(Map<String, Object> params);

    void up(Long spuId);

}

