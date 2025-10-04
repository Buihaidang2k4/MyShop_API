package com.example.MyShop_API.dto.response;

import com.example.MyShop_API.entity.Category;
import com.example.MyShop_API.entity.Image;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {
    Long productId;
    String productName;
    String description;
    Integer quantity;
    double price;
    double discount;
    double specialPrice;
    Category category;
    List<Image> images;
}
