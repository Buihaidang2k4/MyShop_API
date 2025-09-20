package com.example.MyShop_API.controller;

import com.example.MyShop_API.dto.ApiResponse;
import com.example.MyShop_API.dto.CartRequest;
import com.example.MyShop_API.dto.CartResponse;
import com.example.MyShop_API.service.CartService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController {
    CartService cartService;

    @GetMapping
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

    @PostMapping("/userProfile/{userProfileId}")
    ApiResponse<CartResponse> createCart(@PathVariable Long userProfileId, @RequestBody CartRequest cartRequest) {
        return ApiResponse.<CartResponse>builder()
                .code(200)
                .message("Success")
                .data(cartService.addCartForUserProfile(cartRequest, userProfileId))
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<CartResponse> updateCart(@RequestBody CartRequest cartRequest, @PathVariable Long id) {
        return ApiResponse.<CartResponse>builder()
                .code(200)
                .message("Success")
                .data(cartService.updateCart(id, cartRequest))
                .build();
    }

}
