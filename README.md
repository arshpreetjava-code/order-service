# Order Service

Short description

This microservice accepts order requests and publishes `order-created` events to Kafka for downstream processing.

Key info
- Java: 21 (project configured with <java.version>21</java.version>)
- Spring Boot: 3.5.7
- Kafka topics used: `order-created`
- HTTP endpoints: `POST /api/order` (create order)
- Swagger UI (OpenAPI): `/swagger-ui.html` or `/swagger-ui/index.html`

Prerequisites
- Java 21 installed and JAVA_HOME set
- Maven (or use the included Maven wrapper `mvnw.cmd` on Windows)
- A running Kafka cluster accessible to the service

Configuration
- The service reads Kafka bootstrap server and consumer group id from `src/main/resources/env.properties` which is imported by `application.yml`.
- Default values (in `src/main/resources/env.properties`):

```
BOOTSTRAP_SERVER=ec2-13-201-72-156.ap-south-1.compute.amazonaws.com:9092
GROUP_ID=order-service-group
```

- The `application.yml` sets the HTTP server port to `8080`. You can override the Kafka bootstrap server by setting `BOOTSTRAP_SERVER` environment variable or editing `env.properties`.

Build and run

Using the Maven wrapper (Windows):

```bash
cd order-service-main
mvnw.cmd -DskipTests package
java -jar target/order-service-0.0.1-SNAPSHOT.jar
```

Or use `mvn` if installed:

```bash
mvn -DskipTests package
java -jar target/order-service-0.0.1-SNAPSHOT.jar
```

Swagger and API docs

When the service is running, OpenAPI JSON and Swagger UI are available at:

- http://localhost:8080/v3/api-docs
- http://localhost:8080/swagger-ui.html
- http://localhost:8080/swagger-ui/index.html

HTTP API

Create order
- URL: POST /api/order
- Content-Type: application/json
- Example payload:

```json
{
  "orderId": "order-123",
  "userId": "user-1",
  "address": "123 Main St",
  "food": {
    "type": "pizza",
    "name": "Margherita",
    "toppings": ["cheese", "basil"],
    "quantity": 1,
    "price": 299
  }
}
```

- Response: OrderEvent JSON (the created order) and HTTP 200

Kafka

This service publishes to the `order-created` topic. Ensure downstream services are subscribed to that topic.

Troubleshooting

- If the service cannot connect to Kafka, ensure `BOOTSTRAP_SERVER` is reachable and correct.
- To change the HTTP port, edit `application.yml` `server.port` or pass `--server.port` as a JVM arg.