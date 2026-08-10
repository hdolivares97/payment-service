package com.payment.messaging;

import com.payment.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public PaymentEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Publishes a payment status changed event.
     *
     * @param event payment status changed event
     */
    public void publishPaymentStatusChanged(PaymentStatusChangedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PAYMENT_EXCHANGE,
                RabbitMQConfig.PAYMENT_STATUS_CHANGED_ROUTING_KEY,
                event
        );
    }
}