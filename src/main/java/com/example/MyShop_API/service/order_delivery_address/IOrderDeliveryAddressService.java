package com.example.MyShop_API.service.order_delivery_address;

import com.example.MyShop_API.entity.OrderDeliveryAddress;

public interface IOrderDeliveryAddressService {
    OrderDeliveryAddress createDeliveryAddressFromAddressId(Long addressId, Long profileId, String extraDeliveryNote);
}
