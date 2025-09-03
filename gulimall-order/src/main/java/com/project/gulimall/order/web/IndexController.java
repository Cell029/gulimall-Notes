package com.project.gulimall.order.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class IndexController {
    @GetMapping("/{page}.html")
    public String index(@PathVariable("page") String page, Model model) {
        return page;
    }
}
