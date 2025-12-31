package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    long deleteCartItemByProductProductId(Long productProductId);

    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.cartItemId = :id")
    void deleteByIdDirect(@Param("id") Long id);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.cartId = :cartId")
    int hardDeleteByCartId(@Param("cartId") Long cartId);


    @Query("""
            SELECT ci FROM CartItem  ci
                        WHERE ci.cartItemId IN :ids AND ci.cart.profile.profileId =:profileId
            """)
    List<CartItem> findALlByIdsAndProfile(
            @Param("ids") List<Long> ids,
            @Param("profileId") Long profileId
    );
}
