package com.bbende.messaging.rabbit;

import com.bbende.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RabbitProfile
@RequestMapping("/rabbit")
public class RabbitMessageRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMessageRestController.class);

    private final RabbitService rabbitService;

    public RabbitMessageRestController(final RabbitService rabbitService) {
        this.rabbitService = rabbitService;
    }

    @PostMapping(path = "/messages", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String publishMessageWithRabbit(@RequestBody Message message) {
        LOGGER.info("REST Controller [/messages/rabbit] received message: {}", message.getMessage());
        rabbitService.sendMessage(message);
        return message.getMessage();
    }

}
