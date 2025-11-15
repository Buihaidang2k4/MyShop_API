package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "coupons", uniqueConstraints = @UniqueConstraint(columnNames = "code"))
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long couponId;

    @Column(unique = true, nullable = false)
    String code;

    BigDecimal discountPercent; // 10.00 = 10%
    BigDecimal maxDiscountAmount;
    BigDecimal minOrderValue;

    LocalDateTime expiryDate;
    boolean enabled = true;

    @ManyToMany(mappedBy = "coupons")
    Set<Order> orders = new HashSet<>();

    @PrePersist
    public void generateCode() {
        if (this.code == null || this.code.isEmpty()) {
            String prefix = "COUPON_";
            this.code = prefix + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12);
        }
    }
}
