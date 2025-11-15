package com.example.MyShop_API.entity;

import com.example.MyShop_API.Enum.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_status_history")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long historyId;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    Order order;

    @Enumerated(EnumType.STRING)
    OrderStatus orderStatus;

    LocalDateTime changedAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "changed_by")
    User changedBy; // nullable nếu hệ thống tự động
}
