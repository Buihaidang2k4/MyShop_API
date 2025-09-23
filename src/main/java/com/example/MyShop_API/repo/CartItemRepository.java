package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.CartItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends CrudRepository<CartItem, Long> {
    @Query("SELECT c FROM CartItem c " +
            "WHERE c.product.productId =:productId AND c.cart.cartId =:cartId ")
    CartItem findCartItemByProductIdAndCartId(@Param("productId") Long productId, @Param("cartId") Long cartId);

    @Query("SELECT  ci FROM CartItem ci  WHERE ci.product.productId =:productId")
    List<CartItem> findCartItemByProductId(@Param("productId") Long productId);

    long deleteCartItemByProductProductId(Long productProductId);
}
