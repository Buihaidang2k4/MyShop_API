package com.example.MyShop_API.mapper;

import com.example.MyShop_API.dto.request.CreateCouponRequest;
import com.example.MyShop_API.entity.Coupon;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CouponMapper {
    Coupon toCoupon(CreateCouponRequest request);

}
