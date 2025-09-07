package com.project.gulimall.ware.listener;

import com.alibaba.fastjson.TypeReference;
import com.project.common.to.mq.StockDetailTo;
import com.project.common.to.mq.StockLockedTo;
import com.project.common.utils.R;
import com.project.gulimall.ware.domain.entity.OrderEntity;
import com.project.gulimall.ware.domain.entity.WareOrderTaskDetailEntity;
import com.project.gulimall.ware.domain.entity.WareOrderTaskEntity;
import com.project.gulimall.ware.feign.OrderFeignService;
import com.project.gulimall.ware.service.WareOrderTaskDetailService;
import com.project.gulimall.ware.service.WareOrderTaskService;
import com.project.gulimall.ware.service.WareSkuService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

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
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }
}
