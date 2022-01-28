package com.bbende.messaging.ws;

import com.bbende.messaging.Message;
import com.bbende.messaging.rabbit.RabbitMessageRestController;
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
@WebSocketProfile
@RequestMapping("/websocket")
public class WebSocketMessageRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMessageRestController.class);

    private final SimpMessagingTemplate wsMessagingTemplate;
    private final String wsMessagesTopic;

    public WebSocketMessageRestController(final SimpMessagingTemplate simpMessagingTemplate,
                                 @Value("${stomp.messages.topic:/topic/messages}")
                                 final String messagesTopic) {
        this.wsMessagingTemplate = simpMessagingTemplate;
        this.wsMessagesTopic = messagesTopic;
    }

    @PostMapping(path = "/messages", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String publishMessageWithWebSocket(@RequestBody Message message) {
        LOGGER.info("REST Controller [/messages/ws] received message: {}", message.getMessage());
        LOGGER.info("Sending message [{}] to WebSocket destination [{}]", message.getMessage(), wsMessagesTopic);
        wsMessagingTemplate.convertAndSend(wsMessagesTopic, message);
        return message.getMessage();
    }

}
