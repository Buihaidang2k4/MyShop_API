package com.example.MyShop_API.controller;

import com.example.MyShop_API.dto.response.ApiResponse;
import com.example.MyShop_API.dto.request.PaymentRequest;
import com.example.MyShop_API.dto.response.PaymentResponse;
import com.example.MyShop_API.service.PaymentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {
    PaymentService paymentService;

    @GetMapping
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

    @PostMapping
    ApiResponse<PaymentResponse> createPayment(@RequestBody PaymentRequest paymentRequest) {
        return ApiResponse.<PaymentResponse>builder()
                .code(200)
                .message("Success")
                .data(paymentService.createPayment(paymentRequest))
                .build();
    }

    @PutMapping("")
    ApiResponse<PaymentResponse> updatePayment(@RequestBody PaymentRequest paymentRequest, @RequestParam Long paymentId) {
        return ApiResponse.<PaymentResponse>builder()
                .code(200)
                .message("Success")
                .data(paymentService.updatePayment(paymentId, paymentRequest))
                .build();
    }

    @DeleteMapping("")
    ApiResponse<Void> deletePayment(@RequestParam Long paymentId) {
        paymentService.deletePayment(paymentId);

        return ApiResponse.<Void>builder()
                .code(200)
                .message("Success")
                .build();
    }


}

