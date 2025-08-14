package com.project.gulimall.product.domain.vo;

import lombok.Data;

@Data
public class AttrResVo extends AttrVo {
    /**
     * 所属三级分类名
     */
    private String catelogName;

    /**
     * 所属属性分组名
     */
    private String groupName;

    /**
     * 三级分类完整路径
     */
    private Long[] catelogPath;
}
