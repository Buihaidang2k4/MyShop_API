package com.example.MyShop_API.service.payment;

import com.example.MyShop_API.dto.request.PaymentRequest;
import com.example.MyShop_API.dto.response.PaymentResponse;
import com.example.MyShop_API.dto.response.VnpayResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface IPaymentService {
    String createVnPayPayment(HttpServletRequest request, long orderId);

    VnpayResponse handleVnPayCallback(HttpServletRequest request);

}
