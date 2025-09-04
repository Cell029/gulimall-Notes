package com.project.gulimall.order.web;

import com.project.gulimall.order.domain.vo.OrderSubmitVo;
import com.project.gulimall.order.domain.vo.SubmitOrderResponseVo;
import com.project.gulimall.order.service.OrderService;
import com.project.gulimall.order.domain.vo.OrderConfirmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class OrderWebController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/toTrade")
    public String toTrade(Model model) {
        OrderConfirmVo orderConfirmVo = orderService.confirmOrder();
        model.addAttribute("orderConfirmData", orderConfirmVo);
        return "confirm";
    }

    @PostMapping("/submitOrder")
    public String submitOrder(@RequestBody OrderSubmitVo orderSubmitVo, Model model) {
        SubmitOrderResponseVo submitOrderResponseVo = orderService.submitOrder(orderSubmitVo);
        if (submitOrderResponseVo.getCode() == 0) {
            // 提交订单成功，跳转到支付页
            return "pay";
        } else {
            // 失败跳回订单确认页
            return "redirect://order.gulimall.com/toTrade";
        }
    }
}
