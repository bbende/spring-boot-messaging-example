package com.bbende.messaging.ws.client;

import com.bbende.messaging.Message;
import com.bbende.messaging.ws.WebSocketProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

@Component
@WebSocketProfile
public class WebSocketMessageHandler extends StompSessionHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketMessageHandler.class);

    private final String messagesTopic;

    public WebSocketMessageHandler(@Value("${stomp.messages.topic:/topic/messages}")
                                   final String messagesTopic) {
        this.messagesTopic = messagesTopic;
    }

    @Override
    public void afterConnected(final StompSession session, final StompHeaders connectedHeaders) {
        LOGGER.info("New session established: {}", session.getSessionId());
        session.subscribe(messagesTopic, this);
        LOGGER.info("Subscribed to: {}", messagesTopic);
    }

    @Override
    public void handleException(final StompSession session, final StompCommand command, final StompHeaders headers,
                                final byte[] payload, final Throwable exception) {
        LOGGER.error(exception.getMessage(), exception);
    }

    @Override
    public Type getPayloadType(final StompHeaders headers) {
        return Message.class;
    }

    @Override
    public void handleFrame(final StompHeaders headers, final Object payload) {
        final Message message = (Message) payload;
        LOGGER.info("WebSocketMessageHandler received message: {}", message.getMessage());
    }

}
