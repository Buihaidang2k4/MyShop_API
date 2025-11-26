package com.example.MyShop_API.dto.request;


import com.example.MyShop_API.Enum.PaymentMethod;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
    Long profileId;
    Long productId;
    Long addressId;

    @Min(value = 1, message = "Quantity must be at least 1")
    int quantity; // Use Buy Now

    @NotNull(message = "Payment method is required")
    PaymentMethod paymentMethod; // VNPAY : CASH
    String bankCode = "NCB"; // VNPAY
    String couponCode;
    String orderNote; // có thể null
}
