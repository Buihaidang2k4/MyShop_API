package com.example.MyShop_API.service;

import com.example.MyShop_API.Enum.OrderStatus;
import com.example.MyShop_API.dto.OrderRequest;
import com.example.MyShop_API.dto.OrderResponse;
import com.example.MyShop_API.entity.Order;
import com.example.MyShop_API.entity.OrderItem;
import com.example.MyShop_API.entity.Payment;
import com.example.MyShop_API.entity.Product;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.exception.ErrorCode;
import com.example.MyShop_API.mapper.OrderMapper;
import com.example.MyShop_API.repo.OrderItemRepository;
import com.example.MyShop_API.repo.OrderRepository;
import com.example.MyShop_API.repo.PaymentRepository;
import com.example.MyShop_API.repo.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService {
    OrderRepository orderRepository;
    OrderMapper orderMapper;
    ProductRepository productRepository;
    PaymentRepository paymentRepository;

    public List<OrderResponse> getOrder() {
        log.info("getOrder().........");
        return orderRepository.findAll().stream().map(orderMapper::toResponse).collect(Collectors.toList());
    }

    public OrderResponse getOrder(Long id) {
        log.info("getOrderById().........");
        Order order = orderRepository.findById(id).orElseThrow(() ->
                new AppException(ErrorCode.ORDER_NOT_EXISTED));

        return orderMapper.toResponse(order);
    }

    public OrderResponse placeOrder(Long productId, OrderRequest orderRequest) {
        log.info("placeOrder().........");
        Product findProduct = productRepository.findById(productId).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        // So luong don hang nguoi ta dat mua
        int quantity = orderRequest.getOrderItemRequest().getQuantity();
        double finalPrice = findProduct.getPrice() * quantity * (findProduct.getDiscount() * 0.01);

        if (findProduct.getQuantity() < quantity) {
            throw new AppException(ErrorCode.PRODUCT_IS_NOT_ENOUGH, quantity, findProduct.getQuantity());
        }
        //  Tạo đơn hàng chi tiết
        OrderItem orderItem = OrderItem.builder()
                .product(findProduct)
                .quantity(orderRequest.getOrderItemRequest().getQuantity())
                .discount(findProduct.getDiscount())
                .orderedProductPrice(finalPrice)
                .build();

        // Tao don dat hang
        Order order = orderMapper.toEntity(orderRequest);
        order.setTotalAmount(finalPrice);
        order.setOrderDate(LocalDate.now());
        order.setOrderStatus(OrderStatus.PENDING);

        // Set phuong thuc thanh toan
        var paymentId = orderRequest.getPaymentId();
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_EXISTED));
        order.setPayment(payment);

        // Cap nhat ton kho product
        var inventory = findProduct.getQuantity() - quantity;
        findProduct.setQuantity(inventory);

        orderItem.setOrder(order);
        order.setOrderItems(List.of(orderItem));

        return orderMapper.toResponse(orderRepository.save(order));
    }

    public OrderResponse updateOrder(Long orderid, OrderStatus orderStatus) {
        Order findOrder = orderRepository.findById(orderid).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        findOrder.setOrderStatus(orderStatus);

        return orderMapper.toResponse(orderRepository.save(findOrder));
    }


    public void deleteOrder(Long orderId) {
        log.info("deleteOrder().........");
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));
        orderRepository.delete(order);
    }
}
