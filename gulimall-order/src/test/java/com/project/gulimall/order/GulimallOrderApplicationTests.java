package com.project.gulimall.order;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@Slf4j
@SpringBootTest
class GulimallOrderApplicationTests {

	@Autowired
	private AmqpAdmin amqpAdmin;
	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Test
	void createExchange() {
		/**
		 * public DirectExchange(String name, boolean durable, boolean autoDelete) {
		 *     super(name, durable, autoDelete);
	     * }
		 */
		DirectExchange directExchange = new DirectExchange("hello-direct-exchange", true, false);
		amqpAdmin.declareExchange(directExchange);
		log.info("交换机[{}]创建成功", directExchange.getName());
	}

	@Test
	void createQueue() {
		/**
		 * public Queue(String name, boolean durable, boolean exclusive, boolean autoDelete) {
		 *     this(name, durable, exclusive, autoDelete, (Map)null);
		 * }
		 */
		Queue queue = new Queue("hello-queue", true, false, false);
		amqpAdmin.declareQueue(queue);
		log.info("队列[{}]创建成功", queue.getName());
	}

	@Test
	void createBinding() {
		/**
		 * public Binding(String destination, DestinationType destinationType, String exchange, String routingKey, @Nullable Map<String, Object> arguments) {
		 *     super(arguments);
		 *     this.destination = destination;
		 *     this.destinationType = destinationType;
		 *     this.exchange = exchange;
		 *     this.routingKey = routingKey;
		 * }
		 */
		Binding binding = new Binding("hello-queue", Binding.DestinationType.QUEUE, "hello-direct-exchange", "hello", null);
		amqpAdmin.declareBinding(binding);
		log.info("绑定[{}]创建成功", binding.toString()); // 绑定[Binding [destination=hello-queue, exchange=hello-direct-exchange, routingKey=hello, arguments={}]]创建成功
	}

	@Test
	void sendMsg() {
		/**
		 * public void convertAndSend(String exchange, String routingKey, Object object) throws AmqpException {
		 *     this.convertAndSend(exchange, routingKey, object, (CorrelationData)null);
		 * }
		 */
		rabbitTemplate.convertAndSend("hello-direct-exchange", "hello", "hello world");
	}


}
