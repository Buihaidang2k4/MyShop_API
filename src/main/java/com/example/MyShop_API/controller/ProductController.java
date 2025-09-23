package com.example.MyShop_API.controller;


import com.example.MyShop_API.dto.ApiResponse;
import com.example.MyShop_API.dto.ProductRequest;
import com.example.MyShop_API.dto.ProductResponse;
import com.example.MyShop_API.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {
    ProductService productService;

    @GetMapping
    ApiResponse<List<ProductResponse>> getProducts() {
        return ApiResponse.<List<ProductResponse>>builder()
                .code(200)
                .message("Success")
                .data(productService.getProducts())
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<ProductResponse> getProductById(@PathVariable Long id) {
        return ApiResponse.<ProductResponse>builder()
                .code(200)
                .message("Success")
                .data(productService.getProduct(id))
                .build();
    }

    @GetMapping("/category/{categoryName}")
    ApiResponse<List<ProductResponse>> getProductByCategoryName(@PathVariable String categoryName) {
        return ApiResponse.<List<ProductResponse>>builder()
                .code(200)
                .message("Success")
                .data(productService.searchProductByCategory(categoryName))
                .build();
    }

    @PostMapping("/category/{productId}")
    ApiResponse<ProductResponse> addProduct(@RequestBody ProductRequest productRequest, @PathVariable Long productId) {
        return ApiResponse.<ProductResponse>builder()
                .code(200)
                .message("Success")
                .data(productService.addProduct(productRequest, productId))
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<ProductResponse> updateProduct(@PathVariable Long id, @RequestBody ProductRequest productRequest) {
        return ApiResponse.<ProductResponse>builder()
                .code(200)
                .message("Success")
                .data(productService.updateProduct(productRequest, id))
                .build();
    }

    @PutMapping("/{productId}/image")
    ApiResponse<ProductResponse> updateImage(@PathVariable Long productId, @RequestParam("image") MultipartFile image) throws IOException {
        return ApiResponse.<ProductResponse>builder()
                .code(200)
                .message("Success")
                .data(productService.updateProductImage(productId, image))
                .build();
    }

    @DeleteMapping("/{productId}")
    ApiResponse<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Success")
                .build();
    }
}
