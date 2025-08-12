package com.project.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.common.utils.PageUtils;
import com.project.gulimall.product.entity.CategoryEntity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);


    List<CategoryEntity> listWithTree();

    void removeMenuByIds(List<Long> list);

    /**
     * 找到 catelogId 的完整路径（一级/二级/三级）
     *
     * @param catelogId
     * @return
     */
    Long[] findCatelogPath(Long catelogId);

    void updateCascade(CategoryEntity category);
}

