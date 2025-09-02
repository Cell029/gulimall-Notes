package com.project.gulimall.order.controller;

import com.project.gulimall.order.config.RabbitConfig;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@RestController
public class RabbitMQController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    // 正常路由
    @GetMapping("/sendOk")
    public String sendOk() {
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.ROUTING_KEY, "Hello RabbitMQ", new CorrelationData(UUID.randomUUID().toString()));
        return "消息发送成功（会进入队列）"; // 消息成功抵达Broker, 消息ID: f74dd02c-7ddb-40fc-801d-eb16015ad99a
    }

    // 路由失败（交换机存在，但 routingKey 不匹配）
    @GetMapping("/sendFail")
    public String sendFail() {
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, "wrong.key", "Hello RabbitMQ", new CorrelationData(UUID.randomUUID().toString()));
        return "消息发送失败（会触发 ReturnCallback）"; // 消息成功抵达Broker, 消息ID: 933849a5-a51d-402e-ab1b-56ec52d036a3
    }

    // 交换机不存在
    @GetMapping("/sendNoExchange")
    public String sendNoExchange() {
        rabbitTemplate.convertAndSend("no.exchange", RabbitConfig.ROUTING_KEY, "Hello RabbitMQ", new CorrelationData(UUID.randomUUID().toString()));
        return "消息发送失败（会触发 ConfirmCallback，ack=false）";
        // 消息被退回！路由失败。消息体: Hello RabbitMQ, 交换机: product-confirm-exchange, 路由键: wrong.key, 原因: NO_ROUTE
        // 消息未能抵达Broker, 消息ID: 609e8464-f73d-4590-9278-035b3bae67aa, 原因: channel error; protocol method: #method<channel.close>(reply-code=404, reply-text=NOT_FOUND - no exchange 'no.exchange' in vhost '/', class-id=60, method-id=40)
    }

    // 模拟手动确认
    @GetMapping("/sendMessageContainFail")
    public String sendMessageContainFail() {
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.ROUTING_KEY, "Fail", new CorrelationData(UUID.randomUUID().toString()));
        return "发送包含 Fail 字段的消息"; // 处理消息失败: 模拟处理失败
    }
}
