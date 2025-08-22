package com.project.gulimall.product.config;

import java.io.Serializable;

public class NullValue implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final NullValue INSTANCE = new NullValue();

    private NullValue() {}

    public String getType() { // 添加 getter
        return "NullValue";
    }
}
