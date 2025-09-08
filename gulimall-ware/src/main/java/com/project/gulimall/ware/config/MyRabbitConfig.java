package com.project.gulimall.ware.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@Configuration
public class MyRabbitConfig {

    @Bean
    public MessageConverter messageConverter() {
        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
        jackson2JsonMessageConverter.setCreateMessageIds(true);
        return jackson2JsonMessageConverter;
    }

    @Bean
    public Exchange stockEventExchange() {
        return new TopicExchange("stock-event-exchange", true, false);
    }

    @Bean
    public Queue stockReleaseQueue() {
        return new Queue("stock.release.queue", true, false, false);
    }

    @Bean
    public Queue stockLockedQueue() {
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("x-dead-letter-exchange", "stock-event-exchange"); // 指定死信交换机
        args.put("x-dead-letter-routing-key", "stock.release"); // 指定死信路由键
        args.put("x-message-ttl", 120000); // 设置 TTL
        return new Queue("stock.locked.queue", true, false, false, args);
    }

    @Bean
    public Binding stockReleaseBinding() {
        return new Binding("stock.release.queue",
                Binding.DestinationType.QUEUE,
                "stock-event-exchange",
                "stock.release",
                null);
    }

    @Bean
    public Binding stockLockedBinding() {
        return new Binding("stock.locked.queue",
                Binding.DestinationType.QUEUE,
                "stock-event-exchange",
                "stock.locked",
                null);
    }


    /**
     * 自定义 RabbitTemplate，并设置回调
     */
    @Bean
    public RabbitTemplate rabbitTemplate(org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter()); // 添加消息转换器
        rabbitTemplate.setMandatory(true);

        // ConfirmCallback：消息是否到达 Broker
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            String id = correlationData != null ? correlationData.getId() : "null";
            if (ack) {
                log.info("消息成功抵达Broker, 消息ID: {}", id);
            } else {
                log.error("消息未能抵达Broker, 消息ID: {}, 原因: {}", id, cause);
            }
        });

        // ReturnsCallback：消息是否路由到队列
        rabbitTemplate.setReturnsCallback(returned -> {
            log.error("消息被退回！路由失败。消息体: {}, 交换机: {}, 路由键: {}, 原因: {}",
                    new String(returned.getMessage().getBody()),
                    returned.getExchange(),
                    returned.getRoutingKey(),
                    returned.getReplyText());
        });
        return rabbitTemplate;
    }
}

