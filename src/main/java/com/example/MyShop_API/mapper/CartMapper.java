package com.example.MyShop_API.mapper;

import com.example.MyShop_API.dto.CartRequest;
import com.example.MyShop_API.dto.CartResponse;
import com.example.MyShop_API.entity.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CartMapper {
    Cart toEntity(CartRequest cartRequest);

    CartResponse toResponse(Cart cart);

    void update(CartRequest cartRequest, @MappingTarget Cart cart);
}
