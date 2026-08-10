# AI Usage

AI was used as a support tool during the implementation of this technical exercise.

Its use was focused on topics where I wanted to validate concepts and implementation decisions related to RabbitMQ, since my previous experience with event-driven architectures has been mainly with Kafka and Azure Event Hubs.

The generated suggestions were reviewed and adapted before being incorporated into the solution.

The prompts below summarize the main AI-assisted interactions related to RabbitMQ.

## Prompts

### 1. RabbitMQ Messaging Model

**Prompt:**

> I have experience working with Kafka and Azure Event Hubs, but not directly with RabbitMQ.
> Explain how exchange, queue, binding, and routing key work in RabbitMQ and how they relate to concepts I may already know from event-driven architectures.
> For a payment service that needs to publish an event when the payment status changes, suggest a simple topology and explain the reasoning behind it.

**How it was used:**

Used to understand RabbitMQ-specific concepts and compare them with messaging technologies I had previously worked with.

Based on this understanding, the following topology was selected:

- Exchange: `payment.events`
- Routing key: `payment.status.changed`
- Queue: `payment.status.changed.queue`

---

### 2. Spring AMQP Integration

**Prompt:**

> In a Spring Boot payment service, I need to publish an event to RabbitMQ when the status of a payment changes.
> Show me how Spring AMQP can be used to declare the RabbitMQ topology and publish the event.
> Keep the implementation simple and explain the responsibility of the configuration and publisher components.

**How it was used:**

Used as a reference to learn how RabbitMQ integrates with Spring Boot through Spring AMQP, particularly the declaration of the messaging topology and message publishing.

The suggested approach was reviewed and adapted to the payment status update use case.

---

## RabbitMQ Learning and Implementation Flow

AI assistance was limited to the RabbitMQ-related portion of the exercise.

The workflow for this part was:

1. Review RabbitMQ concepts that were new to me.
2. Compare them with my previous experience using Kafka and Azure Event Hubs.
3. Review how Spring AMQP provides RabbitMQ integration in Spring Boot.
4. Implement and validate the selected approach locally using RabbitMQ Management.