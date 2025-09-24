package com.example.MyShop_API.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItemResponse {
    Long oderItemId;
    Long orderId;
    Long productId;
    Integer quantity;
    double discount;
    double orderedProductPrice;
}
