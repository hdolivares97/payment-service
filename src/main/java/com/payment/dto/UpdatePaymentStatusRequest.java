package com.payment.dto;

import com.payment.entity.PaymentStatus;
import jakarta.validation.constraints.NotNull;

public record UpdatePaymentStatusRequest(

        @NotNull
        PaymentStatus status
) {
}
