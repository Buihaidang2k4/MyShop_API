package com.example.MyShop_API.mapper;

import com.example.MyShop_API.dto.request.OrderRequest;
import com.example.MyShop_API.dto.response.OrderResponse;
import com.example.MyShop_API.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {

    Order toEntity(OrderRequest orderRequest);

    @Mapping(source = "payment.paymentId", target = "paymentId")
    @Mapping(source = "orderItems", target = "orderItemResponses")
    @Mapping(source = "profile.profileId", target = "profileId")
    @Mapping(source = "coupon", target = "couponResponse")
    OrderResponse toResponse(Order order);

    List<OrderResponse> toResponse(List<Order> orders);

    void update(OrderRequest orderRequest, @MappingTarget Order order);
}
