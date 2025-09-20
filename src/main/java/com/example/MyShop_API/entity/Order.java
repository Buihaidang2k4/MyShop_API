package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "oders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long oderId;

    @Email
    @Column(nullable = false)
    String email;

    @OneToMany(mappedBy = "order", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    List<OrderItem> orderItems = new ArrayList<>();

    LocalDate orderDate;

    @OneToOne
    @JoinColumn(name = "payment_id")
    Payment payment;

    Double totalAmount;
    String orderStatus;
}
