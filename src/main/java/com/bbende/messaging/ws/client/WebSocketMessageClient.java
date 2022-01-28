package com.bbende.messaging.ws.client;

import com.bbende.messaging.ws.WebSocketProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import javax.annotation.PreDestroy;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@WebSocketProfile
public class WebSocketMessageClient implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketMessageClient.class);

    private final String wsClientUrl;
    private final WebSocketStompClient stompClient;
    private final WebSocketMessageHandler messageHandler;
    private final AtomicBoolean connected = new AtomicBoolean(false);

    public WebSocketMessageClient(@Value("${ws.client.url:ws://localhost:${server.port}${stomp.endpoint}}")
                                  final String wsClientUrl,
                                  final WebSocketStompClient stompClient,
                                  final WebSocketMessageHandler messageHandler) {
        this.wsClientUrl = wsClientUrl;
        this.stompClient = stompClient;
        this.messageHandler = messageHandler;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        connect();
    }

    public synchronized void connect() {
        if (connected.get()) {
            LOGGER.info("Already connected to {}", wsClientUrl);
        }

        LOGGER.info("Connecting to {}", wsClientUrl);
        stompClient.connect(wsClientUrl, messageHandler);
        connected.set(true);
    }

    @PreDestroy
    public synchronized void shutdown() {
        LOGGER.info("Stopping StompClient...");
        try {
            stompClient.stop();
        } catch (Throwable t) {
            LOGGER.warn(t.getMessage(), t);
        } finally {
            connected.set(false);
        }
    }
}
