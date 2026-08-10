package com.payment.service;

import com.payment.dto.CreatePaymentRequest;
import com.payment.dto.CreatePaymentResponse;
import com.payment.dto.PaymentStatusResponse;
import com.payment.dto.UpdatePaymentStatusRequest;

public interface PaymentService {

    /**
     * Creates a payment using the provided information.
     *
     * @param request payment information
     * @return identifier assigned to the created payment
     */
    CreatePaymentResponse createPayment(CreatePaymentRequest request);

    /**
     * Retrieves the current status of a payment.
     *
     * @param paymentId payment identifier
     * @return current payment status
     */
    PaymentStatusResponse getPaymentStatus(Long paymentId);

    /**
     * Updates the status of an existing payment.
     *
     * @param paymentId payment identifier
     * @param request new payment status
     */
    void updatePaymentStatus(Long paymentId, UpdatePaymentStatusRequest request);
}