package com.example.MyShop_API.service.order_delivery_address;

import com.example.MyShop_API.entity.Address;
import com.example.MyShop_API.entity.OrderDeliveryAddress;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.exception.ErrorCode;
import com.example.MyShop_API.repo.AddressRepository;
import io.netty.util.internal.StringUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderDeliveryAddressService implements IOrderDeliveryAddressService {
    AddressRepository addressRepository;

    @Override
    public OrderDeliveryAddress createDeliveryAddressFromAddressId(Long addressId, Long profileId, String extraDeliveryNote) {
        Address address = addressRepository.findById(addressId).orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_EXISTED));


        return OrderDeliveryAddress.builder()
                .createdAt(LocalDateTime.now())
                .recipientName(address.getFullName())
                .recipientPhone(address.getPhone())
                .street(address.getStreet())
                .ward(address.getWard())
                .district(address.getDistrict())
                .province(address.getProvince())
                .postalCode(address.getPostalCode())
                .deliveryNote(
                        StringUtils.hasText(extraDeliveryNote)
                                ? extraDeliveryNote
                                : address.getAdditionalInfo()
                )
                .build();
    }
}
