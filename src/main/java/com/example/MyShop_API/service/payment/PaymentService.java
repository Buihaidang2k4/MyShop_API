package com.example.MyShop_API.service.payment;

import com.example.MyShop_API.Enum.OrderStatus;
import com.example.MyShop_API.Enum.PaymentMethod;
import com.example.MyShop_API.Enum.PaymentStatus;
import com.example.MyShop_API.config.payment.VnpayConfig;
import com.example.MyShop_API.dto.request.PaymentRequest;
import com.example.MyShop_API.dto.response.PaymentResponse;
import com.example.MyShop_API.dto.response.VnpayResponse;
import com.example.MyShop_API.entity.Order;
import com.example.MyShop_API.entity.OrderItem;
import com.example.MyShop_API.entity.Payment;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.exception.ErrorCode;
import com.example.MyShop_API.mapper.PaymentMapper;
import com.example.MyShop_API.repo.OrderRepository;
import com.example.MyShop_API.repo.PaymentRepository;
import com.example.MyShop_API.service.inventory.IInventoryService;
import com.example.MyShop_API.utils.VnpayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentService implements IPaymentService {
    VnpayConfig vnpayConfig;
    OrderRepository orderRepository;
    PaymentRepository paymentRepository;
    IInventoryService inventoryService;


    // ======================== COD =========================================
    @Override
    public boolean processCashPayment(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new AppException(ErrorCode.ORDER_NOT_EXISTED)
        );

        Payment payment = Payment.builder()
                .order(order)
                .status("PENDING")
                .paymentDate(LocalDateTime.now())
                .paymentMethod(PaymentMethod.CASH)
                .paymentStatus(PaymentStatus.UNPAID)
                .amount(order.getTotalAmount().longValue())
                .vnpTxnRef(order.getOrderId().toString())
                .build();

        paymentRepository.save(payment);
        order.setPayment(payment);
        orderRepository.save(order);
        return true;
    }


    // ================================== VnPay =======================================
    @Override
    public String createVnPayPayment(HttpServletRequest request, Long orderId, String bankCode) {
        long amount = getTotalAmountFromOrder(orderId) * 100L;

        Map<String, String> vnParamsMap = vnpayConfig.getVNPayConfig();
        vnParamsMap.put("vnp_Amount", String.valueOf(amount));
        if (bankCode != null && !bankCode.isEmpty()) {
            vnParamsMap.put("vnp_BankCode", bankCode);
        }
        vnParamsMap.put("vnp_IpAddr", VnpayUtil.getIpAddress(request));
        // set orderId - mã đơn hàng
        vnParamsMap.put("vnp_TxnRef", String.valueOf(orderId));

        // query url
        String queryUrl = VnpayUtil.getPaymentURL(vnParamsMap, true);
        String hashData = VnpayUtil.getPaymentURL(vnParamsMap, false);
        String vnpSecureHash = VnpayUtil.hmacSHA512(vnpayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnpayConfig.getVnp_PayUrl() + "?" + queryUrl;

        return paymentUrl;
    }

    // ============== response payment vnpay ================
    @Override
    public VnpayResponse handleVnPayCallback(HttpServletRequest request) {
        String responseCode = request.getParameter("vnp_ResponseCode");

        // Nếu thanh toán thành công
        if ("00".equals(responseCode)) {
            String vnpTxnRef = request.getParameter("vnp_TxnRef");
            Long orderId = Long.parseLong(request.getParameter("vnp_TxnRef"));
            Order order = orderRepository.findById(orderId).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));


            String transactionNo = request.getParameter("vnp_TransactionNo");
            String bankCode = request.getParameter("vnp_BankCode");
            String orderInfo = request.getParameter("vnp_OrderInfo");
            String amountStr = request.getParameter("vnp_Amount");
            String cardType = request.getParameter("vnp_CardType");

            // Số tiền trả về từ VNPay là *100 (VNPay quy định), nên chia lại
            Long amount = Long.parseLong(amountStr) / 100;

            //  Tạo mới bản ghi Payment
            Payment payment = Payment.builder()
                    .paymentMethod(PaymentMethod.VNPAY)
                    .paymentStatus(PaymentStatus.PAID)
                    .vnpTxnRef(vnpTxnRef)
                    .transactionNo(transactionNo)
                    .bankCode(bankCode)
                    .cardType(cardType)
                    .orderInfo(orderInfo)
                    .amount(amount)
                    .responseCode(responseCode)
                    .status("SUCCESS")
                    .paymentDate(LocalDateTime.now())
                    .order(order)
                    .build();

            paymentRepository.save(payment);

            // Luu thanh toan vao dơn hang
            order.setPayment(payment);
            orderRepository.save(order);
            return new VnpayResponse(
                    "00",
                    "Payment order: " + vnpTxnRef + " Success",
                    vnpTxnRef,
                    orderInfo,
                    PaymentStatus.PAID,
                    PaymentMethod.VNPAY
            );
        }
        // Nếu thanh toán thất bại
        return new VnpayResponse(responseCode,
                "Payment Failed",
                request.getParameter("vnp_TxnRef"),
                request.getParameter("vnp_OrderInfo"),
                PaymentStatus.UNPAID,
                PaymentMethod.VNPAY);
    }

    private long getTotalAmountFromOrder(long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));
        BigDecimal totalAmount = order.getTotalAmount();

        long rawAmount = totalAmount.setScale(2, BigDecimal.ROUND_HALF_UP).longValue();

        // =============== GIOI HAN SANDBOX ===============
        if (vnpayConfig.getVnp_PayUrl().contains("sandbox")) {
            if (rawAmount > 2_000_000) {
                rawAmount = 2_000_000; // lớn hơn 2tr → cố định 2tr
                log.warn("VNPAY SANDBOX: Amount capped to 2.000.000đ for testing");
            }
        }

        return rawAmount * 100L;
    }


}
