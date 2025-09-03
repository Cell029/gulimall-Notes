package com.project.gulimall.cart.controller;

import com.project.common.to.CartItemConfirmTo;
import com.project.gulimall.cart.domain.to.LoginUserInfoTo;
import com.project.gulimall.cart.domain.vo.Cart;
import com.project.gulimall.cart.domain.vo.CartItem;
import com.project.gulimall.cart.interceptor.CartInterceptor;
import com.project.gulimall.cart.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
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

    @GetMapping("/checkItem")
    public String checkItem(@RequestParam("skuId") Long skuId, @RequestParam("check") Integer check) {
        cartService.checkItem(skuId, check);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    @GetMapping("/countItem")
    public String countItem(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num) {
        cartService.changeItemCount(skuId, num);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("skuId") Long skuId) {
        cartService.deleteItem(skuId);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    @ResponseBody
    @GetMapping("/{userId}/getCurrentUserCartItems")
    public List<CartItem> getCurrentUserCartItems(@PathVariable Long userId, HttpServletRequest request) {
        Map<String, String> headers = Collections.list(request.getHeaderNames()).stream()
                .collect(Collectors.toMap(
                        name -> name,
                        request::getHeader
                ));
        log.debug("Incoming headers: {}", headers);
        return cartService.getCurrentUserCartItems(userId);
    }
}
