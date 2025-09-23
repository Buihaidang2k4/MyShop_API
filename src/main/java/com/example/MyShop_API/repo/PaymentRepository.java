package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsByPaymentMethod(String paymentMethod);
}
