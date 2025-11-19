package com.example.MyShop_API.service.order;

import com.example.MyShop_API.Enum.OrderStatus;
import com.example.MyShop_API.dto.request.OrderRequest;
import com.example.MyShop_API.dto.response.OrderResponse;
import com.example.MyShop_API.dto.response.VnpayResponse;
import com.example.MyShop_API.entity.Order;
import com.example.MyShop_API.entity.User;
import com.example.MyShop_API.exception.AppException;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.weaver.ast.Or;

import java.util.List;

public interface IOrderService {
    List<Order> getOrders();

    Order getOrder(Long orderId);

    List<Order> getOrdersByStatus(OrderStatus status);

    List<Order> getUserOrders(Long userProfileId);

    Object buyNow(OrderRequest orderRequest, HttpServletRequest request);

    Object placeOrder(OrderRequest orderRequest, HttpServletRequest request) throws AppException;

    VnpayResponse finalizeVnPayCallback(HttpServletRequest request);

    void confirmCashOrder(Long orderId, User admin);

    void cancelOrder(Long orderId);

    Order updateOrderStatus(Long orderId, OrderStatus orderStatus, User admin);


    void deleteOrder(Long orderId);
}
