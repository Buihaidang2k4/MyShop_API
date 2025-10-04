package com.example.MyShop_API.service.order;

import com.example.MyShop_API.Enum.OrderStatus;
import com.example.MyShop_API.dto.request.OrderRequest;
import com.example.MyShop_API.dto.response.OrderResponse;

import java.util.List;

public interface IOrderService {

    OrderResponse getOrder(Long orderId);

    OrderResponse placeOrder(Long productId, OrderRequest orderRequest);

    OrderResponse updateOrder(Long orderid, OrderStatus orderStatus);

    void deleteOrder(Long orderId);
}
