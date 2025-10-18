package com.example.MyShop_API.controller;


import com.example.MyShop_API.dto.response.ApiResponse;
import com.example.MyShop_API.service.cart.ICartItemService;
import com.example.MyShop_API.service.cart.ICartService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("${api.prefix}/cartItems")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartItemController {
    ICartItemService cartItemService;
    ICartService cartService;

    @GetMapping("/cart/{cartId}/cartItem/{cartItemId}")
    ResponseEntity<ApiResponse> getCartItem(@PathVariable Long cartId,
                                            @PathVariable Long cartItemId
    ) {
        return ResponseEntity.ok(new ApiResponse<>(200, "get all cart", cartItemService.getCartItem(cartId, cartItemId)));
    }

    @PostMapping("/cartItem/addItemToCart")
    ResponseEntity<ApiResponse> addItemToCart(@RequestParam(required = false) Optional<Long> cartId,
                                              @RequestParam Long productId,
                                              @RequestParam Integer quantity) {
        try {
            Long checkCartId = cartId
                    .map(id -> cartService.getCartById(id) != null ? id : cartService.initializeNewCart())
                    .orElseGet(() -> cartService.initializeNewCart());

            cartItemService.addItemToCart(checkCartId, productId, quantity);
            return ResponseEntity.ok(new ApiResponse(200, "add cartItem success", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(500, "add cartItem failed " + e.getMessage(), null));
        }
    }

    @PutMapping("/cart/{cartId}/cartItem/{cartItemId}/updateItemQuantity")
    ResponseEntity<ApiResponse> updateItemQuantity(@PathVariable Long cartId,
                                                   @PathVariable Long cartItemId,
                                                   @RequestParam Integer quantity
    ) {
        try {
            cartItemService.updateItemQuantity(cartId, cartItemId, quantity);
            return ResponseEntity.ok(new ApiResponse(200, "update cartItem success", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(500, "update cartItem failed " + e.getMessage(), null));
        }
    }

    @DeleteMapping("/cart/{cartId}/cartItem/{cartItemId}/removeItemFromCart")
    ResponseEntity<ApiResponse> removeItemFromCart(@PathVariable Long cartId,
                                                   @PathVariable Long cartItemId) {
        cartItemService.removeItemFromCart(cartId, cartItemId);
        return ResponseEntity.ok(new ApiResponse(200, "remove cartItem success", null));
    }

}
