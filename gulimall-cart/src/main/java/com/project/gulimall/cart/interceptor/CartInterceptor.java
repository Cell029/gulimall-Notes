package com.project.gulimall.cart.interceptor;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.project.common.constant.CartConstant;
import com.project.common.constant.LoginConstant;
import com.project.common.domain.vo.MemberResponseVo;
import com.project.gulimall.cart.domain.to.LoginUserInfoTo;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

@Component
public class CartInterceptor implements HandlerInterceptor {

    public static ThreadLocal<LoginUserInfoTo> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        MemberResponseVo memberResponseVo = (MemberResponseVo) session.getAttribute(LoginConstant.LOGIN_USER);
        LoginUserInfoTo loginUserInfoTo = new LoginUserInfoTo();
        if (memberResponseVo != null) {
            // 用户登录
            loginUserInfoTo.setUserId(memberResponseVo.getId());
        }

        // 不管未登录还是登录，都设置 user-key
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                String cookieName = cookie.getName();
                if (cookieName.equals(CartConstant.TEMP_USER_COOKIE_NAME)) {
                    loginUserInfoTo.setUserKey(cookie.getValue());
                    loginUserInfoTo.setTempUser(true); // 获取到 user-key 就将标志位设为 true
                }
            }
        }

        // 如果没有分配临时用户，那就需要手动分配一个
        if (StringUtils.isEmpty(loginUserInfoTo.getUserKey())) {
            String uuid = UUID.randomUUID().toString();
            loginUserInfoTo.setUserKey(uuid);
            loginUserInfoTo.setTempUser(true);
        }

        threadLocal.set(loginUserInfoTo);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        LoginUserInfoTo loginUserInfoTo = threadLocal.get();
        if (loginUserInfoTo != null) {
            if (loginUserInfoTo.isTempUser()) {
                Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME, loginUserInfoTo.getUserKey());
                cookie.setDomain("gulimall.com");
                cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIMEOUT);
                response.addCookie(cookie);
            }
        }
    }
}
