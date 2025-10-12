package com.example.MyShop_API.controller;

import com.example.MyShop_API.dto.response.ApiResponse;
import com.example.MyShop_API.dto.request.CartRequest;
import com.example.MyShop_API.dto.response.CartResponse;
import com.example.MyShop_API.entity.Cart;
import com.example.MyShop_API.service.cart.CartService;
import com.example.MyShop_API.service.cart.ICartService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/carts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController {
    ICartService cartService;

    @GetMapping("/all")
    ResponseEntity<ApiResponse> getCarts() {
        List<Cart> carts = cartService.getCarts();
        return ResponseEntity.ok(new ApiResponse<>(200, "success", carts));
    }

    @GetMapping("/page")
    ResponseEntity<ApiResponse> getCarts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "cartId") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Cart> carts = cartService.getAllCarts(pageable);

        Map<String, Object> res = new HashMap<>();
        res.put("content", carts.getContent());
        res.put("currentPage", carts.getNumber());
        res.put("totalItems", carts.getTotalElements());
        res.put("totalPages", carts.getTotalPages());
        res.put("size", carts.getSize());
        res.put("sortBy", sortBy);

        return ResponseEntity.ok(new ApiResponse(200, "success", res));
    }

    @GetMapping("/cart/{cardId}")
    ResponseEntity<ApiResponse> getCart(@PathVariable Long cardId) {
        return ResponseEntity.ok(new ApiResponse<>(200, "success", cartService.getCartById(cardId)));
    }

    @GetMapping("/cart/user-profile/{userProfileId}")
    ResponseEntity<ApiResponse> getCartByUserProfile(@PathVariable Long userProfileId) {
        return ResponseEntity.ok(new ApiResponse<>(200, "success", cartService.getCartByUserProfileId(userProfileId)));
    }

    @PostMapping("/userProfile/{userProfileId}/add")
    ApiResponse<CartResponse> createCart(@PathVariable Long userProfileId, @RequestBody CartRequest cartRequest) {
        return ApiResponse.<CartResponse>builder()
                .code(200)
                .message("Success")
                .data(cartService.addCartForUserProfile(cartRequest, userProfileId))
                .build();
    }

    @PutMapping("/cart/{id}/update")
    ApiResponse<CartResponse> updateCart(@RequestBody CartRequest cartRequest, @PathVariable Long id) {
        return ApiResponse.<CartResponse>builder()
                .code(200)
                .message("Success")
                .data(cartService.updateCart(id, cartRequest))
                .build();
    }

    @DeleteMapping("/cart/{cartId}/clear")
    ResponseEntity<ApiResponse> clearCart(@PathVariable Long cartId) {
        cartService.clearCart(cartId);
        return ResponseEntity.status(200).build();
    }

    @GetMapping("/cart/{cardId}/total-price")
    ResponseEntity<ApiResponse> getTotalPrice(@PathVariable Long cartId) {
        try {
            BigDecimal totalPrice = cartService.getTotalPrice(cartId);
            return ResponseEntity.ok(new ApiResponse(200, "Total Price", totalPrice));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(500, "Error", e.getMessage()));
        }
    }
}
