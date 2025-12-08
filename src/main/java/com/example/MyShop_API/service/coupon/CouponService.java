package com.example.MyShop_API.service.coupon;

import com.example.MyShop_API.Enum.DiscountType;
import com.example.MyShop_API.anotation.AdminOnly;
import com.example.MyShop_API.dto.request.CreateCouponRequest;
import com.example.MyShop_API.entity.Coupon;
import com.example.MyShop_API.entity.Order;
import com.example.MyShop_API.entity.UserProfile;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.exception.ErrorCode;
import com.example.MyShop_API.mapper.CouponMapper;
import com.example.MyShop_API.repo.CouponRepository;
import com.example.MyShop_API.repo.OrderRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CouponService implements ICouponService {
    CouponRepository couponRepository;
    CouponMapper couponMapper;
    OrderRepository orderRepository;

    // ============= ALL COUPONS ==============
    public List<Coupon> getCoupons() {
        return couponRepository.findAll();
    }


    // ================== CREATE COUPON (ADMIN) ==========================
    @AdminOnly
    @Override
    public Coupon createCoupon(CreateCouponRequest request) {
        validateCreateCoupon(request);

        Coupon coupon = couponMapper.toCoupon(request);
        if (request.getStartDate() == null) {
            coupon.setStartDate(LocalDateTime.now());
        }
        String couponCodeRandom = generateCode(request.getCode());
        coupon.setCode(couponCodeRandom);

        // check couponCode duplicate
        if (couponRepository.existsByCode(coupon.getCode())) {
            throw new AppException(ErrorCode.COUPON_CODE_IS_EXISTED);
        }
        return couponRepository.save(coupon);
    }


    // ================ LIST AVAILABLE COUPONS ===========================
    @Override
    public List<Coupon> getAvailableCoupons(BigDecimal orderTotal) {
        return couponRepository.findAvailableCoupons(orderTotal, LocalDateTime.now());
    }

    @Override
    public List<Coupon> getAvailableCoupons() {
        return couponRepository.findCouponByEnabledTrue();
    }


    // =============== APPLY COUPON TO ORDER =================
    @Override
    public BigDecimal applyCouponToOrder(String couponCode,
                                         BigDecimal orderTotal,
                                         Order order,
                                         UserProfile profile) {
        log.info("================== START APPLY COUPON ==================");
        Coupon coupon = couponRepository.findByCodeIgnoreCaseAndEnabledTrue(couponCode)
                .filter(Coupon::isUsable)
                .filter(c -> c.getMinOrderValue() == null || orderTotal.compareTo(c.getMinOrderValue()) >= 0)
                .orElseThrow(() -> new AppException(ErrorCode.COUPON_INVALID));

        // Check limit per user
        if (coupon.isLimitPerUser()) {
            int usedCount = orderRepository.countByProfile_ProfileIdAndCoupon_CouponId(
                    profile.getProfileId(), coupon.getCouponId());
            if (usedCount >= coupon.getMaxUsesPerUser()) {
                throw new AppException(ErrorCode.COUPON_LIMIT_PER_USER_EXCEEDED);
            }
        }

        // Calculate discount
        BigDecimal discount = BigDecimal.ZERO;
        if (coupon.getDiscountType() == DiscountType.PERCENTAGE) {
            discount = orderTotal.multiply(coupon.getDiscountPercent())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            if (coupon.getMaxDiscountAmount() != null && discount.compareTo(coupon.getMaxDiscountAmount()) > 0) {
                discount = coupon.getMaxDiscountAmount();
            }
        } else if (coupon.getDiscountAmount() != null) {
            discount = coupon.getDiscountAmount();
        }

        order.setCoupon(coupon);
        coupon.incrementUsedCount();
        couponRepository.save(coupon); // ← BẮT BUỘC

        log.info("================= END APPLY COUPON ================");
        log.info("Coupon {} applied to order {}. Discount: {}", couponCode, order.getOrderId(), discount);
        return discount;
    }

    private String generateCode(String prefix) {
        String random = UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        return prefix.toUpperCase() + "-" + random;
    }

    private void validateCreateCoupon(CreateCouponRequest request) {
        // 1. Validate discount type + value
        if (request.getDiscountType() == DiscountType.PERCENTAGE) {
            if (request.getDiscountPercent() == null) {
                throw new AppException(ErrorCode.COUPON_INVALID_PERCENTAGE);
            }
            if (request.getDiscountAmount() != null) {
                throw new AppException(ErrorCode.COUPON_INVALID_FIXED_AMOUNT);
            }
        } else if (request.getDiscountType() == DiscountType.FIXED_AMOUNT) {
            if (request.getDiscountAmount() == null) {
                throw new AppException(ErrorCode.COUPON_INVALID_FIXED_AMOUNT);
            }
            if (request.getDiscountPercent() != null) {
                throw new AppException(ErrorCode.COUPON_INVALID_PERCENTAGE);
            }
        } else {
            throw new AppException(ErrorCode.COUPON_INVALID_TYPE);
        }

        // 2. Validate date range
        if (request.getStartDate() != null && request.getExpiryDate() != null &&
                request.getStartDate().isAfter(request.getExpiryDate())) {
            throw new AppException(ErrorCode.COUPON_INVALID_DATE_RANGE);
        }

        // 3. usageLimit >= usedCount
        if (request.getUsageLimit() != null &&
                request.getUsedCount() > request.getUsageLimit()) {
            throw new AppException(ErrorCode.COUPON_INVALID_USAGE_LIMIT);
        }


        // 4. per-user validate
        if (request.isLimitPerUser() &&
                (request.getMaxUsesPerUser() == null || request.getMaxUsesPerUser() < 1)) {
            throw new AppException(ErrorCode.COUPON_INVALID_MAX_USES_PER_USER);
        }
    }
}
