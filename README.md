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
