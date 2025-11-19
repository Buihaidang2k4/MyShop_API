package com.example.MyShop_API.dto.response;


import com.example.MyShop_API.entity.Coupon;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {
    Long orderId;
    Long paymentId;
    Long profileId;
    LocalDate orderDate;
    BigDecimal totalAmount;
    String orderStatus;
    List<OrderItemResponse> orderItemResponses;
    CouponResponse couponResponse;
}
