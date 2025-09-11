package com.project.gulimall.order.listener;

import com.project.common.to.mq.SeckillOrderTo;
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
@RabbitListener(queues = "order.seckill.queue")
public class OrderSeckillListener {

    @Autowired
    private OrderService orderService;

    @RabbitHandler
    public void listener(SeckillOrderTo seckillOrderTo, Channel channel, Message message) throws IOException {
        log.info("收到秒杀订单消息，订单号：{}", seckillOrderTo.getOrderSn());
        try {
            orderService.createSeckillOrder(seckillOrderTo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("接收消息失败：{}", seckillOrderTo.getOrderSn());
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }
}

