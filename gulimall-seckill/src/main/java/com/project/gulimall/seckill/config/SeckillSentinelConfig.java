package com.project.gulimall.seckill.config;

import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlBlockHandler;
import com.alibaba.csp.sentinel.adapter.servlet.callback.WebCallbackManager;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson2.JSON;
import com.project.common.exception.BizCodeEnum;
import com.project.common.utils.R;
import org.springframework.context.annotation.Configuration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class SeckillSentinelConfig {
    public SeckillSentinelConfig() {
        WebCallbackManager.setUrlBlockHandler(new UrlBlockHandler() {
            @Override
            public void blocked(HttpServletRequest request, HttpServletResponse response, BlockException e) throws IOException {
                response.setContentType("application/json;charset=UTF-8");
                R error = R.error(BizCodeEnum.TOO_MANY_REQUEST.getCode(),
                        BizCodeEnum.TOO_MANY_REQUEST.getMsg());
                response.getWriter().write(JSON.toJSONString(error));
            }
        });
    }
}
