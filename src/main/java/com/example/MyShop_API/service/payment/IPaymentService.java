package com.example.MyShop_API.service.payment;

import com.example.MyShop_API.dto.request.PaymentRequest;
import com.example.MyShop_API.dto.response.PaymentResponse;
import com.example.MyShop_API.dto.response.VnpayResponse;
import com.example.MyShop_API.entity.Order;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface IPaymentService {
    boolean processCashPayment(Long orderId);


    String createVnPayPayment(HttpServletRequest request, Long orderId, String bankCode);

    VnpayResponse handleVnPayCallback(HttpServletRequest request);

}
