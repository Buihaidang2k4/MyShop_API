package com.example.MyShop_API.controller;

import com.example.MyShop_API.dto.request.CreateReviewRequest;
import com.example.MyShop_API.dto.response.ApiResponse;
import com.example.MyShop_API.dto.response.ReviewResponse;
import com.example.MyShop_API.entity.Review;
import com.example.MyShop_API.entity.User;
import com.example.MyShop_API.entity.UserProfile;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.exception.ErrorCode;
import com.example.MyShop_API.mapper.ReviewMapper;
import com.example.MyShop_API.repo.UserRepository;
import com.example.MyShop_API.service.review.IReviewService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("${api.prefix}/reviews")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewController {
    IReviewService reviewService;
    ReviewMapper reviewMapper;
    UserRepository userRepository;

    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<ReviewResponse>> getReviewsByProduct(
            @PathVariable Long productId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ReviewResponse> reviews = reviewService.findByProductId(productId, pageable);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping("/create-review")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewResponse> createReview(
            @Valid @RequestBody CreateReviewRequest request,
            Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        UserProfile profile = user.getProfile();
        if (profile == null) throw new AppException(ErrorCode.PROFILE_NOT_EXISTED);
        Review review = reviewService.createReview(request, profile.getProfileId());
        ReviewResponse response = reviewMapper.toResponse(review);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
