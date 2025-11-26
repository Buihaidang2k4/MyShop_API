package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.OrderDeliveryAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDeliveryAddressRepository extends JpaRepository<OrderDeliveryAddress, Long> {
}
