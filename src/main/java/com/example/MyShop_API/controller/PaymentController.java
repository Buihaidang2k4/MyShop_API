package com.example.MyShop_API.controller;

import com.example.MyShop_API.dto.response.ApiResponse;
import com.example.MyShop_API.dto.response.PaymentDto;
import com.example.MyShop_API.dto.response.VnpayResponse;
import com.example.MyShop_API.entity.Order;
import com.example.MyShop_API.service.order.IOrderService;
import com.example.MyShop_API.service.payment.IPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("${api.prefix}/payment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {
    IPaymentService paymentService;
    IOrderService orderService;

    @NonFinal
    @Value("${app.frontend.baseUrl}")
    String baseUrlFe;

    @GetMapping
    ResponseEntity<ApiResponse<List<PaymentDto>>> getPayments() {
        return ResponseEntity.ok(new ApiResponse<>(200, "success", paymentService.getPayments()));
    }

    @GetMapping("/{paymentId}")
    ResponseEntity<ApiResponse<PaymentDto>> getPayment(@PathVariable Long paymentId) {
        return ResponseEntity.ok(new ApiResponse<>(200, "success", paymentService.getPayment(paymentId)));
    }

    @GetMapping("/order/{orderId}")
    ResponseEntity<ApiResponse<PaymentDto>> getPaymentByOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(new ApiResponse<>(200, "success", paymentService.getPaymentByOrder(orderId)));
    }

    // ================ Create Url payment =================
    @GetMapping("/vn-pay")
    @Operation(summary = "test")
    public ResponseEntity<ApiResponse<String>> pay(HttpServletRequest request,
                                                   @RequestParam Long orderId,
                                                   @RequestParam String bankCode
    ) {
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Success", paymentService.createVnPayPayment(request, orderId, bankCode)));
    }

    @GetMapping("/vn-pay-callback")
    public void payVnpayCallback(HttpServletRequest request, HttpServletResponse response) throws IOException {
        VnpayResponse res = orderService.finalizeVnPayCallback(request);

        String orderId = request.getParameter("vnp_TxnRef");
        String code = res.getCode();

        String redirectUrl = baseUrlFe + "/order-details/" + orderId + "?paymentStatus=" + code;

        response.sendRedirect(redirectUrl);
    }

    // ===================== PAYMENT CASH ======================
    @PostMapping("/cod/{orderId}")
    @Operation(summary = "test")
    ResponseEntity<ApiResponse<?>> payWithCash(@PathVariable Long orderId) {
        return ResponseEntity.ok(new ApiResponse(200, "pay success", paymentService.processCashPayment(orderId)));
    }


}

