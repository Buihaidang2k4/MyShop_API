package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "inventory", indexes = @Index(columnList = "product_id"))
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inventoryId;

    @OneToOne
    @JoinColumn(name = "product_id", unique = true, nullable = false)
    private Product product;
    private int available = 0;
    private int reserved = 0; // số lượng đặt trước chưa xuất kho (giỏ hàng, đơn hàng)
    private LocalDateTime updatedAt;

    // kiểm tra xem có thể đặt thêm không
    public boolean canReserve(int quantity) {
        return available >= quantity;
    }
}
