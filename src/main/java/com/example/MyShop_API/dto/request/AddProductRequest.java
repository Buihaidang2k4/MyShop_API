package com.example.MyShop_API.dto.request;

import com.example.MyShop_API.entity.Category;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddProductRequest {
    Long productId;
    String productName;
    String description;
    Category category;
    int quantity;
    BigDecimal price;
    BigDecimal discount;
}
