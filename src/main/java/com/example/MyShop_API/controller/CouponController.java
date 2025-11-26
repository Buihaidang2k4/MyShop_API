package com.example.MyShop_API.controller;

import com.example.MyShop_API.dto.request.CreateCouponRequest;
import com.example.MyShop_API.dto.response.ApiResponse;
import com.example.MyShop_API.service.coupon.ICouponService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("${api.prefix}/coupons")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CouponController {
    ICouponService couponService;

    @GetMapping("/all")
    ResponseEntity<ApiResponse> getCoupons() {
        return ResponseEntity.ok(new ApiResponse<>(200, "All coupon", couponService.getCoupons()));
    }

    @GetMapping("/get-available-coupons/{orderTotal}")
    ResponseEntity<ApiResponse> getAvailableCoupons(@PathVariable BigDecimal orderTotal) {
        return ResponseEntity.ok(new ApiResponse<>(200, "available coupons", couponService.getAvailableCoupons(orderTotal)));
    }

    @PostMapping("/coupon/create")
    ResponseEntity<ApiResponse> createCoupon(@Valid @RequestBody CreateCouponRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(200, "create coupon success", couponService.createCoupon(request)));
    }


}
