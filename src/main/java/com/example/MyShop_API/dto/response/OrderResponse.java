package com.example.MyShop_API.dto;


import com.example.MyShop_API.entity.OrderItem;
import com.example.MyShop_API.entity.Payment;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {
    Long orderId;
    Long paymentId;
    String email;
    LocalDate orderDate;
    Double totalAmount;
    String orderStatus;
    List<OrderItemResponse> orderItemResponses;
}
