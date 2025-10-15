package com.example.MyShop_API.controller;

import com.example.MyShop_API.dto.response.ApiResponse;
import com.example.MyShop_API.dto.response.VnpayResponse;
import com.example.MyShop_API.service.payment.IPaymentService;
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

    /**
     * create URL VNPay
     *
     * @param request
     * @return
     */
    @GetMapping("/vn-pay")
    public ResponseEntity<ApiResponse<String>> pay(HttpServletRequest request,
                                                   @RequestParam Long orderId
    ) {
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Success", paymentService.createVnPayPayment(request, orderId)));
    }

    /**
     * payment processing
     *
     * @param request
     * @return
     */
    @GetMapping("/vn-pay-callback")
    public ResponseEntity<ApiResponse<VnpayResponse>> payCallbackHandler(HttpServletRequest request) {
        VnpayResponse response = paymentService.handleVnPayCallback(request);
        return ResponseEntity.ok(ApiResponse.<VnpayResponse>builder()
                .code(response.getCode().equals("00") ? 200 : 400)
                .message(response.getMessage())
                .data(response)
                .build());

    }

}

