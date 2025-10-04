package com.example.MyShop_API.controller;

import com.example.MyShop_API.dto.response.ApiResponse;
import com.example.MyShop_API.dto.request.CategoryRequest;
import com.example.MyShop_API.dto.response.CategoryResponse;
import com.example.MyShop_API.entity.Category;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.service.category.CategoryService;
import com.example.MyShop_API.service.category.ICategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RestController
@RequestMapping("${api.prefix}/categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {
    ICategoryService categoryService;

    @GetMapping("/all")
    ResponseEntity<ApiResponse> getAllCategories() {
        try {
            List<CategoryResponse> categories = categoryService.getAllCategory();
            return ResponseEntity.ok(new ApiResponse<>(200, "success", categories));
        } catch (AppException e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse<>(500, "Error" + INTERNAL_SERVER_ERROR, null));
        }

    }

    @GetMapping("/category/{categoryId}")
    ResponseEntity<ApiResponse> getCategory(@PathVariable Long categoryId) {
        try {
            Category category = categoryService.getCategoryById(categoryId);
            return ResponseEntity.ok(new ApiResponse<>(200, "success", category));
        } catch (AppException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse<>(200, e.getMessage(), null));
        }

    }

    @PostMapping("/category/add")
    ResponseEntity<ApiResponse> addCategory(@RequestBody Category category) {
        try {
            Category theCategory = categoryService.addCategory(category);
            return ResponseEntity.ok(new ApiResponse<>(200, "Add success", theCategory));
        } catch (AppException e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse<>(500, e.getMessage(), null));
        }

    }

    @PutMapping("/category/{categoryId}/update")
    ResponseEntity<ApiResponse> updateCategory(@RequestBody Category category, @PathVariable Long categoryId) {
        try {
            Category updateCategory = categoryService.updateCategory(category, categoryId);
            return ResponseEntity.ok(new ApiResponse<>(200, "Update success", updateCategory));
        } catch (AppException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse<>(404, e.getMessage(), null));
        }

    }

    @DeleteMapping("/category/{categoryId}/delete")
    ResponseEntity<ApiResponse> deleteCategory(@PathVariable Long categoryId) {
        try {
            categoryService.deleteCategoryById(categoryId);
            return ResponseEntity.ok(new ApiResponse<>(200, "Delete success", null));
        } catch (AppException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse<>(404, e.getMessage(), null));
        }

    }
}
