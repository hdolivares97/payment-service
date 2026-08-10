package com.payment.messaging;

import com.payment.entity.PaymentStatus;

import java.time.Instant;

public record PaymentStatusChangedEvent(
        Long paymentId,
        PaymentStatus status
) {
}