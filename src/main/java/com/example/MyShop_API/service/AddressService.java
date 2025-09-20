package com.example.MyShop_API.service;

import com.example.MyShop_API.dto.AddressRequest;
import com.example.MyShop_API.dto.AddressResponse;
import com.example.MyShop_API.entity.Address;
import com.example.MyShop_API.entity.UserProfile;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.exception.ErrorCode;
import com.example.MyShop_API.mapper.AddressMapper;
import com.example.MyShop_API.repo.AddressRepository;
import com.example.MyShop_API.repo.UserProfileRepository;
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
public class AddressService {
    AddressRepository addressRepository;
    AddressMapper addressMapper;
    UserProfileRepository userProfileRepository;

    public List<AddressResponse> getAllAddresses() {
        return addressRepository.findAll().stream().map(addressMapper::toResponse).collect(Collectors.toList());
    }

    public AddressResponse getAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId).orElseThrow(() ->
                new AppException(ErrorCode.ADDRESS_NOT_EXISTED));

        return addressMapper.toResponse(address);
    }

    public AddressResponse createAddress(AddressRequest addressRequest, Long userProfileId) {
        UserProfile userProfile = userProfileRepository.findById(userProfileId).orElseThrow(() ->
                new AppException(ErrorCode.USER_NOT_EXISTED));

        log.info("Creating address");
        Address address = addressMapper.toEntity(addressRequest);
        // Gan hai chieu
        address.setUserProfiles(userProfile);
        userProfile.setAddress(address);

        // luu
        address = addressRepository.save(address);
        return addressMapper.toResponse(address);
    }

    public AddressResponse updateAddress(Long id, AddressRequest addressRequest) {
        log.info("Updating address");
        Address findAddress = addressRepository.findById(id).orElseThrow(() ->
                new AppException(ErrorCode.ADDRESS_NOT_EXISTED));

        addressMapper.updateAddress(addressRequest, findAddress);

        try {
            findAddress = addressRepository.save(findAddress);
        } catch (AppException e) {
            new AppException(ErrorCode.ADDRESS_EXISTED);
        }
        return addressMapper.toResponse(findAddress);
    }

    public void deleteAddress(Long addressId) {
        log.info("Deleting address");
        addressRepository.deleteById(addressId);
    }


}
