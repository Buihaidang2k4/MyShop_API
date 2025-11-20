package com.example.MyShop_API.controller;

import com.example.MyShop_API.dto.response.ApiResponse;
import com.example.MyShop_API.dto.response.VnpayResponse;
import com.example.MyShop_API.entity.Order;
import com.example.MyShop_API.service.order.IOrderService;
import com.example.MyShop_API.service.payment.IPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("${api.prefix}/payment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {
    IPaymentService paymentService;
    IOrderService orderService;

    // ================ Create Url payment =================
    @GetMapping("/vn-pay")
    @Operation(summary = "test")
    public ResponseEntity<ApiResponse<String>> pay(HttpServletRequest request,
                                                   @RequestParam Long orderId,
                                                   @RequestParam String bankCode
    ) {
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Success", paymentService.createVnPayPayment(request, orderId, bankCode)));
    }

    // ================ Vnpay Callback ===================
//    @GetMapping("/vn-pay-callback")
//    public ResponseEntity<ApiResponse<VnpayResponse>> payCallbackHandler(HttpServletRequest request) {
//        VnpayResponse response = paymentService.handleVnPayCallback(request);
//        return ResponseEntity.ok(ApiResponse.<VnpayResponse>builder()
//                .code(response.getCode().equals("00") ? 200 : 400)
//                .message(response.getMessage())
//                .data(response)
//                .build());
//    }

    @GetMapping("/vn-pay-callback")
    public ResponseEntity<ApiResponse<VnpayResponse>> payVnpayCallback(HttpServletRequest request) {
        VnpayResponse response = orderService.finalizeVnPayCallback(request);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Callback processed",
                        response
                )
        );
    }

    // ===================== PAYMENT CASH ======================
    @PostMapping("/cod/{orderId}")
    @Operation(summary = "test")
    ResponseEntity<ApiResponse<?>> payWithCash(@PathVariable Long orderId) {
        return ResponseEntity.ok(new ApiResponse(200, "pay success", paymentService.processCashPayment(orderId)));
    }


}

