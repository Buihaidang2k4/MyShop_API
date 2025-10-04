package com.example.MyShop_API.controller;


import com.example.MyShop_API.Enum.OrderStatus;
import com.example.MyShop_API.dto.response.ApiResponse;
import com.example.MyShop_API.dto.request.OrderRequest;
import com.example.MyShop_API.dto.response.OrderResponse;
import com.example.MyShop_API.service.order.IOrderService;
import com.example.MyShop_API.service.order.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/order")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {
    IOrderService orderService;


    @GetMapping("/{orderId}")
    ApiResponse<OrderResponse> getOrder(@PathVariable Long orderId) {
        return ApiResponse.<OrderResponse>builder()
                .code(200)
                .message("Success")
                .data(orderService.getOrder(orderId))
                .build();
    }

    @PostMapping("/placeOrder/product/{productId}")
    ApiResponse<OrderResponse> placeOrder(@RequestBody OrderRequest orderRequest, @PathVariable Long productId) {
        return ApiResponse.<OrderResponse>builder()
                .code(200)
                .message("Success")
                .data(orderService.placeOrder(productId, orderRequest))
                .build();
    }

    @PutMapping("/{orderId}/status/update")
    ApiResponse<OrderResponse> updateOrder(@RequestParam OrderStatus orderStatus, @PathVariable Long orderId) {
        return ApiResponse.<OrderResponse>builder()
                .code(200)
                .message("Success")
                .data(orderService.updateOrder(orderId, orderStatus))
                .build();
    }

    @DeleteMapping("/{orderId}/delete")
    ApiResponse<Void> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Success")
                .build();
    }

}
