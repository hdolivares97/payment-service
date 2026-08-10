package com.payment.controller;

import tools.jackson.databind.ObjectMapper;
import com.payment.dto.CreatePaymentRequest;
import com.payment.dto.CreatePaymentResponse;
import com.payment.dto.PaymentStatusResponse;
import com.payment.dto.UpdatePaymentStatusRequest;
import com.payment.entity.PaymentStatus;
import com.payment.exception.PaymentNotFoundException;
import com.payment.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    void shouldCreatePayment() throws Exception {
        CreatePaymentRequest request = new CreatePaymentRequest(
                "Payment 1",
                "Peter",
                "Sam",
                new BigDecimal("1500.50"),
                PaymentStatus.PENDING
        );

        when(paymentService.createPayment(any(CreatePaymentRequest.class)))
                .thenReturn(new CreatePaymentResponse(1L));

        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.paymentId").value(1));
    }

    @Test
    void shouldReturnBadRequestWhenCreatePaymentIsInvalid() throws Exception {
        String request = """
                {
                  "concept": "",
                  "payer": "Peter",
                  "payee": "Sam",
                  "amount": -10,
                  "status": "PENDING"
                }
                """;

        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").value("Invalid request"));
    }

    @Test
    void shouldReturnBadRequestWhenCreatePaymentStatusIsInvalid() throws Exception {
        String request = """
                {
                  "concept": "Payment 1",
                  "payer": "Peter",
                  "payee": "Sam",
                  "amount": 1500.50,
                  "status": "INVALID"
                }
                """;

        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").value("Invalid request"));
    }

    @Test
    void shouldGetPaymentStatus() throws Exception {
        when(paymentService.getPaymentStatus(1L))
                .thenReturn(
                        new PaymentStatusResponse(
                                1L,
                                PaymentStatus.PENDING
                        )
                );

        mockMvc.perform(get("/api/v1/payments/1/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void shouldReturnNotFoundWhenPaymentDoesNotExist() throws Exception {
        when(paymentService.getPaymentStatus(999L))
                .thenThrow(new PaymentNotFoundException(999L));

        mockMvc.perform(get("/api/v1/payments/999/status"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PAYMENT_NOT_FOUND"))
                .andExpect(jsonPath("$.message")
                        .value("Payment with id 999 was not found"));
    }

    @Test
    void shouldReturnBadRequestWhenPaymentIdIsZero() throws Exception {
        mockMvc.perform(get("/api/v1/payments/0/status"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message")
                        .value("Invalid payment identifier"));
    }

    @Test
    void shouldReturnBadRequestWhenPaymentIdIsNegative() throws Exception {
        mockMvc.perform(get("/api/v1/payments/-1/status"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message")
                        .value("Invalid payment identifier"));
    }

    @Test
    void shouldUpdatePaymentStatus() throws Exception {
        UpdatePaymentStatusRequest request =
                new UpdatePaymentStatusRequest(PaymentStatus.COMPLETED);

        doNothing()
                .when(paymentService)
                .updatePaymentStatus(
                        eq(1L),
                        any(UpdatePaymentStatusRequest.class)
                );

        mockMvc.perform(patch("/api/v1/payments/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistingPayment() throws Exception {
        UpdatePaymentStatusRequest request =
                new UpdatePaymentStatusRequest(PaymentStatus.COMPLETED);

        org.mockito.Mockito.doThrow(
                        new PaymentNotFoundException(999L)
                )
                .when(paymentService)
                .updatePaymentStatus(
                        eq(999L),
                        any(UpdatePaymentStatusRequest.class)
                );

        mockMvc.perform(patch("/api/v1/payments/999/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PAYMENT_NOT_FOUND"))
                .andExpect(jsonPath("$.message")
                        .value("Payment with id 999 was not found"));
    }

    @Test
    void shouldReturnBadRequestWhenUpdateStatusIsInvalid() throws Exception {
        String request = """
                {
                  "status": "INVALID"
                }
                """;

        mockMvc.perform(patch("/api/v1/payments/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").value("Invalid request"));
    }

    @Test
    void shouldReturnBadRequestWhenUpdateStatusIsNull() throws Exception {
        String request = """
                {
                  "status": null
                }
                """;

        mockMvc.perform(patch("/api/v1/payments/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").value("Invalid request"));
    }

    @Test
    void shouldReturnBadRequestWhenUpdatePaymentIdIsInvalid() throws Exception {
        UpdatePaymentStatusRequest request =
                new UpdatePaymentStatusRequest(PaymentStatus.COMPLETED);

        mockMvc.perform(patch("/api/v1/payments/0/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message")
                        .value("Invalid payment identifier"));
    }
}