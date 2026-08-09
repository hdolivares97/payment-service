package com.payment.service;

import com.payment.dto.CreatePaymentRequest;
import com.payment.dto.CreatePaymentResponse;

public interface PaymentService {

    /**
     * Creates a payment using the provided information.
     *
     * @param request payment information
     * @return identifier assigned to the created payment
     */
    CreatePaymentResponse createPayment(CreatePaymentRequest request);
}