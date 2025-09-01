package com.project.gulimall.cart.domain.vo;

import java.math.BigDecimal;
import java.util.List;

public class Cart {
    List<CartItem> cartItems;
    private Integer countNum;
    private Integer countType;
    private BigDecimal totalAmount; // 商品总价
    private BigDecimal reduceAmount = new BigDecimal("0.00"); // 优惠价格

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public Integer getCountNum() {
        int count = 0;
        if (!cartItems.isEmpty()) {
            for (CartItem cartItem : cartItems) {
                count += cartItem.getCount();
            }
        }
        return count;
    }


    public Integer getCountType() {
        int count = 0;
        if (!cartItems.isEmpty()) {
            for (CartItem cartItem : cartItems) {
                count += 1;
            }
        }
        return count;
    }

    public BigDecimal getTotalAmount() {
        BigDecimal amount = new BigDecimal("0.00");
        // 1. 计算购物项总价
        if (!cartItems.isEmpty()) {
            for (CartItem cartItem : cartItems) {
                if (cartItem.getCount() > 0 && cartItem.getCheck()) {
                    BigDecimal totalPrice = cartItem.getTotalPrice();
                    amount = amount.add(totalPrice);
                }
            }
        }
        // 2. 减去优惠价
        BigDecimal subtract = amount.subtract(this.getReduceAmount());
        return subtract;
    }

    public BigDecimal getReduceAmount() {
        return reduceAmount;
    }

    public void setReduceAmount(BigDecimal reduceAmount) {
        this.reduceAmount = reduceAmount;
    }
}
