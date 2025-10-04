package com.example.MyShop_API.controller;

import com.example.MyShop_API.dto.response.ApiResponse;
import com.example.MyShop_API.dto.request.PaymentRequest;
import com.example.MyShop_API.dto.response.PaymentResponse;
import com.example.MyShop_API.service.payment.IPaymentService;
import com.example.MyShop_API.service.payment.PaymentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/payments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {
    IPaymentService paymentService;

    @GetMapping("/all")
    ApiResponse<List<PaymentResponse>> getPayment() {
        return ApiResponse.<List<PaymentResponse>>builder()
                .code(200)
                .message("Success")
                .data(paymentService.getPayment())
                .build();
    }

    @GetMapping("/{paymentId}")
    ApiResponse<PaymentResponse> getPayment(@PathVariable Long paymentId) {
        return ApiResponse.<PaymentResponse>builder()
                .code(200)
                .message("Success")
                .data(paymentService.getPaymentById(paymentId))
                .build();
    }

    @PostMapping("/add")
    ApiResponse<PaymentResponse> addPayment(@RequestBody PaymentRequest paymentRequest) {
        return ApiResponse.<PaymentResponse>builder()
                .code(200)
                .message("Success")
                .data(paymentService.createPayment(paymentRequest))
                .build();
    }

    @PutMapping("/{paymentId}/update")
    ApiResponse<PaymentResponse> updatePayment(@RequestBody PaymentRequest paymentRequest, @PathVariable Long paymentId) {
        return ApiResponse.<PaymentResponse>builder()
                .code(200)
                .message("Success")
                .data(paymentService.updatePayment(paymentId, paymentRequest))
                .build();
    }

    @DeleteMapping("/{paymentId}/delete")
    ApiResponse<Void> deletePayment(@PathVariable Long paymentId) {
        paymentService.deletePayment(paymentId);

        return ApiResponse.<Void>builder()
                .code(200)
                .message("Success")
                .build();
    }


}

