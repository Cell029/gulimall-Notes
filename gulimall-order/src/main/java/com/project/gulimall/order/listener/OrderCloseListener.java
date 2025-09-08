package com.project.gulimall.order.listener;

import com.project.gulimall.order.domain.entity.OrderEntity;
import com.project.gulimall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RabbitListener(queues = "order.release.queue")
public class OrderCloseListener {

    @Autowired
    private OrderService orderService;

    @RabbitHandler
    public void listener(OrderEntity orderEntity, Channel channel, Message message) throws IOException {
        log.info("收到订单超时消息，订单号：{}", orderEntity.getOrderSn());
        // 简单的取消订单逻辑
        if (orderEntity.getStatus() == 0) { // 0 表示待付款
            try {
                log.info("执行订单取消操作，订单ID：{}", orderEntity.getId());
                orderService.closeOrder(orderEntity);
                System.out.println("订单 " + orderEntity.getOrderSn() + " 因超时未支付已自动取消");
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            } catch (Exception e) {
                log.error("接收消息失败：{}", orderEntity.getId());
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
            }
        } else {
            log.info("订单已支付，无需处理，订单号：{}", orderEntity.getOrderSn());
        }
    }
}
