package com.example.MyShop_API.service.payment;

import com.example.MyShop_API.dto.request.PaymentRequest;
import com.example.MyShop_API.dto.response.PaymentResponse;

import java.util.List;

public interface IPaymentService {
    List<PaymentResponse> getPayment();

    PaymentResponse getPaymentById(Long paymentId);

    PaymentResponse createPayment(PaymentRequest paymentRequest);

    PaymentResponse updatePayment(Long paymentId, PaymentRequest paymentRequest);

    void deletePayment(Long paymentId);
}
