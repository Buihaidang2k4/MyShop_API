package com.example.MyShop_API.service.address;

import com.example.MyShop_API.dto.request.AddressRequest;
import com.example.MyShop_API.dto.response.AddressResponse;

import java.util.List;

public interface IAddressService {
    List<AddressResponse> getAllAddresses();

    AddressResponse getAddressById(Long addressId);

    AddressResponse createAddress(AddressRequest addressRequest, Long userProfileId);

    AddressResponse updateAddress(Long id, AddressRequest addressRequest);

    void deleteAddress(Long addressId);
}
