package com.bbende.messaging.ws.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@Configuration
public class WebSocketClientConfiguration {

    @Bean
    public WebSocketClient getWebSocketClient() {
        return new StandardWebSocketClient();
    }

    @Bean
    public WebSocketStompClient getWebSocketStompClient(final WebSocketClient webSocketClient) {
        final WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        return stompClient;
    }

}
