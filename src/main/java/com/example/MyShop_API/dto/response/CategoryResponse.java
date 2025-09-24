package com.example.MyShop_API.dto;

import com.example.MyShop_API.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    Long categoryId;
    String categoryName;
    List<ProductResponse> products;
}
