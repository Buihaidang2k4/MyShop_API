package com.example.MyShop_API.mapper;

import com.example.MyShop_API.dto.request.CartRequest;
import com.example.MyShop_API.dto.response.CartResponse;
import com.example.MyShop_API.entity.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {
    Cart toEntity(CartRequest cartRequest);

    CartResponse toResponse(Cart cart);

    List<CartResponse> toResponseList(List<Cart> cartList);

    void update(CartRequest cartRequest, @MappingTarget Cart cart);
}
