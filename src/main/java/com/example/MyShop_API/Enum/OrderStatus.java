package com.example.MyShop_API.Enum;

public enum OrderStatus {
    PENDING,    // Đơn hàng mới tạo
    SHIPPED,    // Đã giao cho đơn vị vận chuyển
    DELIVERED,  // Đã giao thành công
    CANCELLED   // Bị hủy
}
