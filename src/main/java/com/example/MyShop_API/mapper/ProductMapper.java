package com.example.MyShop_API.mapper;

import com.example.MyShop_API.dto.ProductRequest;
import com.example.MyShop_API.dto.ProductResponse;
import com.example.MyShop_API.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toEntity(ProductRequest productRequest);

    ProductResponse toResponse(Product product);

    void update(ProductRequest productRequest, @MappingTarget Product product);
}
