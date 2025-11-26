package com.example.MyShop_API.repo;

import com.example.MyShop_API.Enum.OrderStatus;
import com.example.MyShop_API.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE o.profile.profileId =:profileId")
    List<Order> findByProfileProfile_id(@Param("profileId") Long profileId);

    List<Order> findByOrderStatus(OrderStatus orderStatus);

    int countByProfile_ProfileIdAndCoupon_CouponId(Long profileProfileId, Long couponCouponId);


    @Query("""
            SELECT COUNT(o) > 0
            FROM Order o
            JOIN o.orderItems oi
            WHERE o.profile.profileId = :profileId
              AND oi.product.productId = :productId
              AND o.orderId = :orderId
              AND o.orderStatus IN :statuses
            """)
    boolean hasPurchasedProductInOrder(
            @Param("profileId") Long profileId,
            @Param("productId") Long productId,
            @Param("orderId") Long orderId,
            @Param("statuses") List<OrderStatus> statuses);
}
