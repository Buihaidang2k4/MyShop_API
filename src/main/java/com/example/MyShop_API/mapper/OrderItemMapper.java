package com.example.MyShop_API.mapper;

import com.example.MyShop_API.dto.OrderItemRequest;
import com.example.MyShop_API.dto.OrderItemResponse;
import com.example.MyShop_API.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    OrderItem toEntity(OrderItemRequest orderItemRequest);


    @Mapping(source = "order.orderId", target = "orderId")
    @Mapping(source = "product.productId", target = "productId")
    OrderItemResponse toResponse(OrderItem orderItem);

    void updateOrder(OrderItemRequest orderItemRequest, @MappingTarget OrderItem orderItem);
}
