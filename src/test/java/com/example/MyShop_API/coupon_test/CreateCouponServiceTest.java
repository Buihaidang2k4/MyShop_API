package com.example.MyShop_API.coupon_test;

import com.example.MyShop_API.Enum.DiscountType;
import com.example.MyShop_API.dto.request.CreateCouponRequest;
import com.example.MyShop_API.entity.Coupon;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.mapper.CouponMapper;
import com.example.MyShop_API.repo.CouponRepository;
import com.example.MyShop_API.service.coupon.CouponService;
import com.example.MyShop_API.service.coupon.ICouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreateCouponServiceTest {
    @InjectMocks
    CouponService couponService;

    @Mock
    CouponRepository couponRepository;

    @Mock
    CouponMapper couponMapper;

    CreateCouponRequest req;

    @BeforeEach
    void setUp() {
        req = CreateCouponRequest.builder()
                .discountType(DiscountType.PERCENTAGE)
                .discountPercent(BigDecimal.valueOf(10))
                .discountAmount(null)
                .maxDiscountAmount(BigDecimal.valueOf(50000))
                .minOrderValue(BigDecimal.ZERO)
                .startDate(LocalDateTime.now())
                .expiryDate(LocalDateTime.now().plusDays(1))
                .usageLimit(10)
                .usedCount(0)
                .limitPerUser(true)
                .maxUsesPerUser(1)
                .build();

        when(couponRepository.existsByCode(anyString())).thenReturn(false);
        when(couponMapper.toCoupon(any())).thenAnswer(a -> {
            CreateCouponRequest r = a.getArgument(0);
            return Coupon.builder()
                    .discountType(r.getDiscountType())
                    .discountPercent(r.getDiscountPercent())
                    .discountAmount(r.getDiscountAmount())
                    .startDate(r.getStartDate())
                    .expiryDate(r.getExpiryDate())
                    .usageLimit(r.getUsageLimit())
                    .usedCount(r.getUsedCount())
                    .limitPerUser(r.isLimitPerUser())
                    .maxUsesPerUser(r.getMaxUsesPerUser())
                    .build();
        });
    }

//    @Test
//    void shouldThrowWhenPercentageButPercentNull() {
//        req.setDiscountPercent(null);
//        assertThrows(AppException.class,
//                () -> couponService.createCoupon(req),
//                "Expected COUPON_INVALID_PERCENTAGE");
//    }

//    @Test
//    void shouldCreatePercentageCouponSuccessfully() {
//        when(couponRepository.save(any())).thenAnswer(a -> a.getArgument(0));
//        Coupon c = couponService.createCoupon(req);
//        assertEquals(DiscountType.PERCENTAGE, c.getDiscountType());
//    }


}
