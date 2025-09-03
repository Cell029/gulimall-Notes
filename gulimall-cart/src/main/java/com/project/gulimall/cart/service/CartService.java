package com.project.gulimall.cart.service;

import com.project.common.to.CartItemConfirmTo;
import com.project.gulimall.cart.domain.vo.Cart;
import com.project.gulimall.cart.domain.vo.CartItem;

import java.util.List;

public interface CartService {
    CartItem addToCart(Long skuId, Integer num);

    CartItem getCartItem(Long skuId);

    Cart getCart();

    void checkItem(Long skuId, Integer check);

    void changeItemCount(Long skuId, Integer num);

    void deleteItem(Long skuId);

    List<CartItem> getCurrentUserCartItems(Long userId);
}
