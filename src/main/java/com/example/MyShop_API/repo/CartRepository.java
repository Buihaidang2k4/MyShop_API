package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("SELECT c FROM Cart c  WHERE c.userProfile.account_id =:userProfileId")
    Cart findByUserProfileId(@Param("userProfileId") Long userProfileId);

    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.cartItems WHERE c.cartId = :id")
    Optional<Cart> findByIdWithItems(@Param("id") Long id);

}
