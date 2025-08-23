package com.project.gulimall.search.service;

import com.project.gulimall.search.domain.vo.SearchParam;
import com.project.gulimall.search.domain.vo.SearchResult;

public interface MallSearchService {

    SearchResult search(SearchParam param);

}
