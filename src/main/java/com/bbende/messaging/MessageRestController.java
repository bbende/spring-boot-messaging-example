package com.bbende.messaging;

import com.bbende.messaging.rabbit.RabbitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/messages")
public class MessageRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageRestController.class);

    private final RabbitService rabbitService;
    private final SimpMessagingTemplate wsMessagingTemplate;
    private final String wsMessagesTopic;

    public MessageRestController(final RabbitService rabbitService,
                                 final SimpMessagingTemplate simpMessagingTemplate,
                                 @Value("${stomp.messages.topic:/topic/messages}")
                                 final String messagesTopic) {
        this.rabbitService = rabbitService;
        this.wsMessagingTemplate = simpMessagingTemplate;
        this.wsMessagesTopic = messagesTopic;
    }

    @PostMapping(path = "/rabbit", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String publishMessageWithRabbit(@RequestBody Message message) {
        LOGGER.info("REST Controller [/messages/rabbit] received message: {}", message.getMessage());
        rabbitService.sendMessage(message);
        return message.getMessage();
    }

    @PostMapping(path = "/ws", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String publishMessageWithWebSocket(@RequestBody Message message) {
        LOGGER.info("REST Controller [/messages/ws] received message: {}", message.getMessage());
        LOGGER.info("Sending message [{}] to WebSocket destination [{}]", message.getMessage(), wsMessagesTopic);
        wsMessagingTemplate.convertAndSend(wsMessagesTopic, message);
        return message.getMessage();
    }

}
