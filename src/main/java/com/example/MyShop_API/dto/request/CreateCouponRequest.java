package com.example.MyShop_API.dto.request;

import com.example.MyShop_API.Enum.DiscountType;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateCouponRequest {

    String code; // để trống → tự sinh

    @NotNull
    DiscountType discountType;

    BigDecimal discountPercent;
    BigDecimal discountAmount;

    BigDecimal maxDiscountAmount;
    BigDecimal minOrderValue;

    LocalDateTime startDate;
    LocalDateTime expiryDate;

    Integer usageLimit;           // null = không giới hạn
    Integer usedCount = 0;
    boolean limitPerUser = true;  // mặc định bật
    Integer maxUsesPerUser = 1;   // mặc định 1 lần/user

    @Builder.Default
    boolean enabled = true;
}
