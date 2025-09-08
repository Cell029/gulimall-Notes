package com.project.gulimall.order.config;

import com.project.gulimall.order.domain.entity.OrderEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Component
public class MyRabbitMQInitializer {

    @Bean
    public Queue orderDelayQueue() {
        /**
         * x-dead-letter-exchange
         * x-dead-letter-routing-key
         * x-message-ttl
         */
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("x-dead-letter-exchange", "order-event-exchange"); // 指定死信交换机
        args.put("x-dead-letter-routing-key", "order.release.order"); // 指定死信路由键
        args.put("x-message-ttl", 60000); // 设置TTL

        /**
         * public Queue(String name, boolean durable, boolean exclusive, boolean autoDelete, @Nullable Map<String, Object> arguments);
         */
        return new Queue("order.delay.queue", true, false, false, args);
    }

    @Bean
    public Queue orderReleaseQueue() {
        return new Queue("order.release.queue", true, false, false);
    }

    @Bean
    public Exchange orderEventExchange() {
        /**
         * public TopicExchange(String name, boolean durable, boolean autoDelete, Map<String, Object> arguments);
         */
        return new TopicExchange("order-event-exchange", true, false);
    }

    @Bean
    public Binding orderCreateBinding() {
        /**
         * public Binding(String destination, DestinationType destinationType, String exchange, String routingKey, @Nullable Map<String, Object> arguments);
         */
        return new Binding("order.delay.queue", Binding.DestinationType.QUEUE, "order-event-exchange", "order.create.order", null);
    }

    @Bean
    public Binding orderReleaseBinding() {
        return new Binding("order.release.queue", Binding.DestinationType.QUEUE, "order-event-exchange", "order.release.order", null);
    }

    /**
     * 订单释放和库存释放也进行绑定
     */
    @Bean
    public Binding orderReleaseOtherBinding() {
        return new Binding("stock.release.queue", Binding.DestinationType.QUEUE, "order-event-exchange", "order.release.other", null);
    }

}
