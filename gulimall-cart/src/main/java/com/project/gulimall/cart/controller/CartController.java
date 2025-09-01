package com.project.gulimall.cart.controller;

import com.project.gulimall.cart.domain.to.LoginUserInfoTo;
import com.project.gulimall.cart.domain.vo.Cart;
import com.project.gulimall.cart.domain.vo.CartItem;
import com.project.gulimall.cart.interceptor.CartInterceptor;
import com.project.gulimall.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 浏览器里有一个 cookie，用来标识用户身份
     */
    @GetMapping("/cart.html")
    public String cartListPage(Model model) {
        Cart cart = cartService.getCart();
        model.addAttribute("cart", cart);
        return "cartList";
    }

    /**
     * 添加商品到购物车
     */
    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num, RedirectAttributes redirectAttributes) {
        CartItem cartItem = cartService.addToCart(skuId, num);
        redirectAttributes.addAttribute("skuId", skuId);
        return "redirect:http://cart.gulimall.com/addToCartSuccess.html";
    }

    @GetMapping("/addToCartSuccess.html")
    public String addToCartSuccessPage(@RequestParam("skuId") Long skuId, Model model) {
        // 通过 skuId 再查一遍购物车信息
        CartItem cartItem = cartService.getCartItem(skuId);
        model.addAttribute("cartItem", cartItem);
        return "success";
    }
}
