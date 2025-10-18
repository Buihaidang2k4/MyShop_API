package com.example.MyShop_API.controller;

import com.example.MyShop_API.dto.request.AddressRequest;
import com.example.MyShop_API.dto.response.AddressResponse;
import com.example.MyShop_API.dto.response.ApiResponse;
import com.example.MyShop_API.service.address.AddressService;
import com.example.MyShop_API.service.address.IAddressService;
import com.example.MyShop_API.service.authentication.IAuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/address")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AddressController {
    IAddressService addressService;

    @GetMapping("/all")
    ApiResponse<List<AddressResponse>> getAllAddresses() {
        return ApiResponse.<List<AddressResponse>>builder()
                .code(200)
                .message("Success")
                .data(addressService.getAllAddresses())
                .build();
    }

    @GetMapping("/{addressId}")
    ApiResponse<AddressResponse> getAddress(@PathVariable Long addressId) {
        return ApiResponse.<AddressResponse>builder()
                .code(200)
                .message("Success")
                .data(addressService.getAddressById(addressId))
                .build();
    }

    @PostMapping("/usersProfile/{userProfileId}/add")
    ApiResponse<AddressResponse> createAddress(@RequestBody AddressRequest addressRequest, @PathVariable Long userProfileId) {
        return ApiResponse.<AddressResponse>builder()
                .data(addressService.createAddress(addressRequest, userProfileId))
                .build();
    }

    @PutMapping("/{addressId}/update")
    ApiResponse<AddressResponse> updateAddress(@RequestBody AddressRequest addressRequest, @PathVariable Long addressId) {
        return ApiResponse.<AddressResponse>builder()
                .data(addressService.updateAddress(addressId, addressRequest))
                .build();
    }

    @DeleteMapping("/{addressId}/delete")
    ApiResponse<Void> deleteAddress(@PathVariable Long addressId) {
        addressService.deleteAddress(addressId);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Success")
                .build();
    }

}
