package com.example.MyShop_API.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder

public class ReviewResponse {
    private Long reviewId;
    private String customerName;
    private int rating;
    private String comment;
    private String timeAgo;
}
