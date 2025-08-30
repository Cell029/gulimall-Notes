package com.gulimall.gulimallauthserver.excetpion;

public class OAuthException extends RuntimeException {
    public OAuthException(String message) {
        super(message);
    }
}