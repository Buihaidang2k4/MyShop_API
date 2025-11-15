package com.example.MyShop_API.mapper;

import com.example.MyShop_API.dto.request.AddProductRequest;
import com.example.MyShop_API.dto.response.ProductResponse;
import com.example.MyShop_API.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "category", source = "category")
    Product toEntity(AddProductRequest addProductRequest);

    ProductResponse toResponse(Product product);

    List<ProductResponse> toResponseList(List<Product> products);

    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "category", ignore = true)
    void update(AddProductRequest addProductRequest, @MappingTarget Product product);
}
