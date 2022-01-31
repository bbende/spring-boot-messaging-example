package com.bbende.messaging.ws.broker;

import com.bbende.messaging.ws.WebSocketProfile;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.config.StompBrokerRelayRegistration;
import org.springframework.messaging.simp.stomp.StompReactorNettyCodec;
import org.springframework.messaging.tcp.TcpOperations;
import org.springframework.messaging.tcp.reactor.ReactorNettyTcpClient;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.security.KeyStore;

@Configuration
@WebSocketProfile
@EnableWebSocketMessageBroker
public class WebSocketBrokerConfiguration implements WebSocketMessageBrokerConfigurer {

    private static Logger LOGGER = LoggerFactory.getLogger(WebSocketBrokerConfiguration.class);

    private final String stompEndpoint;
    private final String relayHost;
    private final int relayPort;

    private String keystoreFilename;
    private String keystorePass;
    private String keystoreType;
    private String truststoreFilename;
    private String truststorePass;
    private String truststoreType;

    public WebSocketBrokerConfiguration(@Value("${stomp.relay.host:localhost}")
                                        final String relayHost,
                                        @Value("${stomp.relay.port:61613}")
                                        final int relayPort,
                                        @Value("${stomp.endpoint:/ws}")
                                        final String stompEndpoint,
                                        @Value("${spring.rabbitmq.ssl.keyStore:}")
                                        final String keystoreFilename,
                                        @Value("${spring.rabbitmq.ssl.keyStorePassword:}")
                                        final String keystorePass,
                                        @Value("${spring.rabbitmq.ssl.keyStoreType:}")
                                        final String keystoreType,
                                        @Value("${spring.rabbitmq.ssl.trustStore:}")
                                        final String truststoreFilename,
                                        @Value("${spring.rabbitmq.ssl.trustStorePassword:}")
                                        final String truststorePass,
                                        @Value("${spring.rabbitmq.ssl.trustStoreType:}")
                                        final String truststoreType) {
        this.relayHost = relayHost;
        this.relayPort = relayPort;
        this.stompEndpoint = stompEndpoint;
        this.keystoreFilename = keystoreFilename;
        this.keystorePass = keystorePass;
        this.keystoreType = keystoreType;
        this.truststoreFilename = truststoreFilename;
        this.truststorePass = truststorePass;
        this.truststoreType = truststoreType;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        try {
            LOGGER.info("Configuring STOMP Broker relay with host [{}] and port [{}]", relayHost, relayPort);

            final StompBrokerRelayRegistration stompBrokerRelayRegistration =
                    config.enableStompBrokerRelay("/queue/", "/topic/")
                            .setRelayHost(relayHost)
                            .setRelayPort(relayPort);

            final KeyManagerFactory keyManagerFactory = getKeyManagerFactory();
            final TrustManagerFactory trustManagerFactory = getTrustManagerFactory();
            if (keyManagerFactory != null || trustManagerFactory != null) {
                LOGGER.info("Configuring STOMP Broker relay with SSLContext...");

                final SslContext sslContext = SslContextBuilder.forClient()
                        .keyManager(keyManagerFactory)
                        .trustManager(trustManagerFactory)
                        .build();

                final TcpOperations<byte[]> tcpOperations = new ReactorNettyTcpClient<>(
                        tc -> tc.host(relayHost)
                                .port(relayPort)
                                .secure(sslContextSpec -> sslContextSpec.sslContext(sslContext)),
                        new StompReactorNettyCodec()
                );

                stompBrokerRelayRegistration.setTcpClient(tcpOperations);
            }
        } catch (Throwable t) {
            throw new RuntimeException(t.getMessage(), t);
        }
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(stompEndpoint);
    }

    private KeyManagerFactory getKeyManagerFactory() throws Exception {
        if (!StringUtils.hasText(keystoreFilename) || !StringUtils.hasText(keystorePass) || !StringUtils.hasText(keystoreType)) {
            return null;
        }

        final KeyStore keyStore = KeyStore.getInstance(keystoreType);
        try (final InputStream keyStoreStream = new FileInputStream(new File(new URI(keystoreFilename)))) {
            keyStore.load(keyStoreStream, keystorePass.toCharArray());
        }
        final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, keystorePass.toCharArray());
        return keyManagerFactory;
    }

    private TrustManagerFactory getTrustManagerFactory() throws Exception {
        if (!StringUtils.hasText(truststoreFilename) || !StringUtils.hasText(truststorePass) || !StringUtils.hasText(truststoreType)) {
            return null;
        }

        final KeyStore trustStore = KeyStore.getInstance(truststoreType);
        try (final InputStream trustStoreStream = new FileInputStream(new File(new URI(truststoreFilename)))) {
            trustStore.load(trustStoreStream, truststorePass.toCharArray());
        }
        final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);
        return trustManagerFactory;
    }

}
