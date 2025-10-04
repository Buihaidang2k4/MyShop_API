package com.example.MyShop_API.service.category;

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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService implements ICategoryService {
    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;

    public List<CategoryResponse> getAllCategory() {
        return categoryRepository.findAll().stream().map(categoryMapper::toResponse).collect(Collectors.toList());
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() ->
                new AppException(ErrorCode.CATEGORY_NOT_EXISTED));
    }

    @Override
    public Category getCategoryByName(String name) {
        return categoryRepository.findByCategoryName(name);
    }

    public Category addCategory(Category category) {
        return Optional.of(category).filter(c -> !categoryRepository.existsCategoriesByCategoryName(category.getCategoryName()))
                .map(categoryRepository::save)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_EXISTED));
    }

    public Category updateCategory(Category category, Long id) {
        return Optional.ofNullable(getCategoryById(id)).map(oldCategory -> {
            oldCategory.setCategoryName(category.getCategoryName());
            return categoryRepository.save(oldCategory);
        }).orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

    }

    public void deleteCategoryById(Long id) {
        categoryRepository.findById(id).ifPresentOrElse(categoryRepository::delete, () -> {
            throw new AppException(ErrorCode.CATEGORY_NOT_EXISTED);
        });
    }
}
