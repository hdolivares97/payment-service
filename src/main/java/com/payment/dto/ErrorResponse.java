package com.payment.dto;

public record ErrorResponse(
        String code,
        String message
) {
}