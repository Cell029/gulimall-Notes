package com.project.gulimall.seckill;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableRedisHttpSession
@MapperScan("com.project.gulimall.seckill.dao")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.project.gulimall.seckill.feign")
public class GulimallSeckillApplication {

	public static void main(String[] args) {
		SpringApplication.run(GulimallSeckillApplication.class, args);
	}

}
