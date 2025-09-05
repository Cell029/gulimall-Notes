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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String submitOrder(@RequestBody OrderSubmitVo orderSubmitVo, Model model, RedirectAttributes redirectAttributes) {
        SubmitOrderResponseVo submitOrderResponseVo = orderService.submitOrder(orderSubmitVo);
        if (submitOrderResponseVo.getCode() == 0) {
            // 提交订单成功，跳转到支付页
            model.addAttribute("submitOrderResponseVo", submitOrderResponseVo);
            return "pay";
        } else {
            String msg = "下单失败：";
            switch (submitOrderResponseVo.getCode()) {
                case 1: msg += "订单信息过期，请刷新后再提交！"; break;
                case 2: msg += "订单商品价格发生变化，请确认后再提交！"; break;
            }
            redirectAttributes.addFlashAttribute("msg", msg);
            // 失败跳回订单确认页
            return "redirect://order.gulimall.com/toTrade";
        }
    }
}
