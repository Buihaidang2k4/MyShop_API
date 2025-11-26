package com.example.MyShop_API.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateReviewRequest {

    @Min(1)
    @Max(5)
    private int rating;
    private String comment;
}
