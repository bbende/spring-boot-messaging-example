package com.bbende.messaging.rabbit;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RabbitProfile
public class RabbitConfig {

    public static final String EXCHANGE_NAME = "test-exchange";
    public static final String QUEUE_NAME = "test-queue";

    @Bean
    public MessageConverter getMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
