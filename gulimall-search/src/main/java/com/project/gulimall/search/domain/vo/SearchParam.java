package com.project.gulimall.search.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * 封装页面可能传递的所有查询条件
 */
@Data
public class SearchParam {
    // 全文匹配关键字
    private String keyword;
    // 三级分类 id
    private Long catalog3Id;
    /**
     * sort = saleCount_asc/desc，销量排序
     * sort = skuPrice_asc/desc，价格排序
     * sort = hotScore_asc/desc，综合排序（热度评分）
     */
    private String sort; // 排序条件
    /**
     * 过滤条件
     *   hasStock（是否有货）、skuPrice 区间、brandId、catalog3Id、attrs
     *   hasStock = 0/1
     *   skuPrice = 1_500/_500/500_（1 - 500/500 以内/大于 500）
     */
    private Integer hasStock; // 是否只显示有货
    private String skuPrice; // 价格区间
    private List<Long> brandId; // 按品牌筛选（品牌 Id），允许多选
    private List<String> attrs; // 按属性筛选
    private Integer pageNum = 1; // 页码

    // 前端查询的 url 中 "?" 后的所有查询条件
    private String queryString;

}
