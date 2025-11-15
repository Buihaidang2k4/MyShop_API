package com.example.MyShop_API.service.order;

import com.example.MyShop_API.Enum.OrderStatus;
import com.example.MyShop_API.dto.request.OrderRequest;
import com.example.MyShop_API.dto.response.OrderResponse;
import com.example.MyShop_API.entity.*;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.exception.ErrorCode;
import com.example.MyShop_API.mapper.OrderMapper;
import com.example.MyShop_API.repo.OrderRepository;
import com.example.MyShop_API.repo.PaymentRepository;
import com.example.MyShop_API.repo.ProductRepository;
import com.example.MyShop_API.service.cart.ICartService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService implements IOrderService {
    OrderRepository orderRepository;
    OrderMapper orderMapper;
    ICartService cartService;
    ProductRepository productRepository;

    @Override
    public List<Order> getOrders() {
        return orderRepository.findAll().stream().toList();
    }

    @Override
    public Order getOrder(Long orderId) {
        log.info("getOrderById().........");
        Order order = orderRepository.findById(orderId).orElseThrow(() ->
                new AppException(ErrorCode.ORDER_NOT_EXISTED));

        return order;
    }

    @Override
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByOrderStatus(status);
    }

    @Override
    public List<Order> getUserOrders(Long profileId) {
        return orderRepository.findByProfileProfile_id(profileId);
    }

    @Transactional
    @Override
    public Order placeOrder(Long profileId) {
        log.info("placeOrder().........");

        Cart cart = cartService.getCartByUserProfileId(profileId);
        Order order = createOrder(cart);
        List<OrderItem> orderItems = createOrderItem(order, cart);
        order.setOrderItems(new HashSet<>(orderItems));
        order.setTotalAmount(calculateTotalAmount(orderItems));
        Order saveOrder = orderRepository.save(order);
        cartService.clearCart(cart.getCartId());

        // Thanh toan
        return saveOrder;
    }

    @Transactional
    @Override
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));
        if (order.getOrderStatus() == OrderStatus.SHIPPED || order.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new AppException(ErrorCode.ORDER_CANCEL_FAILED);
        }

        // Hoàn kho
        order.getOrderItems().forEach(orderItem -> {
            Product product = orderItem.getProduct();
//            product.setQuantity(product.getQuantity() + orderItem.getQuantity());
            productRepository.save(product);
        });

        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    @Override
    public Order updateOrderStatus(Long orderId, OrderStatus orderStatus) {
        Order order = orderRepository.findById(orderId).orElseThrow(() ->
                new AppException(ErrorCode.ORDER_NOT_EXISTED));
        order.setOrderStatus(orderStatus);
        return orderRepository.save(order);
    }

    private Order createOrder(Cart cart) {
        Order order = Order.builder()
                .profile(cart.getProfile())
                .orderDate(LocalDate.now())
                .orderStatus(OrderStatus.PENDING)
                .build();
        return order;
    }

    private List<OrderItem> createOrderItem(Order order, Cart cart) {
        return cart.getCartItems().stream().map(cartItem -> {
            Product product = cartItem.getProduct();
//            product.setQuantity(product.getQuantity() - cartItem.getQuantity());

            return OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .price(cartItem.getUnitPrice())
                    .build();
        }).toList();
    }

    private BigDecimal calculateTotalAmount(List<OrderItem> orderItems) {
        return orderItems.
                stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    @Override
    public void deleteOrder(Long orderId) {
        log.info("deleteOrder().........");
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));
        orderRepository.delete(order);
    }
}
