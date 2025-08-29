package com.project.gulimall.member;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptExample {
    public static void main(String[] args) {
        // cost = 12，安全性和性能的平衡
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

        // 注册时：加密密码
        String rawPassword = "MySecurePassword123!";
        String hashedPassword = encoder.encode(rawPassword);

        System.out.println("原始密码: " + rawPassword);
        System.out.println("BCrypt哈希: " + hashedPassword);

        // 登录时：验证密码是否正确
        boolean matches = encoder.matches("MySecurePassword123!", hashedPassword);
        System.out.println("密码是否匹配: " + matches);
    }
}

