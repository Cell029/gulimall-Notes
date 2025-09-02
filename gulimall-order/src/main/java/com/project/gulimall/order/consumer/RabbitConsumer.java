package com.project.gulimall.order.consumer;

import com.project.gulimall.order.config.RabbitConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.rabbitmq.client.Channel;
import java.io.IOException;

@Slf4j
@Component
public class RabbitConsumer {

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void handleMessage(String msg, Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            log.info("收到消息: {}", msg);
            // 模拟业务逻辑异常
            if (msg.contains("Fail")) {
                throw new RuntimeException("模拟处理失败");
            }
            // 手动确认
            channel.basicAck(deliveryTag, false);
            log.info("消息确认成功");
        } catch (Exception e) {
            log.error("处理消息失败: {}", e.getMessage());
            // 否定确认，不重新入队
            channel.basicNack(deliveryTag, false, false);
            // channel.basicReject(deliveryTag, false);
        }
    }
}
