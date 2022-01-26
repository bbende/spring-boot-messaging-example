package com.bbende.messaging;

import com.bbende.messaging.rabbit.RabbitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/messages")
public class MessageRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageRestController.class);

    private RabbitService rabbitService;

    public MessageRestController(RabbitService rabbitService) {
        this.rabbitService = rabbitService;
    }

    @PostMapping(path = "/rabbit", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String createMessage(@RequestBody Message message) {
        LOGGER.info("REST Controller [/messages/rabbit] received message: {}", message.getMessage());
        rabbitService.sendMessage(message);
        return message.getMessage();
    }

}
