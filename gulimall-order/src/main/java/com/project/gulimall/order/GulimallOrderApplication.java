package com.project.gulimall.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


@EnableRabbit
@EnableFeignClients
@SpringBootApplication
@EnableDiscoveryClient
@EnableRedisHttpSession
@MapperScan("com.project.gulimall.order.dao")
public class GulimallOrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(GulimallOrderApplication.class, args);
	}

}
