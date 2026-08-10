package com.payment.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String PAYMENT_EXCHANGE = "payment.events";
    public static final String PAYMENT_STATUS_CHANGED_QUEUE =
            "payment.status.changed.queue";
    public static final String PAYMENT_STATUS_CHANGED_ROUTING_KEY =
            "payment.status.changed";

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public TopicExchange paymentExchange() {
        return new TopicExchange(PAYMENT_EXCHANGE);
    }

    @Bean
    public Queue paymentStatusChangedQueue() {
        return new Queue(PAYMENT_STATUS_CHANGED_QUEUE, true);
    }

    @Bean
    public Binding paymentStatusChangedBinding(
            Queue paymentStatusChangedQueue,
            TopicExchange paymentExchange) {

        return BindingBuilder
                .bind(paymentStatusChangedQueue)
                .to(paymentExchange)
                .with(PAYMENT_STATUS_CHANGED_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}