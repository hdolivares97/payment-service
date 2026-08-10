package com.payment.dto;

import com.payment.entity.PaymentStatus;

public record PaymentStatusResponse(
        Long paymentId,
        PaymentStatus status
) {
}