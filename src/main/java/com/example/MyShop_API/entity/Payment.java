package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long paymentId;

    @OneToOne(mappedBy = "payment", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    Order order;

    // Phương thức thanh toán (VD: "VNPAY", "COD", "MOMO", ...)
    @NotBlank
    @Size(min = 4, message = "Payment method must contain at least 4 characters")
    @Column(unique = true, nullable = false)
    String paymentMethod;

    // Mã giao dịch VNPay (vnp_TxnRef)
    @Column(unique = true)
    String vnpTxnRef;

    // Số tiền thanh toán (đơn vị: VNĐ)
    Long amount;

    // Nội dung thanh toán (vnp_OrderInfo)
    String orderInfo;

    // Mã ngân hàng (vnp_BankCode)
    String bankCode;

    // Mã phản hồi từ VNPay (vnp_ResponseCode)
    String responseCode;

    // Trạng thái giao dịch (PENDING, SUCCESS, FAILED)
    String status;

    // Thời gian thanh toán (vnp_PayDate)
    LocalDateTime paymentDate;
}
