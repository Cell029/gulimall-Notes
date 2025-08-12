package com.project.gulimall.product.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.project.gulimall.product.dao")
public class MyBatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        PaginationInnerInterceptor pagination = new PaginationInnerInterceptor(DbType.MYSQL);
        pagination.setOverflow(false); // 溢出页的处理：true/false，true 表示请求溢出页时返回第一页数据；false 表示返回空数据
        pagination.setMaxLimit(500L); // 单页最大条数（-1 不限制）
        interceptor.addInnerInterceptor(pagination);
        return interceptor;
    }
}
