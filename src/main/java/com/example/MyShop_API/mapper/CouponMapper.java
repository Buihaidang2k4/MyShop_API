package com.example.MyShop_API.mapper;

import com.example.MyShop_API.dto.request.CreateCouponRequest;
import com.example.MyShop_API.dto.response.CouponResponse;
import com.example.MyShop_API.entity.Coupon;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CouponMapper {
    Coupon toCoupon(CreateCouponRequest request);

    CouponResponse toCouponResponse(Coupon coupon);

    List<CouponResponse> toCouponResponses(List<Coupon> coupons);
}
