package com.bbende.messaging.ws.broker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketBrokerConfiguration implements WebSocketMessageBrokerConfigurer {

    private final String stompEndpoint;
    private final String relayHost;
    private final int relayPort;

    public WebSocketBrokerConfiguration(@Value("${stomp.relay.host:localhost}")
                                        final String relayHost,
                                        @Value("${stomp.relay.port:61613}")
                                        final int relayPort,
                                        @Value("${stomp.endpoint:/ws}")
                                        final String stompEndpoint) {
        this.relayHost = relayHost;
        this.relayPort = relayPort;
        this.stompEndpoint = stompEndpoint;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableStompBrokerRelay("/queue/", "/topic/")
                .setRelayHost(relayHost)
                .setRelayPort(relayPort);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(stompEndpoint);
    }

}
