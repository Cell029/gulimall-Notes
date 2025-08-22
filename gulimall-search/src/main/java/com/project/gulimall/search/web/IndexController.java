package com.project.gulimall.search.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndexController {

    @GetMapping({"/search/search.html"})
    public String indexPage(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        model.addAttribute("keyword", keyword);
        // 默认前缀 classpath:/templates/
        // 默认后缀 .html
        return "index";
    }


}
