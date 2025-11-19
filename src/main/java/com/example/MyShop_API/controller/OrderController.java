package com.example.MyShop_API.controller;


import com.example.MyShop_API.Enum.OrderStatus;
import com.example.MyShop_API.dto.response.ApiResponse;
import com.example.MyShop_API.dto.request.OrderRequest;
import com.example.MyShop_API.dto.response.OrderResponse;
import com.example.MyShop_API.entity.Order;
import com.example.MyShop_API.entity.User;
import com.example.MyShop_API.mapper.OrderMapper;
import com.example.MyShop_API.service.order.IOrderService;
import com.example.MyShop_API.service.order.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {
    IOrderService orderService;
    OrderMapper orderMapper;

    @GetMapping("/all")
    ResponseEntity<ApiResponse<List<OrderResponse>>> getOrders() {
        List<OrderResponse> responses = orderMapper.toResponse(orderService.getOrders());
        return ResponseEntity.ok(new ApiResponse<>(200, "Get orders", responses));
    }

    @GetMapping("/order/{orderId}")
    ResponseEntity<ApiResponse<OrderResponse>> getOrder(@PathVariable Long orderId) {
        OrderResponse orderResponse = orderMapper.toResponse(orderService.getOrder(orderId));
        return ResponseEntity.ok(new ApiResponse<>(200, "Get order", orderResponse));
    }

    @GetMapping("/status")
    ResponseEntity<ApiResponse<List<OrderResponse>>> getOrderByStatus(@RequestParam OrderStatus orderStatus) {
        List<OrderResponse> responses = orderMapper.toResponse(orderService.getOrdersByStatus(orderStatus));
        return ResponseEntity.ok(
                new ApiResponse<>(200, "Get orders by status", responses)
        );
    }

    @GetMapping("/profile/{profileId}")
    ResponseEntity<ApiResponse<List<OrderResponse>>> getOrderByUser(@PathVariable Long profileId) {
        List<OrderResponse> responses = orderMapper.toResponse(orderService.getUserOrders(profileId));
        return ResponseEntity.ok(new ApiResponse<>(200, "Get user order", responses));
    }

    // ============= BUY NOW ==============
    @PostMapping("/buy-now")
    @Operation(summary = "Buy now ")
    ResponseEntity<ApiResponse<Object>> buyNow(
            @RequestBody OrderRequest orderRequest,
            HttpServletRequest request) {
        Object result = orderService.buyNow(orderRequest, request);
        return buildOrderResponse(result);
    }


    // ============= PLACE ORDER FROM CART  ===============
    @PostMapping("/placeOrder")
    @Operation(summary = "Place order from cart")
    ResponseEntity<ApiResponse<Object>> placeOrder(
            @RequestBody OrderRequest orderRequest,
            HttpServletRequest request
    ) {
        Object result = orderService.placeOrder(orderRequest, request);

        return buildOrderResponse(result);
    }

    // ============== CONFIRM PAYMENT CASH =================
    @PutMapping("order/{orderId}/confirm-cash-payment")
    ResponseEntity<ApiResponse<Void>> confirmCashPayment(@PathVariable Long orderId,
                                                         @AuthenticationPrincipal User admin
    ) {
        orderService.confirmCashOrder(orderId, admin);
        return ResponseEntity.ok(new ApiResponse<>(200, "Cash payment confirm ", null));
    }

    @PutMapping("/order/{orderId}/cancelOrder")
    ResponseEntity<ApiResponse> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok(new ApiResponse(200, "Cancel Order", null));
    }

    @PutMapping("/order/updateStatus")
    ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(@RequestParam("orderStatus") OrderStatus orderStatus
            , @RequestParam("orderId") Long orderId
            , @AuthenticationPrincipal User admin

    ) {
        OrderResponse orderResponse = orderMapper.toResponse(orderService.updateOrderStatus(orderId, orderStatus, admin));
        return ResponseEntity.ok(
                new ApiResponse(200, "Update order status success", orderResponse)
        );
    }

    @DeleteMapping("/order/{orderId}/delete")
    ResponseEntity<ApiResponse> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok(new ApiResponse(200, "Delete Order Success", null));
    }

    private ResponseEntity<ApiResponse<Object>> buildOrderResponse(Object result) {
        // CASH
        if (result instanceof Order savedOrder) {
            OrderResponse response = orderMapper.toResponse(savedOrder);
            return ResponseEntity.ok(new ApiResponse<>(200, "Place order success", response));
        }

        // VNPAY
        if (result instanceof String paymentUrl) {
            return ResponseEntity.ok(new ApiResponse<>(200, "Redirect to Vnpay", paymentUrl));
        }

        // fallback
        return ResponseEntity.ok(
                new ApiResponse<>(500, "Unexpected return type", null)
        );
    }

}
