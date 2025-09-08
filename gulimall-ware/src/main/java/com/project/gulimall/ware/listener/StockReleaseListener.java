package com.project.gulimall.ware.listener;

import com.project.common.to.mq.OrderReleaseTo;
import com.project.common.to.mq.StockLockedTo;
import com.project.gulimall.ware.domain.entity.OrderEntity;
import com.project.gulimall.ware.service.WareSkuService;
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
@RabbitListener(queues = "stock.release.queue")
public class StockReleaseListener {

    @Autowired
    private WareSkuService wareSkuService;

    @RabbitHandler
    void releaseLockStock(StockLockedTo stockLockedTo, Message message, Channel channel) throws IOException {
        System.out.println("收到解锁库存消息...");
        try{
            wareSkuService.unLockStock(stockLockedTo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("处理解锁库存消息失败，消息内容: {}, 错误原因: ", stockLockedTo, e);
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }

    @RabbitHandler
    void releaseLockStockAfterOrderClosed(OrderReleaseTo orderReleaseTo, Message message, Channel channel) throws IOException {
        System.out.println("收到订单关闭消息，准备解锁库存...");
        try{
            wareSkuService.unLockStock(orderReleaseTo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }
}
