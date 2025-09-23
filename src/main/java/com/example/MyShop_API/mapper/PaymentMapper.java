package com.example.MyShop_API.mapper;

import com.example.MyShop_API.dto.OrderRequest;
import com.example.MyShop_API.dto.PaymentRequest;
import com.example.MyShop_API.dto.PaymentResponse;
import com.example.MyShop_API.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    Payment toEntity(PaymentRequest paymentRequest);

    PaymentResponse toResponse(Payment payment);

    void update(PaymentRequest paymentRequest, @MappingTarget Payment payment);
}
