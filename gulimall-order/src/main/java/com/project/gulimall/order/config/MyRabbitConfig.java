package com.project.gulimall.order.config;

import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration
public class MyRabbitConfig {
    @Bean
    public MessageConverter messageConverter() {
        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
        jackson2JsonMessageConverter.setCreateMessageIds(true);
        return jackson2JsonMessageConverter;
    }

    /**
     * 自定义 RabbitTemplate，并设置回调
     */
    @Bean
    public RabbitTemplate rabbitTemplate(org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
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

