package com.example.MyShop_API.service;

import com.example.MyShop_API.dto.request.PaymentRequest;
import com.example.MyShop_API.dto.response.PaymentResponse;
import com.example.MyShop_API.entity.Payment;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.exception.ErrorCode;
import com.example.MyShop_API.mapper.PaymentMapper;
import com.example.MyShop_API.repo.PaymentRepository;
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
public class PaymentService {
    PaymentRepository paymentRepository;
    PaymentMapper paymentMapper;


    public List<PaymentResponse> getPayment() {
        return paymentRepository.findAll().stream().map(paymentMapper::toResponse).collect(Collectors.toList());
    }

    public PaymentResponse getPaymentById(Long paymentId) {
        Payment findpaPayment = paymentRepository.findById(paymentId).orElseThrow(() ->
                new AppException(ErrorCode.PAYMENT_NOT_EXISTED));

        if (paymentRepository.existsByPaymentMethod(findpaPayment.getPaymentMethod())) {
            throw new AppException(ErrorCode.PAYMENT_EXISTED);
        }

        return paymentMapper.toResponse(findpaPayment);
    }

    public PaymentResponse createPayment(PaymentRequest paymentRequest) {
        Payment payment = paymentMapper.toEntity(paymentRequest);

        if (paymentRepository.existsByPaymentMethod(payment.getPaymentMethod())) {
            throw new AppException(ErrorCode.PAYMENT_EXISTED);
        }

        return paymentMapper.toResponse(paymentRepository.save(payment));
    }

    public PaymentResponse updatePayment(Long paymentId, PaymentRequest paymentRequest) {
        Payment findPayment = paymentRepository.findById(paymentId).orElseThrow(() ->
                new AppException(ErrorCode.PAYMENT_NOT_EXISTED));

        if (paymentRepository.existsByPaymentMethod(findPayment.getPaymentMethod())) {
            throw new AppException(ErrorCode.PAYMENT_EXISTED);
        }
        paymentMapper.update(paymentRequest, findPayment);
        findPayment = paymentRepository.save(findPayment);

        return paymentMapper.toResponse(findPayment);
    }

    public void deletePayment(Long paymentId) {
        Payment findPayment = paymentRepository.findById(paymentId).orElseThrow(() ->
                new AppException(ErrorCode.PAYMENT_NOT_EXISTED));

        paymentRepository.delete(findPayment);
    }
}
