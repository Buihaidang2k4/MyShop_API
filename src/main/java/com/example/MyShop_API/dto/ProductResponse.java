package com.example.MyShop_API.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {
    Long productId;
    Long categoryId;
    String productName;
    String image;
    String description;
    Integer quantity;
    double price;
    double discount;
    double specialPrice;
}
