package com.project.gulimall.order.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String EXCHANGE = "product-confirm-exchange";
    public static final String QUEUE = "product-confirm-queue";
    public static final String ROUTING_KEY = "product-confirm";

    @Bean
    public DirectExchange demoExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue demoQueue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    public Binding demoBinding() {
        return BindingBuilder.bind(demoQueue()).to(demoExchange()).with(ROUTING_KEY);
    }
}
