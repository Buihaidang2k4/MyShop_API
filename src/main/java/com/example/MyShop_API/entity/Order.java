package com.example.MyShop_API.entity;

import com.example.MyShop_API.Enum.OrderStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
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
    BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    OrderStatus orderStatus;

    @JsonManagedReference
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<OrderItem> orderItems = new HashSet<>();

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "profile_id")
    UserProfile profile;

    @OneToOne
    @JoinColumn(name = "payment_id")
    Payment payment;

    @ManyToMany
    @JoinTable(
            name = "order_coupons",
            joinColumns = @JoinColumn(name = "order_id")
            , inverseJoinColumns = @JoinColumn(name = "coupon_id")
    )
    Set<Coupon> coupons = new HashSet<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    List<OrderStatusHistory> statusHistory = new ArrayList<>();

}
