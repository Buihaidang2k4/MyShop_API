package com.example.MyShop_API.controller;


import com.example.MyShop_API.anotation.AllAccess;
import com.example.MyShop_API.dto.response.ApiResponse;
import com.example.MyShop_API.dto.request.AddProductRequest;
import com.example.MyShop_API.dto.response.ProductResponse;
import com.example.MyShop_API.entity.Cart;
import com.example.MyShop_API.entity.Category;
import com.example.MyShop_API.entity.Product;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.mapper.ProductMapper;
import com.example.MyShop_API.service.product.IProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestController
@RequestMapping("${api.prefix}/products")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {
    IProductService productService;
    ProductMapper productMapper;

    @GetMapping("/category/by-category")
    ResponseEntity<ApiResponse> getProductByCategory(@RequestParam String category) {
        try {
            List<Product> products = productService.searchProductByCategory(category);
            return ResponseEntity.ok(new ApiResponse<>(200, "GetProductByCategory", productMapper.toResponseList(products)));
        } catch (AppException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(404, e.getMessage(), null));
        }
    }

    @GetMapping("/all")
    ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts() {
        try {
            List<ProductResponse> products = productMapper.toResponseList(productService.getProducts());
            return ResponseEntity.ok(new ApiResponse(200, "Get all products successfully!", products));
        } catch (AppException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(404, e.getMessage(), null));
        }
    }

    @GetMapping("/page")
    ResponseEntity<ApiResponse> getCarts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "productId") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> products = productService.getProducts(pageable);
        Page<ProductResponse> productRes = products.map(productMapper::toResponse);

        Map<String, Object> res = new HashMap<>();
        res.put("content", productRes.getContent());
        res.put("size", productRes.getSize());
        res.put("currentPage", productRes.getNumber());
        res.put("totalItems", productRes.getTotalElements());
        res.put("totalPages", productRes.getTotalPages());
        res.put("sortBy", sortBy);

        return ResponseEntity.ok(new ApiResponse(200, "success", res));
    }

    @GetMapping("/product/{productId}")
    ResponseEntity<ApiResponse> getProductById(@PathVariable Long productId) {
        try {
            Product product = productService.getProductById(productId);
            return ResponseEntity.ok(new ApiResponse(200, "Get product successfully!", productMapper.toResponse(product)));
        } catch (AppException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(404, e.getMessage(), null));
        }
    }

    @GetMapping("/product/by-productName")
    ResponseEntity<ApiResponse> getProductByName(@RequestParam String productName) {
        try {
            Product product = productService.getProductByName(productName);
            return ResponseEntity.ok(new ApiResponse(200, "Get product successfully!", productMapper.toResponse(product)));
        } catch (AppException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(404, e.getMessage(), null));
        }
    }

    @PostMapping("/product/add")
    ResponseEntity<ApiResponse<ProductResponse>> addProduct(@RequestBody AddProductRequest addProductRequest) {
        try {
            Product product = productService.addProduct(addProductRequest);
            return ResponseEntity.status(CREATED).body(new ApiResponse(201, "Add product successfully!", productMapper.toResponse(product)));
        } catch (AppException e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(500, e.getMessage(), null));
        }
    }

    @PutMapping("/product/{productId}/update")
    ResponseEntity<ApiResponse> updateProduct(@PathVariable Long productId, @RequestBody AddProductRequest addProductRequest) {
        try {
            Product product = productService.updateProduct(addProductRequest, productId);
            return ResponseEntity.status(ACCEPTED).body(new ApiResponse(200, "Update product successfully!", productMapper.toResponse(product)));
        } catch (AppException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getErrorCode().getCode(), e.getMessage(), null));
        }
    }

    @DeleteMapping("/product/{productId}/delete")
    ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long productId) {
        try {
            productService.deleteProductById(productId);
            return ResponseEntity.ok(new ApiResponse(200, "Delete product successfully!", productId));
        } catch (AppException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getErrorCode().getCode(), e.getMessage(), null));
        }
    }
}
