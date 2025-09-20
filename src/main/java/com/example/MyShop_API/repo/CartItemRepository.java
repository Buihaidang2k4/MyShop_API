package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.CartItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends CrudRepository<CartItem, Long> {
    @Query("SELECT c FROM CartItem c " +
            "WHERE c.product.productId =:productId AND c.cart.cartId =:cartId ")
    CartItem findCartItemByProductIdAndCartId(@Param("productId") Long productId, @Param("cartId") Long cartId);
}
