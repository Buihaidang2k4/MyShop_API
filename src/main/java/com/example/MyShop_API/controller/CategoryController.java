package com.example.MyShop_API.controller;

import com.example.MyShop_API.dto.response.ApiResponse;
import com.example.MyShop_API.dto.request.CategoryRequest;
import com.example.MyShop_API.dto.response.CategoryResponse;
import com.example.MyShop_API.service.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {
    CategoryService categoryService;

    @GetMapping
    ApiResponse<List<CategoryResponse>> getCategory() {
        return ApiResponse.<List<CategoryResponse>>builder()
                .code(200)
                .message("Success")
                .data(categoryService.getCategory())
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<CategoryResponse> getCategory(@PathVariable Long id) {
        return ApiResponse.<CategoryResponse>builder()
                .code(200)
                .message("Success")
                .data(categoryService.getCategoryById(id))
                .build();
    }

    @PostMapping
    ApiResponse<CategoryResponse> saveCategory(@RequestBody CategoryRequest categoryRequest) {
        return ApiResponse.<CategoryResponse>builder()
                .code(200)
                .message("Success")
                .data(categoryService.createCategory(categoryRequest))
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<CategoryResponse> updateCategory(@RequestBody CategoryRequest categoryRequest, @PathVariable Long id) {
        return ApiResponse.<CategoryResponse>builder()
                .code(200)
                .message("Success")
                .data(categoryService.updateCategory(categoryRequest, id))
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Success")
                .build();
    }
}
