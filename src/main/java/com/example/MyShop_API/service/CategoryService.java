package com.example.MyShop_API.service;

import com.example.MyShop_API.dto.request.CategoryRequest;
import com.example.MyShop_API.dto.response.CategoryResponse;
import com.example.MyShop_API.entity.Category;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.exception.ErrorCode;
import com.example.MyShop_API.mapper.CategoryMapper;
import com.example.MyShop_API.repo.CategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService {
    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;

    public List<CategoryResponse> getCategory() {
        return categoryRepository.findAll().stream().map(categoryMapper::toResponse).collect(Collectors.toList());
    }

    public CategoryResponse getCategoryById(Long id) {
        Category findCategory = categoryRepository.findById(id).orElseThrow(() ->
                new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        return categoryMapper.toResponse(categoryRepository.save(findCategory));
    }

    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        Category category = categoryMapper.toEntity(categoryRequest);

        if (categoryRepository.existsCategoriesByCategoryName(category.getCategoryName()))
            throw new AppException(ErrorCode.CATEGORY_EXISTED);

        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    public CategoryResponse updateCategory(CategoryRequest categoryRequest, Long id) {

        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        categoryMapper.update(categoryRequest, category);

        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    public void deleteCategory(Long id) {
        log.info("Delete category with id {}", id);
        Category findCategory = categoryRepository.findById(id).orElseThrow(() ->
                new AppException(ErrorCode.CATEGORY_NOT_EXISTED));
        categoryRepository.deleteById(id);
    }
}
