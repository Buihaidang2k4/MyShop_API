package com.example.MyShop_API.dto.response;

import com.example.MyShop_API.entity.Review;
import com.example.MyShop_API.entity.UserProfile;
import lombok.Builder;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Builder

public class ReviewResponse {
    private Long reviewId;
    private String customerName;
    private int rating;
    private String comment;
    private String timeAgo;

    public static ReviewResponse from(Review r) {
        UserProfile p = r.getProfile();
        String name = (p.getFirstName() != null ? p.getFirstName() + " " : "")
                + (p.getLastName() != null ? p.getLastName() : "Khách hàng");

        return ReviewResponse.builder()
                .reviewId(r.getReviewId())
                .customerName(name.trim())
                .rating(r.getRating())
                .comment(r.getComment())
                .timeAgo(formatTimeAgo(r.getCreatedAt()))
                .build();
    }

    private static String formatTimeAgo(LocalDateTime date) {
        Duration d = Duration.between(date, LocalDateTime.now());
        long days = d.toDays();
        if (days > 30) return "Hơn 1 tháng trước";
        if (days > 0) return days + " ngày trước";
        if (d.toHours() > 0) return d.toHours() + " giờ trước";
        return "Vừa xong";
    }
}
