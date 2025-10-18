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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {
    IOrderService orderService;

    @GetMapping("/all")
    ResponseEntity<ApiResponse> getOrders() {
        return ResponseEntity.ok(new ApiResponse<>(200, "Get orders", orderService.getOrders()));
    }

    @GetMapping("/order/{orderId}")
    ResponseEntity<ApiResponse> getOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(new ApiResponse<>(200, "Get order", orderService.getOrder(orderId)));
    }

    @GetMapping("/OrderStatus")
    ResponseEntity<ApiResponse> getOrderByStatus(@RequestParam OrderStatus orderStatus) {
        return ResponseEntity.ok(new ApiResponse<>(200, "Get orders by status", orderService.getOrdersByStatus(orderStatus)));
    }

    @GetMapping("/userprofile/{profileId}")
    ResponseEntity<ApiResponse> getOrderByUser(@PathVariable Long profileId) {
        return ResponseEntity.ok(new ApiResponse<>(200, "Get orders by user", orderService.getUserOrders(profileId)));
    }

    @PostMapping("/userprofile/{profileId}/placeOrder")
    ResponseEntity<ApiResponse> placeOrder(@PathVariable Long profileId) {
        return ResponseEntity.ok(new ApiResponse(200, "place order", orderService.placeOrder(profileId)));
    }

    @PutMapping("/order/{orderId}/cancelOrder")
    ResponseEntity<ApiResponse> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok(new ApiResponse(200, "Cancel Order", null));
    }

    @PutMapping("/order/{orderId}/updateStatus")
    ResponseEntity<ApiResponse> updateOrderStatus(@RequestParam OrderStatus orderStatus, @PathVariable Long orderId) {
        return ResponseEntity.ok(new ApiResponse(200, "update status", orderService.updateOrderStatus(orderId, orderStatus)));
    }

    @DeleteMapping("/order/{orderId}/delete")
    ResponseEntity<ApiResponse> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok(new ApiResponse(200, "Delete Order Success", null));
    }

}
