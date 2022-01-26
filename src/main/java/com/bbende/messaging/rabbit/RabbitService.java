package com.bbende.messaging.rabbit;

import com.bbende.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class RabbitService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitService.class);

    private RabbitTemplate rabbitTemplate;

    public RabbitService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessage(Message message) {
        LOGGER.info("RabbitMQService sending message [{}] to queue [{}]", message.getMessage(), RabbitConfig.QUEUE_NAME);
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_NAME, "", message);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(RabbitConfig.QUEUE_NAME + "-${application.id}"),
            exchange = @Exchange(name = RabbitConfig.EXCHANGE_NAME, type = ExchangeTypes.FANOUT)
    ))
    public void receiveMessage(Message message) {
        LOGGER.info("RabbitListener received message: {}", message.getMessage());
    }

}
