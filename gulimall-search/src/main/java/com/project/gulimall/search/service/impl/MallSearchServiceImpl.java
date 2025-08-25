package com.project.gulimall.search.service.impl;

import com.alibaba.fastjson2.JSON;
import com.project.common.constant.EsConstant;
import com.project.common.to.es.SkuEsModel;
import com.project.gulimall.search.domain.vo.SearchParam;
import com.project.gulimall.search.domain.vo.SearchResult;
import com.project.gulimall.search.service.MallSearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service("mallSearchService")
public class MallSearchServiceImpl implements MallSearchService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public SearchResult search(SearchParam param) {
        // 动态构建出查询需要的 DSL 语句
        SearchResult result = null;
        // 1. 准备检索请求
        SearchRequest searchRequest = buildSearchRequest(param);
        SearchResponse searchResponse;
        try {
            // 2. 执行检索请求
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            // 3. 分析响应数据，封装成需要的格式
            result = buildSearchResult(searchResponse, param);
        } catch (IOException e) {
            log.error("构建检索失败：{}", e.getMessage());
        }
        return result;
    }


    /**
     * 构建结果数据
     * @param searchResponse
     * @return
     */
    private SearchResult buildSearchResult(SearchResponse searchResponse, SearchParam param) {
        SearchResult searchResult = new SearchResult();
        // 1. 返回所有查询到的商品
        List<SkuEsModel> skuEsModels = new ArrayList<>();
        SearchHits hits = searchResponse.getHits();
        if (hits.getHits() != null && hits.getHits().length > 0) {
            for (SearchHit hit : hits.getHits()) {
                String sourceAsString = hit.getSourceAsString();
                SkuEsModel skuEsModel = JSON.parseObject(sourceAsString, SkuEsModel.class);
                if (StringUtils.isNotBlank(param.getKeyword())) {
                    HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
                    if (skuTitle != null && skuTitle.getFragments() != null && skuTitle.getFragments().length > 0) {
                        skuEsModel.setSkuTitle(skuTitle.getFragments()[0].string());
                    }
                }
                skuEsModels.add(skuEsModel);
            }
        }
        searchResult.setProducts(skuEsModels);

        // 2. 当前查询到的结果，所涉及到的所有品牌
        List<SearchResult.BrandVo> brandVos = new ArrayList<>();
        Terms brand_agg = searchResponse.getAggregations().get("brand_agg");
        if (brand_agg != null && !brand_agg.getBuckets().isEmpty()) {
            brand_agg.getBuckets().forEach(bucket -> {
                SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
                // 获取品牌 id
                brandVo.setBrandId(bucket.getKeyAsNumber().longValue());
                // 获取品牌名称
                Terms brand_name_agg = bucket.getAggregations().get("brand_name_agg");
                if (brand_name_agg != null && !brand_name_agg.getBuckets().isEmpty()) {
                    brandVo.setBrandName(brand_name_agg.getBuckets().get(0).getKeyAsString());
                }
                // 获取品牌的图片
                Terms brand_img_agg = bucket.getAggregations().get("brand_img_agg");
                if (brand_img_agg != null && !brand_img_agg.getBuckets().isEmpty()) {
                    brandVo.setBrandImg(brand_img_agg.getBuckets().get(0).getKeyAsString());
                }
                brandVos.add(brandVo);
            });
            searchResult.setBrands(brandVos);
        }

        // 3. 当前查询到的结果，所涉及到的所有分类
        List<SearchResult.CatalogVo> catalogVos = new ArrayList<>();
        Terms catalog_agg = searchResponse.getAggregations().get("catalog_agg");
        if (catalog_agg != null && !catalog_agg.getBuckets().isEmpty()) {
            catalog_agg.getBuckets().forEach(bucket -> {
                SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
                // 得到分类 id
                String keyAsString = bucket.getKeyAsString();
                catalogVo.setCatalogId(Long.valueOf(keyAsString));
                // 得到分类名
                Terms catalog_name_agg = bucket.getAggregations().get("catalog_name_agg");
                if (catalog_name_agg != null && !catalog_name_agg.getBuckets().isEmpty()) {
                    String catalogName = catalog_name_agg.getBuckets().get(0).getKeyAsString();
                    catalogVo.setCatalogName(catalogName);
                }
                catalogVos.add(catalogVo);
            });
            searchResult.setCatalogs(catalogVos);
        }

        // 4. 当前查询到的结果，所涉及到的所有属性
        List<SearchResult.AttrVo> attrVos = new ArrayList<>();
        Nested attr_agg = searchResponse.getAggregations().get("attr_agg");
        if (attr_agg != null) {
            Terms attr_id_agg = attr_agg.getAggregations().get("attr_id_agg");
            if (attr_id_agg != null && !attr_id_agg.getBuckets().isEmpty()) {
                attr_id_agg.getBuckets().forEach(bucket -> {
                    SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
                    // 1. 得到属性 id
                    attrVo.setAttrId(bucket.getKeyAsNumber().longValue());
                    Terms attr_name_agg = bucket.getAggregations().get("attr_name_agg");
                    if (attr_name_agg != null && !attr_name_agg.getBuckets().isEmpty()) {
                        // 2. 得到属性名称
                        attrVo.setAttrName(attr_name_agg.getBuckets().get(0).getKeyAsString());
                    }
                    Terms attr_value_agg = bucket.getAggregations().get("attr_value_agg");
                    if (attr_value_agg != null && !attr_value_agg.getBuckets().isEmpty()) {
                        List<String> attrValueList = attr_value_agg.getBuckets().stream().map(MultiBucketsAggregation.Bucket::getKeyAsString).collect(Collectors.toList());
                        attrVo.setAttrValue(attrValueList);
                    }
                    attrVos.add(attrVo);
                });
                searchResult.setAttrs(attrVos);
            }
        }

        // 5. 分页信息
        searchResult.setPageNum(param.getPageNum());
        if (hits.getTotalHits() != null) {
            long total = hits.getTotalHits().value;
            searchResult.setTotal(total);
            // 向上取整，不满一页的时候也算作一页
            int totalPages = (int) ((total + EsConstant.PRODUCT_PAGESIZE - 1) / EsConstant.PRODUCT_PAGESIZE);
            searchResult.setTotalPages(totalPages);
        }
        // 展示分页数组，一个属性对应一页，便于统计
        List<Integer> pageNavs = new ArrayList<>();
        for (int i = 1; i <= searchResult.getTotalPages(); i++) {
            pageNavs.add(i);
        }
        searchResult.setPageNavs(pageNavs);

        // 6. 构建面包屑 NavVo
        List<SearchResult.NavVo> navVos = new ArrayList<>();
        List<Long> blankAttrIds = new ArrayList<>();
        if (param.getAttrs() != null) { // 通过传递的参数获取所有属性
            for (String attr : param.getAttrs()) { // attr=16_A13仿生
                String[] split = attr.split("_", 2); // 只拆分成 2 个部分
                if (split.length != 2) continue;
                // 获取 attrId
                String attrId = split[0];
                blankAttrIds.add(Long.valueOf(attrId));
                // 获取 attrValue
                String attrValue = split[1];
                // 封装为 NavVo 对象
                SearchResult.NavVo navVo = new SearchResult.NavVo();
                navVo.setNavValue(attrValue);
                // 获取属性名称
                attrVos.stream()
                        .filter(attrVo -> attrVo.getAttrId().toString().equals(attrId))
                        .findFirst()
                        .ifPresent(attrVo -> navVo.setNavName(attrVo.getAttrName()));
                // 生成取消面包屑的链接
                String queryString = Optional.ofNullable(param.getQueryString()).orElse("");
                List<String> queryAttrParams = new ArrayList<>();
                // 根据 & 进行分割
                for (String p : queryString.split("&")) {
                    if (!p.isEmpty()) queryAttrParams.add(p);
                }
                String targetAttr = attr; // 原始 attr，例如 "15_高通(Qualcomm)"
                // 从 queryString 里删除目标属性参数
                queryAttrParams.removeIf(p -> {
                    String[] kv = p.split("=", 2); // limit=2 的作用：即便 value 里还有 "="，也只切一次，避免被继续拆开
                    if (kv.length != 2) {
                        return false;
                    }
                    // 只处理 key 为 "attrs" 的参数；其他参数（如 catalog3Id、brandId）一律保留
                    if (!kv[0].equals("attrs")) {
                        return false;
                    }
                    try {
                        // 对 value 做 URL 解码（把 %E4%BB%A5… 还原成中文；把 '+' 按表单规则解码成空格）
                        String value = java.net.URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
                        // 如果解码后的值正好等于要移除的那个 attr（未编码的原始字符串），就返回 true -> 删除
                        return value.equals(targetAttr);
                    } catch (Exception e) {
                        return false;
                    }
                });
                String newQuery = String.join("&", queryAttrParams);
                // 对原始的请求条件进行判断，如果为空，那么取消面包屑后直接拼接空值；否则拼接原有的 queryString
                navVo.setLink("http://search.gulimall.com/list.html" + (newQuery.isEmpty() ? "" : "?" + newQuery));
                navVos.add(navVo);
            }
        }
        searchResult.setNavs(navVos);
        searchResult.setBlankAttrIds(blankAttrIds);
        return searchResult;

    }


    /**
     * 准备检索请求
     * 模糊匹配、过滤（属性、分类、品牌、价格区间、库存）、排序、分页、高亮、聚合分析
     */
    private SearchRequest buildSearchRequest(SearchParam param) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        /**
         * 模糊匹配、过滤（属性、分类、品牌、价格区间、库存）
         */
        // 1. 构建 bool-query
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 构建 must 模糊匹配
        if (StringUtils.isNotBlank(param.getKeyword())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle", param.getKeyword()));
        }

        // 构建 filter
        // 按照三级分类 id 查询
        if (param.getCatalog3Id() != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("catalogId", param.getCatalog3Id()));
        }
        // 按照品牌 id 集合查询
        if (param.getBrandId() != null && !param.getBrandId().isEmpty()) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId", param.getBrandId()));
        }
        // 按照属性查询
        if (param.getAttrs() != null && !param.getAttrs().isEmpty()) {
            for (String attrStr : param.getAttrs()) {
                BoolQueryBuilder nestedBoolQueryBuilder = QueryBuilders.boolQuery();
                // attrs=1_5寸:8寸&attrs=2_16G:8G
                String[] s = attrStr.split("_");
                String attrId = s[0]; // 检索的属性 id
                String[] attrValues = s[1].split(":"); // 检索的属性值
                nestedBoolQueryBuilder.must(QueryBuilders.termQuery("attrs.attrId", attrId));
                nestedBoolQueryBuilder.must(QueryBuilders.termsQuery("attrs.attrValue", attrValues));
                // 每一个 attr 都要生成一个 nested 查询
                NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("attrs", nestedBoolQueryBuilder, ScoreMode.None);
                boolQueryBuilder.filter(nestedQueryBuilder);
            }
        }
        // 按照是否有库存查询
        if (param.getHasStock() != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("hasStock", param.getHasStock() == 1));
        }
        // 按照价格区间查询
        if (StringUtils.isNotBlank(param.getSkuPrice())) {
            // 约定格式：1_500/_500/500_
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("skuPrice");
            // 解析 skuPrice 格式
            String[] s = param.getSkuPrice().split("_");
            if (s.length == 2) {
                if (!s[0].isEmpty()) rangeQueryBuilder.gte(s[0]);
                if (!s[1].isEmpty())rangeQueryBuilder.lte(s[1]);
            } else {
                // 判断是大于还是小于
                if (param.getSkuPrice().startsWith("_")) {
                    // 以 "_" 开头证明是小于
                    if (!s[0].isEmpty()) { // 对于 "_500", s[0] 是空字符串，所以不会执行
                        rangeQueryBuilder.lte(s[0]);
                    }
                    if (s.length > 1 && !s[1].isEmpty()) {
                        rangeQueryBuilder.lte(s[1]);
                    }
                    // rangeQueryBuilder.lte(s[0]);
                } else if (param.getSkuPrice().endsWith("_")) {
                    // 处理 500_
                    if (!s[0].isEmpty()) {
                        rangeQueryBuilder.gte(s[0]);
                    }
                }
            }
            boolQueryBuilder.filter(rangeQueryBuilder);
        }

        searchSourceBuilder.query(boolQueryBuilder);

        /**
         * 排序、分页、高亮
         */
        // 排序
        if (StringUtils.isNotBlank(param.getSort())) {
            String sort = param.getSort();
            String[] s = sort.split("_");
            if (!s[0].isEmpty() && !s[1].isEmpty()) {
                SortOrder sortOrder = s[1].equals("asc") ? SortOrder.ASC : SortOrder.DESC;
                searchSourceBuilder.sort(s[0], sortOrder);
            }
        }
        // 分页
        searchSourceBuilder.from((param.getPageNum() - 1) * EsConstant.PRODUCT_PAGESIZE);
        searchSourceBuilder.size(EsConstant.PRODUCT_PAGESIZE);
        // 高亮
        if (StringUtils.isNotBlank(param.getKeyword())) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<span style='color:red'>");
            highlightBuilder.postTags("</span>");
            searchSourceBuilder.highlighter(highlightBuilder);
        }

        /**
         * 聚合分析
         */
        // 1. 品牌聚合
        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg");
        brand_agg.field("brandId").size(50);
        // 品牌聚合的子聚合
        brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName")).size(1);
        brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg")).size(10);
        searchSourceBuilder.aggregation(brand_agg);

        // 2. 分类聚合
        TermsAggregationBuilder catalog_agg = AggregationBuilders.terms("catalog_agg");
        catalog_agg.field("catalogId").size(50);
        // 分类聚合的子聚合
        catalog_agg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName")).size(1);
        searchSourceBuilder.aggregation(catalog_agg);

        // 3. 属性聚合
        NestedAggregationBuilder attr_agg = AggregationBuilders.nested("attr_agg", "attrs");
        // 作为 nested 的子聚合
        TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId").size(50);
        // 作为 nested 的子聚合的子聚合，即名字和属性值
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName")).size(1);
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue")).size(50);
        attr_agg.subAggregation(attr_id_agg);
        // 放入 SearchSourceBuilder
        searchSourceBuilder.aggregation(attr_agg);

        String s = searchSourceBuilder.toString();
        System.out.println("构建的 DSL：" + s);

        SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, searchSourceBuilder);

        return searchRequest;
    }
}
