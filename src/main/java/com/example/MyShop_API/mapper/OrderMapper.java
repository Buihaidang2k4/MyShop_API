package com.example.MyShop_API.mapper;

import com.example.MyShop_API.dto.request.OrderRequest;
import com.example.MyShop_API.dto.response.OrderResponse;
import com.example.MyShop_API.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    Order toEntity(OrderRequest orderRequest);

    @Mapping(source = "payment.paymentId", target = "paymentId")
    @Mapping(source = "orderItems", target = "orderItemResponses")
    OrderResponse toResponse(Order order);

    void update(OrderRequest orderRequest, @MappingTarget Order order);
}
