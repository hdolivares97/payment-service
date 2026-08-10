package com.payment.controller;

import com.payment.dto.*;
import com.payment.service.PaymentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@Validated
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Creates a new payment.
     *
     * @param request payment creation data
     * @return created payment information
     */
    @PostMapping
    public ResponseEntity<CreatePaymentResponse> createPayment(
            @Valid @RequestBody CreatePaymentRequest request) {

        CreatePaymentResponse response =
                paymentService.createPayment(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Retrieves the current status of a payment.
     *
     * @param paymentId payment identifier
     * @return current payment status
     */
    @GetMapping("/{paymentId}/status")
    public PaymentStatusResponse getPaymentStatus(
            @PathVariable @Min(1) Long paymentId) {

        return paymentService.getPaymentStatus(paymentId);
    }

    /**
     * Updates the status of an existing payment.
     *
     * @param paymentId payment identifier
     * @param request new payment status
     */
    @PatchMapping("/{paymentId}/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePaymentStatus(
            @PathVariable @Min(1) Long paymentId,
            @Valid @RequestBody UpdatePaymentStatusRequest request) {

        paymentService.updatePaymentStatus(paymentId, request);
    }
}