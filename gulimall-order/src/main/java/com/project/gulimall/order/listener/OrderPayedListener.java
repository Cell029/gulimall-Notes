package com.project.gulimall.order.listener;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.project.gulimall.order.config.AlipayTemplate;
import com.project.gulimall.order.domain.entity.OrderEntity;
import com.project.gulimall.order.domain.vo.PayAsyncVo;
import com.project.gulimall.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@RestController
public class OrderPayedListener {

    @Autowired
    private OrderService orderService;
    @Autowired
    private AlipayTemplate alipayTemplate;

    @PostMapping("/order/payed/notify")
    public String handleAliPayed(PayAsyncVo payAsyncVo, HttpServletRequest request) throws AlipayApiException, UnsupportedEncodingException {
        Map<String, String[]> map = request.getParameterMap();

        for (String key : map.keySet()) {
            System.out.println(key + "=" + request.getParameter(key));
        }
        //验签，获取支付宝POST过来反馈信息
        Map<String,String> params = new HashMap<String,String>();
        Map<String,String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            // valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayTemplate.getAlipay_public_key(), alipayTemplate.getCharset(), alipayTemplate.getSign_type()); //调用SDK验证签名
        System.out.println("签名验证结果: " + signVerified);
        if (signVerified) {
            System.out.println("签名验证成功！");
            String result = orderService.handlePayResult(payAsyncVo);
            return result;
        } else {
            System.out.println("签名验证失败！");
            return "error";
        }
    }
}
