package com.example.MyShop_API.service.cart;

import com.example.MyShop_API.dto.request.CartRequest;
import com.example.MyShop_API.dto.response.CartResponse;
import com.example.MyShop_API.entity.Cart;

import java.util.List;

public interface ICartService {
    List<CartResponse> getCarts();

    CartResponse getCartById(Long cartId);

    Cart getCartByUserProfileId(Long userProfileId);

    CartResponse addCartForUserProfile(CartRequest cartRequest, Long userProfileId);

    CartResponse addProductToCart(Long cardId, Long productId, Integer quantity);

    CartResponse updateCart(Long cartId, CartRequest cartRequest);


    void clearCart(Long cartId);
}
