package com.example.MyShop_API.service.order;

import com.example.MyShop_API.Enum.OrderStatus;
import com.example.MyShop_API.Enum.PaymentMethod;
import com.example.MyShop_API.Enum.PaymentStatus;
import com.example.MyShop_API.dto.request.OrderRequest;
import com.example.MyShop_API.dto.response.VnpayResponse;
import com.example.MyShop_API.entity.*;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.exception.ErrorCode;
import com.example.MyShop_API.repo.OrderRepository;
import com.example.MyShop_API.repo.PaymentRepository;
import com.example.MyShop_API.repo.ProductRepository;
import com.example.MyShop_API.repo.UserProfileRepository;
import com.example.MyShop_API.service.cart.ICartService;
import com.example.MyShop_API.service.coupon.ICouponService;
import com.example.MyShop_API.service.inventory.IInventoryService;
import com.example.MyShop_API.service.order_delivery_address.IOrderDeliveryAddressService;
import com.example.MyShop_API.service.order_status_history.IOrderStatusHistoryService;
import com.example.MyShop_API.service.payment.IPaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService implements IOrderService {
    OrderRepository orderRepository;
    ICartService cartService;
    ProductRepository productRepository;
    IInventoryService inventoryService;
    IPaymentService paymentService;
    UserProfileRepository profileRepository;
    ICouponService couponService;
    IOrderStatusHistoryService historyService;
    PaymentRepository paymentRepository;
    IOrderDeliveryAddressService deliveryAddressService;

    @Override
    public List<Order> getOrders() {
        return orderRepository.findAll().stream().toList();
    }

    @Override
    public Order getOrder(Long orderId) {
        log.info("getOrderById().........");
        return orderRepository.findById(orderId).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));
    }

    @Override
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByOrderStatus(status);
    }

    @Override
    public List<Order> getUserOrders(Long profileId) {
        return orderRepository.findByProfileProfile_id(profileId);
    }

    // placeOrder Buy Now -> create orderItem -> Payment
    // ===================== Buy Now =============================
    @Override
    @Transactional
    public Object buyNow(OrderRequest orderRequest, HttpServletRequest request) throws AppException {
        Product product = productRepository.findById(orderRequest.getProductId()).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        UserProfile profile = profileRepository.findById(orderRequest.getProfileId()).orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_EXISTED));

        // Check stock
        boolean reserved = inventoryService.reserveStock(product.getProductId(), orderRequest.getQuantity());
        if (!reserved) throw new AppException(ErrorCode.INVENTORY_NOT_ENOUGH);

        // create order + orderItem
        Order order = Order.builder()
                .profile(profile)
                .orderDate(LocalDate.now())
                .orderStatus(OrderStatus.PENDING)
                .build();

        OrderItem orderItem = OrderItem.builder()
                .product(product)
                .quantity(orderRequest.getQuantity())
                .order(order)
                .price(product.getSpecialPrice() != null ? product.getSpecialPrice() : product.getPrice())
                .build();

        order.setOrderItems(new HashSet<>(List.of(orderItem)));
        order.setTotalAmount(orderItem.getPrice()
                .multiply(BigDecimal.valueOf(orderItem.getQuantity())));

        // apply coupon if any
        if (orderRequest.getCouponCode() != null && !orderRequest.getCouponCode().isBlank()) {
            BigDecimal discount = couponService.applyCouponToOrder(
                    orderRequest.getCouponCode()
                    , order.getTotalAmount()
                    , order
                    , profile);

            log.info("discount: {}", discount);
            order.setTotalAmount(order.getTotalAmount().subtract(discount));
            BigDecimal newTotal = order.getTotalAmount();
            if (newTotal.compareTo(BigDecimal.ZERO) < 0) {
                newTotal = BigDecimal.ZERO;
            }
            order.setTotalAmount(newTotal);
        }


        Order savedOrder = orderRepository.save(order);

        // set address shipping
        OrderDeliveryAddress deliveryAddress = deliveryAddressService
                .createDeliveryAddressFromAddressId(orderRequest.getAddressId(), orderRequest.getProfileId(), orderRequest.getOrderNote());
        deliveryAddress.setOrder(savedOrder);
        order.setDeliveryAddress(deliveryAddress);


        // log audit status (system)
        historyService.logStatusChange(savedOrder, OrderStatus.PENDING, null);

        try {
            // payment
            return processPayment(savedOrder, orderRequest.getPaymentMethod(), request, orderRequest.getBankCode());
        } catch (AppException e) {
            inventoryService.restock(product.getProductId(), orderRequest.getQuantity());
            log.error("Error : {}", e.getMessage());
            throw e;
        }
    }

    // placeOrder Buy from Cart -> create OrderItem -> Order -> Payment
    // =================== PLACE ORDER FROM CART ======================
    @Override
    @Transactional
    public Object placeOrder(OrderRequest orderRequest, HttpServletRequest request) {
        log.info("placeOrder().........");

        // Đặt hàng
        Cart cart = cartService.getCartByUserProfileId(orderRequest.getProfileId());
        if (cart.getCartItems().isEmpty()) throw new AppException(ErrorCode.CART_NOT_EXISTED);

        // đặt hàng trước cho mỗi item
        cart.getCartItems().forEach(item -> {
            if (!inventoryService.reserveStock(item.getProduct().getProductId(), item.getQuantity())) {
                throw new AppException(ErrorCode.INVENTORY_NOT_ENOUGH);
            }
        });
        // Tạo đơn hàng
        Order order = createOrderFromCart(cart);
        Order saveOrder = orderRepository.save(order);

        // apply coupon if any
        if (orderRequest.getCouponCode() != null && !orderRequest.getCouponCode().isBlank()) {
            BigDecimal discount = couponService
                    .applyCouponToOrder(
                            orderRequest.getCouponCode().trim(),
                            saveOrder.getTotalAmount(),
                            saveOrder,
                            saveOrder.getProfile());
            saveOrder.setTotalAmount(saveOrder.getTotalAmount().subtract(discount));
            BigDecimal newTotal = saveOrder.getTotalAmount();
            if (newTotal.compareTo(BigDecimal.ZERO) < 0) {
                newTotal = BigDecimal.ZERO;
            }
            saveOrder.setTotalAmount(newTotal);
        }

        // set address shipping from address profile
        OrderDeliveryAddress deliveryAddress = deliveryAddressService.createDeliveryAddressFromAddressId(
                orderRequest.getAddressId(),
                orderRequest.getProfileId(),
                orderRequest.getOrderNote()
        );

        deliveryAddress.setOrder(saveOrder);
        saveOrder.setDeliveryAddress(deliveryAddress);

        saveOrder = orderRepository.save(saveOrder);
        try {
            // Process payment
            Object paymentResult = processPayment(saveOrder, orderRequest.getPaymentMethod(), request, orderRequest.getBankCode());

            if (orderRequest.getPaymentMethod() == PaymentMethod.CASH && paymentResult instanceof Order) {
                confirmOrderInventory(saveOrder);
                cartService.clearCart(cart.getCartId());
            }

            return paymentResult;
        } catch (Exception e) {
            // Hoàn hàng nếu có lỗi
            cart.getCartItems().forEach(item -> inventoryService.restock(item.getProduct().getProductId(), item.getQuantity()));
            log.error("Error : {}", e);
            throw new AppException(ErrorCode.PAYMENT_FAILED, e.getMessage());
        }
    }

    //================ Vnpay callback finalizer =====================
    @Transactional
    public VnpayResponse finalizeVnPayCallback(HttpServletRequest request) {
        // paymentService.handleVnPayCallback will create Payment record and attach to Order
        VnpayResponse resp = paymentService.handleVnPayCallback(request);

        String code = resp.getCode();
        Long orderId = null;
        try {
            orderId = Long.parseLong(request.getParameter("vnp_TxnRef"));
        } catch (Exception ignored) {
        }

        if (orderId == null) {
            // can't find order id -> just return response (no inventory op)
            return resp;
        }

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        if ("00".equals(code)) {
            // Payment success -> confirm reserved stock for all items
            order.getOrderItems().forEach(i -> inventoryService.confirmOrder(i.getProduct().getProductId(), i.getQuantity()));
            order.setOrderStatus(OrderStatus.PENDING);

            // Log system change status
            historyService.logStatusChange(order, OrderStatus.PENDING, null);
            // test
            order.getPayment().setPaymentStatus(PaymentStatus.PAID);
            orderRepository.save(order);
            return resp;
        } else {
            // Payment failed/declined -> release reservations
            order.getOrderItems().forEach(i -> inventoryService.restock(i.getProduct().getProductId(), i.getQuantity()));
            order.setOrderStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            return resp;
        }
    }

    // ================== CONFIRM COD ============================
    @Override
    public void confirmCashOrder(Long orderId, User admin) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        if (order.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new AppException(ErrorCode.ORDER_ALREADY_DELIVERED);
        }

        // Update order status when delivery is made
        order.setOrderStatus(OrderStatus.DELIVERED);
        historyService.logStatusChange(order, OrderStatus.DELIVERED, admin);
        Payment payment = order.getPayment();

        if (payment == null) throw new AppException(ErrorCode.PAYMENT_NOT_EXISTED);

        payment.setPaymentStatus(PaymentStatus.PAID);
        payment.setStatus("SUCCESS");
        payment.setPaymentDate(LocalDateTime.now());

        // Confirm inventory
        List<OrderItem> items = order.getOrderItems().stream().toList();
        if (items.isEmpty()) {
            throw new AppException(ErrorCode.ORDER_ITEM_EMPTY);
        }

        for (OrderItem item : items) {
            inventoryService.confirmOrder(
                    item.getProduct().getProductId(),
                    item.getQuantity()
            );
        }

        paymentRepository.save(payment);
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        // Tìm kiếm đơn hàng
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        if (order.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new AppException(ErrorCode.ORDER_CANCEL_FAILED);
        }

        // Order was paid/confirm restock
        if (order.getOrderStatus() == OrderStatus.SHIPPED || order.getOrderStatus() == OrderStatus.PENDING) {


            order.getOrderItems().forEach(orderItem -> {
                // restock
                inventoryService.restock(orderItem.getProduct().getProductId(), orderItem.getQuantity());
            });
        }

        // Update status order
        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    // ============= UPDATE STATUS ====================
    @Override
    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus, User admin) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        OrderStatus currentStatus = order.getOrderStatus();

        // NGĂN CHẶN CHUYỂN TRẠNG THÁI SAI LUẬT
        if (currentStatus == OrderStatus.DELIVERED) {
            throw new AppException(ErrorCode.ORDER_STATUS_FINAL);
        }

        if (currentStatus == OrderStatus.CANCELLED) {
            throw new AppException(ErrorCode.ORDER_ALREADY_CANCELLED);
        }
        order.setOrderStatus(newStatus);
        // audit status
        historyService.logStatusChange(order, newStatus, admin);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public void deleteOrder(Long orderId) {
        log.info("deleteOrder().........");
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        if (order.getOrderStatus() == OrderStatus.DELIVERED || order.getOrderStatus() == OrderStatus.SHIPPED) {
            throw new AppException(ErrorCode.ORDER_CANCEL_FAILED);
        }
        orderRepository.delete(order);
    }

    // =========== PROCESS PAYMENT ===============
    private Object processPayment(Order order, PaymentMethod method, HttpServletRequest request, Object... extraParams) {
        if (method == PaymentMethod.CASH) {
            boolean ok = paymentService.processCashPayment(order.getOrderId());
            if (!ok) throw new AppException(ErrorCode.PAYMENT_DECLINED);

            order.setOrderStatus(OrderStatus.PENDING);
            orderRepository.save(order);
            return order;
        }

        if (method == PaymentMethod.VNPAY) {
            return paymentService.createVnPayPayment(request, order.getOrderId(), (String) (extraParams.length > 0 ? extraParams[0] : null));
        }

        throw new AppException(ErrorCode.PAYMENT_METHOD_NOT_SUPPORT);
    }

    private Order createOrderFromCart(Cart cart) {
        Order order = Order.builder().profile(cart.getProfile()).orderDate(LocalDate.now()).orderStatus(OrderStatus.PENDING).build();

        List<OrderItem> orderItems = cart.getCartItems().stream().map(ci -> OrderItem.builder().order(order).product(ci.getProduct()).quantity(ci.getQuantity()).price(ci.getUnitPrice()).build()).collect(Collectors.toList());

        order.setOrderItems(new HashSet<>(orderItems));
        order.setTotalAmount(calculateTotalAmount(orderItems));

        return order;
    }

    private BigDecimal calculateTotalAmount(List<OrderItem> orderItems) {
        return orderItems.stream().map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void confirmOrderInventory(Order order) {
        order.getOrderItems().forEach(item -> inventoryService.confirmOrder(item.getProduct().getProductId(), item.getQuantity()));
    }
}
