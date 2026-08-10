package com.payment.service;

import com.payment.dto.CreatePaymentRequest;
import com.payment.dto.CreatePaymentResponse;
import com.payment.dto.PaymentStatusResponse;
import com.payment.entity.Payment;
import com.payment.exception.PaymentNotFoundException;
import com.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
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
}