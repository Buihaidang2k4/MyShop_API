package com.example.MyShop_API.entity;

import com.example.MyShop_API.Enum.OrderStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long orderId;
    LocalDate orderDate;
    BigDecimal shippingFee = BigDecimal.ZERO;
    BigDecimal discountAmount = BigDecimal.ZERO;
    BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    OrderStatus orderStatus;

    @JsonIgnore
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<OrderItem> orderItems = new HashSet<>();

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "profile_id")
    UserProfile profile;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "payment_id")
    Payment payment;

    @ManyToOne
    @JoinColumn(name = "coupon_id", unique = false)
    Coupon coupon;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    List<OrderStatusHistory> statusHistory = new ArrayList<>();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    OrderDeliveryAddress deliveryAddress;
}
