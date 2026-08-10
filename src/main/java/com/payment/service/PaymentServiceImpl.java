package com.payment.service;

import com.payment.dto.CreatePaymentRequest;
import com.payment.dto.CreatePaymentResponse;
import com.payment.dto.PaymentStatusResponse;
import com.payment.dto.UpdatePaymentStatusRequest;
import com.payment.entity.Payment;
import com.payment.exception.PaymentNotFoundException;
import com.payment.messaging.PaymentEventPublisher;
import com.payment.messaging.PaymentStatusChangedEvent;
import com.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentEventPublisher paymentEventPublisher;

    public PaymentServiceImpl(
            PaymentRepository paymentRepository,
            PaymentEventPublisher paymentEventPublisher) {

        this.paymentRepository = paymentRepository;
        this.paymentEventPublisher = paymentEventPublisher;
    }

    @Override
    @Transactional
    public CreatePaymentResponse createPayment(CreatePaymentRequest request) {
        Payment payment = new Payment();

        payment.setConcept(request.concept());
        payment.setPayer(request.payer());
        payment.setPayee(request.payee());
        payment.setAmount(request.amount());
        payment.setStatus(request.status());

        Payment savedPayment = paymentRepository.save(payment);

        return new CreatePaymentResponse(savedPayment.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentStatusResponse getPaymentStatus(Long paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new PaymentNotFoundException(paymentId)
                );

        return new PaymentStatusResponse(
                payment.getId(),
                payment.getStatus()
        );
    }

    @Override
    @Transactional
    public void updatePaymentStatus(
            Long paymentId,
            UpdatePaymentStatusRequest request) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new PaymentNotFoundException(paymentId)
                );

        if (payment.getStatus() == request.status()) {
            return;
        }

        payment.setStatus(request.status());

        paymentRepository.save(payment);

        PaymentStatusChangedEvent event =
                new PaymentStatusChangedEvent(
                        payment.getId(),
                        payment.getStatus()
                );

        paymentEventPublisher.publishPaymentStatusChanged(event);
    }
}