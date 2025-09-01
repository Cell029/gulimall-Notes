package com.project.gulimall.cart.service;

import com.project.gulimall.cart.domain.vo.Cart;
import com.project.gulimall.cart.domain.vo.CartItem;

public interface CartService {
    CartItem addToCart(Long skuId, Integer num);

    CartItem getCartItem(Long skuId);

    Cart getCart();

}
