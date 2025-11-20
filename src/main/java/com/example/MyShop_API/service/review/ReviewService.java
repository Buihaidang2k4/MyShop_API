package com.example.MyShop_API.service.review;

import com.example.MyShop_API.Enum.OrderStatus;
import com.example.MyShop_API.dto.request.CreateReviewRequest;
import com.example.MyShop_API.dto.response.ReviewResponse;
import com.example.MyShop_API.entity.Product;
import com.example.MyShop_API.entity.Review;
import com.example.MyShop_API.entity.UserProfile;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.exception.ErrorCode;
import com.example.MyShop_API.mapper.ReviewMapper;
import com.example.MyShop_API.repo.OrderRepository;
import com.example.MyShop_API.repo.ProductRepository;
import com.example.MyShop_API.repo.ReviewRepository;
import com.example.MyShop_API.repo.UserProfileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ReviewService implements IReviewService {
    ReviewRepository reviewRepository;
    OrderRepository orderRepository;
    ProductRepository productRepository;
    UserProfileRepository profileRepository;
    ReviewMapper reviewMapper;

    @Override
    public Review createReview(CreateReviewRequest request, Long profileId) {
        boolean purchased = orderRepository.existsByProfileProfileIdAndOrderItemsProductProductIdAndOrderStatusIn(profileId, request.getProductId(), List.of(OrderStatus.DELIVERED));

        if (!purchased) throw new AppException(ErrorCode.REVIEW_NOT_PURCHASED);

        if (reviewRepository.existsByProfileProfileIdAndProductProductIdAndDeletedFalse(profileId, request.getProductId())) {
            throw new AppException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }

        Product product = productRepository.findById(request.getProductId()).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
        UserProfile profile = profileRepository.findById(profileId).orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_EXISTED));
        Review review = Review.builder()
                .product(product)
                .profile(profile)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        log.info("Profile {} reviewed product {}", profile.getProfileId(), request.getProductId());
        return reviewRepository.save(review);
    }

    @Override
    public Page<ReviewResponse> findByProductId(Long productId, Pageable pageable) {
        return reviewRepository.findByProductProductIdAndDeletedFalse(productId, pageable).map(reviewMapper::toResponse);
    }
}
