package com.example.MyShop_API.service.order;

import com.example.MyShop_API.Enum.OrderStatus;
import com.example.MyShop_API.dto.request.OrderRequest;
import com.example.MyShop_API.dto.response.OrderResponse;
import com.example.MyShop_API.entity.Order;
import org.aspectj.weaver.ast.Or;

import java.util.List;

public interface IOrderService {
    List<Order> getOrders();

    Order getOrder(Long orderId);

    List<Order> getOrdersByStatus(OrderStatus status);

    List<Order> getUserOrders(Long userProfileId);

    Order placeOrder(Long userProfileId);

    void cancelOrder(Long orderId);

    Order updateOrderStatus(Long orderId, OrderStatus orderStatus);


    void deleteOrder(Long orderId);
}
