package com.project.gulimall.order.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "gulimall.thread")
public class ThreadPoolConfigProperties {
    private Integer corePoolSize;
    private Integer maxPoolSize;
    private Integer keepAliveTime;
}
