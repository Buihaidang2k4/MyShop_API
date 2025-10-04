package com.example.MyShop_API.controller;

import com.example.MyShop_API.dto.response.ApiResponse;
import com.example.MyShop_API.dto.request.CartRequest;
import com.example.MyShop_API.dto.response.CartResponse;
import com.example.MyShop_API.service.cart.CartService;
import com.example.MyShop_API.service.cart.ICartService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/carts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController {
    ICartService cartService;

    @GetMapping("/all")
    ApiResponse<List<CartResponse>> getCarts() {
        return ApiResponse.<List<CartResponse>>builder()
                .data(cartService.getCarts())
                .build();
    }

    @GetMapping("/{cardId}")
    ApiResponse<CartResponse> getCart(Long cardId) {
        return ApiResponse.<CartResponse>builder()
                .code(200)
                .message("Success")
                .data(cartService.getCartById(cardId))
                .build();
    }

    @PostMapping("/userProfile/{userProfileId}/add")
    ApiResponse<CartResponse> createCart(@PathVariable Long userProfileId, @RequestBody CartRequest cartRequest) {
        return ApiResponse.<CartResponse>builder()
                .code(200)
                .message("Success")
                .data(cartService.addCartForUserProfile(cartRequest, userProfileId))
                .build();
    }

    @PutMapping("/{id}/update")
    ApiResponse<CartResponse> updateCart(@RequestBody CartRequest cartRequest, @PathVariable Long id) {
        return ApiResponse.<CartResponse>builder()
                .code(200)
                .message("Success")
                .data(cartService.updateCart(id, cartRequest))
                .build();
    }

    @DeleteMapping("")
    ResponseEntity<ApiResponse> clearCart(@PathVariable Long cardId) {
        cartService.clearCart(cardId);
        return ResponseEntity.status(200).build();
    }

}
