package com.example.MyShop_API.dto.request;


import com.example.MyShop_API.Enum.OrderStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;


@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderRequest {
    String email;
    Long paymentId;
    OrderStatus orderStatus;
    OrderItemRequest orderItemRequest;
}
