package com.example.MyShop_API.service;

import com.example.MyShop_API.dto.request.OrderItemRequest;
import com.example.MyShop_API.dto.response.OrderItemResponse;
import com.example.MyShop_API.entity.OrderItem;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.exception.ErrorCode;
import com.example.MyShop_API.mapper.OrderItemMapper;
import com.example.MyShop_API.repo.OrderItemRepository;
import com.example.MyShop_API.repo.OrderRepository;
import com.example.MyShop_API.repo.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderItemService {
    OrderItemRepository orderItemRepository;
    OrderItemMapper orderItemMapper;
    OrderRepository orderRepository;
    ProductRepository productRepository;

    public List<OrderItemResponse> getOrderItem() {
        return orderItemRepository.findAll().stream().map(orderItemMapper::toResponse).collect(Collectors.toList());
    }

    public OrderItemResponse getOrderItem(Long orderItemId) {
        OrderItem findOrderItem = orderItemRepository.findById(orderItemId).orElseThrow(() ->
                new AppException(ErrorCode.ORDER_ITEM_NOT_EXISTED));

        return orderItemMapper.toResponse(findOrderItem);
    }


    public OrderItemResponse updateOrderItem(Long orderItemId, OrderItemRequest orderItemRequest) {
        OrderItem findOrderItem = orderItemRepository.findById(orderItemId).orElseThrow(() -> new AppException(ErrorCode.ORDER_ITEM_NOT_EXISTED));
        orderItemMapper.updateOrder(orderItemRequest, findOrderItem);

        findOrderItem = orderItemRepository.save(findOrderItem);
        return orderItemMapper.toResponse(findOrderItem);
    }


    public void deleteOrderItem(Long orderItemId) {
        OrderItem findOrderItem = orderItemRepository.findById(orderItemId).orElseThrow(() -> new AppException(ErrorCode.ORDER_ITEM_NOT_EXISTED));
        orderItemRepository.delete(findOrderItem);
    }
}
