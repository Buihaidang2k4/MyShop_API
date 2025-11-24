package com.example.MyShop_API.service.address;

import com.example.MyShop_API.dto.request.AddressRequest;
import com.example.MyShop_API.dto.response.AddressResponse;
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
public class AddressService implements IAddressService {
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

    public AddressResponse createAddress(AddressRequest addressRequest, Long profileId) {
        UserProfile userProfile = userProfileRepository.findById(profileId).orElseThrow(() ->
                new AppException(ErrorCode.USER_NOT_EXISTED));

        Address address = addressMapper.toEntity(addressRequest);

        // Gan hai chieu
        address.setProfile(userProfile);
        userProfile.getAddress().add(address);

        // luu
        address = addressRepository.save(address);
        log.info("=============Creating address=========================");
        return addressMapper.toResponse(address);
    }

    public AddressResponse updateAddress(Long addressId, AddressRequest addressRequest) {
        Address findAddress = addressRepository.findById(addressId).orElseThrow(() ->
                new AppException(ErrorCode.ADDRESS_NOT_EXISTED));

        addressMapper.updateAddress(addressRequest, findAddress);

        try {
            findAddress = addressRepository.save(findAddress);
            log.info("=====================Updating address==============");
        } catch (AppException e) {
            new AppException(ErrorCode.ADDRESS_EXISTED);
        }
        return addressMapper.toResponse(findAddress);
    }

    public void deleteAddress(Long addressId, Long profileId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_EXISTED));

        if (!address.getProfile().getProfileId().equals(profileId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        addressRepository.delete(address);
        log.info("=====================Deleting address==============");
    }


}
