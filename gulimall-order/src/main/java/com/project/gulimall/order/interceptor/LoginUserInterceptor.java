package com.project.gulimall.order.interceptor;

import com.project.common.domain.vo.MemberResponseVo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginUserInterceptor implements HandlerInterceptor {

    public static ThreadLocal<MemberResponseVo> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestURI = request.getRequestURI();
        boolean match = new AntPathMatcher().match("/order/order/status", requestURI);
        if (match) {
            return true;
        }

        MemberResponseVo loginUser = (MemberResponseVo) request.getSession().getAttribute("loginUser");
        if (loginUser != null) {
            threadLocal.set(loginUser);
            return true;
        } else {
            // 没登录就去登录
            request.getSession().setAttribute("noLoginMsg", "请先进行登录!");
            response.sendRedirect("http://auth.gulimall.com/login.html");
            return false;
        }
    }
}
