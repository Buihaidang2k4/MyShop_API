package com.example.MyShop_API.service.cart;

import com.example.MyShop_API.entity.CartItem;

public interface ICartItemService {
    void addItemToCart(Long cartId, Long productId, int quantity);

    void removeItemFromCart(Long cartId, Long cartItemId);

    void updateItemQuantity(Long cartId, Long cartItemId, int quantity);

    CartItem getCartItem(Long cartId, Long cartItemId);
}
