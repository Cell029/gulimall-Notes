package com.project.gulimall.search.web;

import com.project.gulimall.search.domain.vo.SearchParam;
import com.project.gulimall.search.domain.vo.SearchResult;
import com.project.gulimall.search.service.MallSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import javax.servlet.http.HttpServletRequest;


@Controller
public class SearchController {

    @Autowired
    private MallSearchService mallSearchService;

    /**
     * 携带检索参数跳转到页面
     */
    @GetMapping({"/", "/list.html"})
    public String indexPage(SearchParam param, Model model, HttpServletRequest request) {
        // 传递 queryString
        param.setQueryString(request.getQueryString() == null ? "" : request.getQueryString());
        // 1. 根据传递来的页面的查询参数去 es 中检索商品
        SearchResult result = mallSearchService.search(param);
        model.addAttribute("result", result);
        return "list";
    }


}
