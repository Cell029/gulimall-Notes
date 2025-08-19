package com.project.gulimall.search.service;

import com.project.common.to.es.SkuEsModel;
import java.io.IOException;
import java.util.List;


public interface ProductSaveService {
    boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException;
}
