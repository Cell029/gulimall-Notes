package com.project.gulimall.cart.domain.to;

import lombok.Data;

/**
 * 用来判断用户登录信息的对象
 */
@Data
public class LoginUserInfoTo {
    private Long userId;
    private String userKey;
    private boolean tempUser = false; // 标志位，判断是否有 userKey
}
