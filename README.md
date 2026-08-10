# Payment Service

REST API for payment management developed with Java 21 and Spring Boot.

The service allows payments to be created, their status to be retrieved and updated, and publishes an event to RabbitMQ whenever a payment status changes.

## Tech Stack

- Java 21
- Spring Boot
- MySQL
- RabbitMQ
- Maven
- Docker Compose
- JUnit 5 / Mockito
- OpenAPI / Swagger
- AsyncAPI

## Running the Application

### Prerequisites

- Java 21+
- Maven
- Docker
- Docker Compose

### Environment variables

Create a `.env` file in the project root using `.env.example` as reference.

```bash
cp .env.example .env
```

### Build and run

Generate the application JAR:

```bash
mvn clean package
```

Start the application and its dependencies:

```bash
docker compose up --build
```

The following services will be available:

| Service | URL / Port |
|---|---|
| Payment Service | `http://localhost:8080` |
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
| MySQL | `localhost:3307` |
| RabbitMQ | `localhost:5672` |
| RabbitMQ Management | `http://localhost:15672` |

To stop the environment:

```bash
docker compose down
```

## API

The service exposes three main operations:

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/payments` | Create a payment |
| `GET` | `/api/v1/payments/{paymentId}/status` | Retrieve payment status |
| `PATCH` | `/api/v1/payments/{paymentId}/status` | Update payment status |

The complete REST contract, including request/response schemas and error responses, is available at:

```text
docs/contracts/payment-api-openapi.yaml
```

## Messaging

When a payment status changes, the service publishes an event through RabbitMQ using:

```text
Exchange:    payment.events
Routing key: payment.status.changed
Queue:       payment.status.changed.queue
```

The complete event contract is documented at:

```text
docs/contracts/payment-events-asyncapi.yaml
```

## Tests

Run the automated tests with:

```bash
mvn test
```

Or run the complete Maven build:

```bash
mvn clean install
```

Unit tests cover the service and controller layers using JUnit 5 and Mockito.

## Postman

A Postman collection with the API requests at:

```text
postman/PAYMENT-API.postman_collection.json
```

## Additional Documentation

- REST contract: `docs/contracts/payment-api-openapi.yaml`
- Event contract: `docs/contracts/payment-events-asyncapi.yaml`
- Database schema: `db/schema.sql`
- AI usage: `AI_USAGE.md`