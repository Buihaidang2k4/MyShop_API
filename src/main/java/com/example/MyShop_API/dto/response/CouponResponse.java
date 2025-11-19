package com.example.MyShop_API.dto.response;

import com.example.MyShop_API.Enum.DiscountType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CouponResponse {
    Long couponId;
    String code;
    DiscountType discountType;
    BigDecimal discountPercent; // 10.00 = 10%
    BigDecimal discountAmount; // 5000VNĐ
    BigDecimal maxDiscountAmount; // maximum discount limit
    BigDecimal minOrderValue; // Minimum conditions for discount
    LocalDateTime startDate;
    LocalDateTime expiryDate;
    boolean enabled = true;
}
