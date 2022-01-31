# Spring Boot Messaging Examples

Examples of using AMQP and WebSockets with an external RabbitMQ broker.

## Quick Start

Build RabbitMQ Docker image with STOMP plugin enabled:

```
docker build -t rabbitmq-stomp  docker-rabbitmq-stomp
```

Start RabbitMQ:

```
docker run -d -p 5672:5672 -p 15672:15672 -p 61613:61613 --name my-rabbit rabbitmq-stomp:latest
```

Verify accessing the RabbitMQ Admin UI:

- [http://localhost:15672](http://localhost:15672)
- Username: guest
- Password: guest

Start first application instance:

```
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8080 --application.id=app1"
```

Start second application instance:

```
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=7080 --application.id=app2"
```

Send a Rabbit message to first application instance:

```
curl -H "Content-Type: application/json" -X POST "http://localhost:8080/messages/rabbit" --data '{"message":"foo1"}'
```

Both instances of the application should log:

``` 
2022-01-26 13:19:28.106  INFO 59816 --- [ntContainer#0-1] c.bbende.messaging.rabbit.RabbitService  : RabbitListener received message: foo1
```

Send a WebSocket message to first application instance:

```
curl -H "Content-Type: application/json" -X POST "http://localhost:8080/messages/ws" --data '{"message":"foo2"}'
```

Both instances of the application should log:

``` 
2022-01-26 17:01:53.506  INFO 65523 --- [lient-AsyncIO-4] c.b.m.ws.client.WebSocketMessageHandler  : WebSocketMessageHandler received message: foo2
```

## TLS

To configure RabbitMQ for TLS, create the file `RABBIT_HOME/etc/rabbitmq/rabbitmq.conf` with the following content:

``` 
listeners.ssl.default = 5671
stomp.listeners.ssl.1 = 61614

ssl_options.cacertfile = /path/to/your-ca.pem
ssl_options.certfile   = /path/to/localhost-cert.pem
ssl_options.keyfile    = /path/to/localhost-key.pem
ssl_options.verify     = verify_peer
ssl_options.fail_if_no_peer_cert = true
```

This creates a TLS TCP listener on port `5671` for standard AMQP messaging, and a TLS STOMP listener on port `61614` for enabling TLS on the STOMP relay.

Launch the application setting `spring.profiles.active=rabbit,websocket,tls`.

Also provide the keystore and truststore properties by editing `application-tls.properties`, or by providing 
the following environment variables:

``` 
SPRING_RABBITMQ_SSL_KEYSTORE=file:///path/to/localhost-keystore.jks
SPRING_RABBITMQ_SSL_KEYSTORETYPE=JKS
SPRING_RABBITMQ_SSL_KEYSTOREPASSWORD=keystore-password
SPRING_RABBITMQ_SSL_TRUSTSTORE=file:///path/to/localhost-truststore.jks
SPRING_RABBITMQ_SSL_TRUSTSTORETYPE=JKS
SPRING_RABBITMQ_SSL_TRUSTSTOREPASSWORD=truststore-password
```