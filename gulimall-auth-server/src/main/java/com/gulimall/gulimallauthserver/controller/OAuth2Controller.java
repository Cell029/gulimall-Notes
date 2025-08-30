package com.gulimall.gulimallauthserver.controller;

import com.gulimall.gulimallauthserver.domain.vo.GitHubUserInfoVo;
import com.gulimall.gulimallauthserver.domain.vo.MemberResponseVo;
import com.gulimall.gulimallauthserver.excetpion.OAuthException;
import com.gulimall.gulimallauthserver.service.GitHubOAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 处理社交登录请求
 */
@Slf4j
@Controller
public class OAuth2Controller {

    @Autowired
    private GitHubOAuthService gitHubOAuthService;

    @GetMapping("/oauth/github/success")
    public String githubSuccess(@RequestParam("code") String code, RedirectAttributes redirectAttributes) {
        // 根据 code 换取 Access Token
        try {
            MemberResponseVo userInfo = gitHubOAuthService.loginOrRegister(code);
            System.out.println(userInfo);
            if (userInfo != null) {
                // 登录成功，跳转首页
                log.info("GitHub 用户登录成功");
                redirectAttributes.addFlashAttribute("loginUser", userInfo);
                return "redirect:http://gulimall.com";
            } else {
                log.error("GitHub 用户登录失败");
                return "redirect:http://auth.gulimall.com/login.html";
            }
        } catch (OAuthException e) {
            redirectAttributes.addFlashAttribute("errors", e.getMessage());
            return "redirect:http://auth.gulimall.com/login.html";
        }
    }
}
