package com.payment.service;

import com.payment.dto.CreatePaymentRequest;
import com.payment.dto.CreatePaymentResponse;
import com.payment.dto.PaymentStatusResponse;
import com.payment.dto.UpdatePaymentStatusRequest;
import com.payment.entity.Payment;
import com.payment.entity.PaymentStatus;
import com.payment.exception.PaymentNotFoundException;
import com.payment.messaging.PaymentEventPublisher;
import com.payment.messaging.PaymentStatusChangedEvent;
import com.payment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentEventPublisher paymentEventPublisher;

    private PaymentServiceImpl paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentServiceImpl(
                paymentRepository,
                paymentEventPublisher
        );
    }

    @Test
    void shouldCreatePayment() {
        CreatePaymentRequest request = new CreatePaymentRequest(
                "Payment 1",
                "Peter",
                "Sam",
                new BigDecimal("1500.50"),
                PaymentStatus.PENDING
        );

        Payment savedPayment = new Payment();
        savedPayment.setId(1L);
        savedPayment.setConcept(request.concept());
        savedPayment.setPayer(request.payer());
        savedPayment.setPayee(request.payee());
        savedPayment.setAmount(request.amount());
        savedPayment.setStatus(request.status());

        when(paymentRepository.save(any(Payment.class)))
                .thenReturn(savedPayment);

        CreatePaymentResponse response =
                paymentService.createPayment(request);

        assertEquals(1L, response.paymentId());

        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void shouldGetPaymentStatus() {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setStatus(PaymentStatus.PENDING);

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(payment));

        PaymentStatusResponse response =
                paymentService.getPaymentStatus(1L);

        assertEquals(1L, response.paymentId());
        assertEquals(PaymentStatus.PENDING, response.status());

        verify(paymentRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenPaymentIsNotFound() {
        when(paymentRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                PaymentNotFoundException.class,
                () -> paymentService.getPaymentStatus(999L)
        );

        verify(paymentRepository).findById(999L);
    }

    @Test
    void shouldUpdatePaymentStatusAndPublishEvent() {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setStatus(PaymentStatus.PENDING);

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(payment));

        UpdatePaymentStatusRequest request =
                new UpdatePaymentStatusRequest(PaymentStatus.COMPLETED);

        paymentService.updatePaymentStatus(1L, request);

        assertEquals(PaymentStatus.COMPLETED, payment.getStatus());

        verify(paymentRepository).save(payment);

        ArgumentCaptor<PaymentStatusChangedEvent> eventCaptor =
                ArgumentCaptor.forClass(PaymentStatusChangedEvent.class);

        verify(paymentEventPublisher)
                .publishPaymentStatusChanged(eventCaptor.capture());

        PaymentStatusChangedEvent event = eventCaptor.getValue();

        assertEquals(1L, event.paymentId());
        assertEquals(PaymentStatus.COMPLETED, event.status());
    }

    @Test
    void shouldNotPublishEventWhenStatusDoesNotChange() {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setStatus(PaymentStatus.COMPLETED);

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(payment));

        UpdatePaymentStatusRequest request =
                new UpdatePaymentStatusRequest(PaymentStatus.COMPLETED);

        paymentService.updatePaymentStatus(1L, request);

        verify(paymentRepository, never())
                .save(any(Payment.class));

        verify(paymentEventPublisher, never())
                .publishPaymentStatusChanged(any());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingPayment() {
        when(paymentRepository.findById(999L))
                .thenReturn(Optional.empty());

        UpdatePaymentStatusRequest request =
                new UpdatePaymentStatusRequest(PaymentStatus.COMPLETED);

        assertThrows(
                PaymentNotFoundException.class,
                () -> paymentService.updatePaymentStatus(999L, request)
        );

        verify(paymentRepository).findById(999L);
        verify(paymentRepository, never()).save(any(Payment.class));
        verify(paymentEventPublisher, never())
                .publishPaymentStatusChanged(any());
    }
}