package com.example.MyShop_API.dto.request;

import java.math.BigDecimal;

public record ProductRevenueDTO(
        Long productId,
        String productName,
        BigDecimal revenueProduct,
        int totalSold,
        int totalOrders,
        BigDecimal averagePrice // gia ban trung binh
) {
}
