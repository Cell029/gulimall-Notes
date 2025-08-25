package com.project.gulimall.search.domain.vo;

import com.project.common.to.es.SkuEsModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SearchResult {
    /**
     * 商品信息
     */
    private List<SkuEsModel> products; // 查询到的所有商品信息
    /**
     * 分页信息
     */
    private Integer pageNum; // 当前页码
    private Long total = 0L; // 总记录数
    private Integer totalPages = 0; // 总页码
    private List<Integer> pageNavs = new ArrayList<>(); // 导航页码

    private List<BrandVo> brands; // 当前查询到的结果，所涉及到的所有品牌

    private List<AttrVo> attrs; // 当前查询到的结果，所涉及到的所有属性

    private List<CatalogVo> catalogs; // 当前查询到的结果，所涉及到的所有分类

    private List<Long> blankAttrIds; // 添加某个属性值后，该属性所属的那个 name 不显示

    // 面包屑导航数据
    private List<NavVo> navs;

    @Data
    public static class NavVo {
        private String navName;
        private String navValue;
        private String link;
    }

    @Data
    public static class BrandVo {
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    public static class AttrVo {
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }

    @Data
    public static class CatalogVo {
        private Long catalogId;
        private String catalogName;
    }

}