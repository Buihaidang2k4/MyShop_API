package com.example.MyShop_API.repo;

import com.example.MyShop_API.Enum.OrderStatus;
import com.example.MyShop_API.entity.Coupon;
import com.example.MyShop_API.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE o.profile.profileId =:profileId")
    List<Order> findByProfileProfile_id(@Param("profileId") Long profileId);

    List<Order> findByOrderStatus(OrderStatus orderStatus);

    int countByProfile_ProfileIdAndCoupon_CouponId(Long profileProfileId, Long couponCouponId);
}
