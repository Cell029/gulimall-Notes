package com.gulimall.gulimallauthserver.controller;

import com.alibaba.fastjson.TypeReference;
import com.gulimall.gulimallauthserver.domain.vo.UserLoginVo;
import com.gulimall.gulimallauthserver.feign.MemberFeignService;
import com.gulimall.gulimallauthserver.service.SendCodeService;
import com.project.common.constant.LoginConstant;
import com.project.common.domain.vo.MemberResponseVo;
import com.project.common.domain.vo.UserRegisterVo;
import com.project.common.exception.BizCodeEnum;
import com.project.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class LoginController {
    @Autowired
    private SendCodeService sendCodeService;
    @Autowired
    private MemberFeignService memberFeignService;

    @ResponseBody
    @GetMapping("/sms/sendcode")
    public R sendCode(@RequestParam("phone") String phone) {
        boolean result = sendCodeService.sendCode(phone);
        if (!result) {
           return R.error(BizCodeEnum.SMS_CODE_EXCEPTION.getCode(), BizCodeEnum.SMS_CODE_EXCEPTION.getMsg());
        }
        return R.ok();
    }

    @PostMapping("/register")
    public String register(@Valid UserRegisterVo userRegisterVo, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        // 如果校验出错就跳转到注册页面
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = bindingResult.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.gulimall.com/reg.html";
        } else {
            boolean result = sendCodeService.checkCode(userRegisterVo.getPhone(), userRegisterVo.getCode());
            if (!result) {
                Map<String, String> errors = new HashMap<>();
                errors.put("code", "验证码错误");
                redirectAttributes.addFlashAttribute("errors", errors);
                // 校验出错，转发到注册页
                return "redirect:http://auth.gulimall.com/reg.html";
            } else {
                R r = memberFeignService.register(userRegisterVo);
                log.debug("注册结果：{}", r);
                if (r.getCode() == 0) {
                    // 注册成功回到登录页
                    return "redirect:http://auth.gulimall.com/login.html";
                } else {
                    Map<String, String> errors = new HashMap<>();
                    errors.put("msg", r.getData("msg", new TypeReference<String>() {}));
                    redirectAttributes.addFlashAttribute("errors", errors);
                    return "redirect:http://auth.gulimall.com/reg.html";
                }
            }
        }
    }

    @PostMapping("/login")
    public String login(UserLoginVo userLoginVo, RedirectAttributes redirectAttributes, HttpSession session) {
        // 调用远程登录
        R r = memberFeignService.login(userLoginVo);
        if (r.getCode() == 0) {
            // 成功
            MemberResponseVo memberEntity = r.getData("memberEntity", new TypeReference<MemberResponseVo>() {
            });
            // 登录成功就将数据存到 spring session 中（实际在 redis 中）
            session.setAttribute(LoginConstant.LOGIN_USER, memberEntity);
            return "redirect:http://gulimall.com";
        } else {
            Map<String, String> errors = new HashMap<>();
            errors.put("msg", r.getData("msg", new TypeReference<String>() {}));
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.gulimall.com/login.html";
        }
    }

    /**
     * 进入登录页面时，如果 session 存在，那么就直接跳转到首页，而不是登录页面
     */
    @GetMapping("/login.html")
    public String loginPage(HttpSession session) {
        if (session.getAttribute(LoginConstant.LOGIN_USER) != null) {
            return "redirect:http://gulimall.com";
        } else {
            return "login";
        }
    }

}

