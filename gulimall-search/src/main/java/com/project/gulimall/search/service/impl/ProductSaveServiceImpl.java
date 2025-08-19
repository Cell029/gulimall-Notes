package com.project.gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.project.common.constant.EsConstant;
import com.project.common.to.es.SkuEsModel;
import com.project.gulimall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service("productSaveService")
public class ProductSaveServiceImpl implements ProductSaveService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException {
        // 保存到 es
        BulkRequest bulkRequest = new BulkRequest();
        // 构造批量保存的请求
        for (SkuEsModel skuEsModel : skuEsModels) {
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.id(skuEsModel.getSkuId().toString());
            String skuEsModelJson = JSON.toJSONString(skuEsModel);
            indexRequest.source(skuEsModelJson, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }

        BulkResponse bulk  = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        boolean b = false;
        if (bulk != null) {
            // 如果发生错误，b 就变为 true
            b = bulk.hasFailures();
            List<String> bulkItemIds = Arrays.stream(bulk.getItems()).map(BulkItemResponse::getId).collect(Collectors.toList());
            log.info("商品上架 es 完成：{}", bulkItemIds);
        }
        return b;
    }
}
