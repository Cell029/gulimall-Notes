package com.project.gulimall.order;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Producer {
    private final static String DELAYED_EXCHANGE_NAME = "my-delayed-exchange";
    private final static String ROUTING_KEY = "my.routing.key";

    public static void main(String[] argv) throws Exception {
        // 1. 创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();

        // 2. 配置连接信息
        factory.setHost("localhost"); // RabbitMQ服务器主机名
        factory.setUsername("admin");
        factory.setPassword("admin");
        factory.setPort(5672);

        // 3. 建立连接和通道
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            // 4. 准备创建延迟交换机所需的参数
            // 最后一个参数 Map 中必须包含一个键为 "x-delayed-type" 的数据
            Map<String, Object> exchangeArgs = new HashMap<>();
            exchangeArgs.put("x-delayed-type", "direct");

            // 5. 声明延迟交换机
            // exchangeDeclare(String exchange, String type, boolean durable, boolean autoDelete, Map<String, Object> arguments)
            channel.exchangeDeclare(
                    DELAYED_EXCHANGE_NAME, // 交换机名称
                    "x-delayed-message",   // 交换机类型，由延迟插件提供
                    true, // durable: 持久化，服务器重启后交换机仍存在
                    false, // autoDelete: 不自动删除（当所有队列解绑后不删除交换机）
                    exchangeArgs // arguments: 参数，包含关键的 x-delayed-type
            );
            System.out.println("延迟交换机 '" + DELAYED_EXCHANGE_NAME + "' 声明成功！");

            // 6. 准备要发送的消息内容
            String message = "这是一条测试延迟消息！";
            System.out.println("准备发送消息: '" + message + "'");

            // 7. 构建消息属性，设置延迟时间
            // 通过在消息头中设置 x-delay 参数来指定每条消息的延迟时间
            Map<String, Object> headers = new HashMap<>();
            headers.put("x-delay", 10000); // 延迟 10 秒 (10 * 1000 ms)

            AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                    .headers(headers) // 设置自定义头，这是实现延迟的关键
                    .contentType("text/plain") // 设置内容类型
                    .deliveryMode(2)  // 设置消息为持久化 (2 = persistent)
                    .build();

            // 8. 发送消息到延迟交换机
            // basicPublish(String exchange, String routingKey, BasicProperties props, byte[] body)
            channel.basicPublish(
                    DELAYED_EXCHANGE_NAME,
                    ROUTING_KEY, // routingKey: 路由键，用于消息路由
                    props, // props: 包含延迟时间等属性的对象
                    message.getBytes(StandardCharsets.UTF_8) // body: 消息体字节数组
            );
            System.out.println("[x] 消息已发送，它将在 10 秒后被投递。");
        } catch (Exception e) {
            System.err.println("发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
