# Spring Boot Messaging Examples

Examples of using AMQP and WebSockets with an external RabbitMQ broker.

## Quick Start

Start RabbitMQ:

```
docker run -d -p 5672:5672 -p 15672:15672 --name my-rabbit rabbitmq:3-management
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

Send a message to first application instance:

```
curl -H "Content-Type: application/json" -X POST "http://localhost:8080/messages/rabbit" --data '{"message":"foo1"}'
```

Both instances of the application should log:

``` 
2022-01-26 13:19:28.106  INFO 59816 --- [ntContainer#0-1] c.bbende.messaging.rabbit.RabbitService  : RabbitListener received message: foo1
```


