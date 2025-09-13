package com.project.gulimall.member;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Example {

    public static String getMD5(String input) {
        try {
            // 1. 获取 MD5 摘要器实例
            MessageDigest md = MessageDigest.getInstance("MD5");

            // 2. 将输入字符串转换为字节数组，并计算哈希值
            byte[] messageDigest = md.digest(input.getBytes());

            // 3. 将字节数组（16字节）转换为 32 位的十六进制字符串
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);

            // 4. 确保生成的字符串是 32 位长（前面可能补0）
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        String s = "hello world";
        System.out.println("Your HashCode for '" + s + "' is: " + getMD5(s));
        // 输出: 5eb63bbbe01eeed093cb22bb8f5acdc3

        String s2 = "hello world.";
        System.out.println("Your HashCode for '" + s2 + "' is: " + getMD5(s2));
        // 输出一个完全不同的值：3c4292ae95be58e0c58e4e5511f09647
    }
}