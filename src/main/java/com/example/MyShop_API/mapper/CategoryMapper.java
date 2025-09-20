package com.example.MyShop_API.mapper;

import com.example.MyShop_API.dto.CategoryRequest;
import com.example.MyShop_API.dto.CategoryResponse;
import com.example.MyShop_API.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toEntity(CategoryRequest categoryRequest);

    CategoryResponse toResponse(Category category);

    void update(CategoryRequest categoryRequest, @MappingTarget Category category);
}
