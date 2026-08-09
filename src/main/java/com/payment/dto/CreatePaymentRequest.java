package com.payment.dto;

import com.payment.entity.PaymentStatus;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreatePaymentRequest(

        @NotBlank
        @Size(max = 255)
        String concept,

        @NotBlank
        @Size(max = 255)
        String payer,

        @NotBlank
        @Size(max = 255)
        String payee,

        @NotNull
        @Positive
        BigDecimal amount,

        @NotNull
        PaymentStatus status

) {
}