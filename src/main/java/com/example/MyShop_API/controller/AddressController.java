package com.example.MyShop_API.controller;

import com.example.MyShop_API.dto.request.AddressRequest;
import com.example.MyShop_API.dto.response.AddressResponse;
import com.example.MyShop_API.dto.response.ApiResponse;
import com.example.MyShop_API.service.AddressService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AddressController {
    AddressService addressService;

    @GetMapping
    ApiResponse<List<AddressResponse>> getAllAddresses() {
        return ApiResponse.<List<AddressResponse>>builder()
                .code(200)
                .message("Success")
                .data(addressService.getAllAddresses())
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<AddressResponse> getAddress(@PathVariable Long id) {
        return ApiResponse.<AddressResponse>builder()
                .code(200)
                .message("Success")
                .data(addressService.getAddressById(id))
                .build();
    }

    @PostMapping("/usersProfile/{userProfileId}")
    ApiResponse<AddressResponse> createAddress(@RequestBody AddressRequest addressRequest, @PathVariable Long userProfileId) {
        return ApiResponse.<AddressResponse>builder()
                .data(addressService.createAddress(addressRequest, userProfileId))
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<AddressResponse> updateAddress(@RequestBody AddressRequest addressRequest, @PathVariable Long id) {
        return ApiResponse.<AddressResponse>builder()
                .data(addressService.updateAddress(id, addressRequest))
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<Void> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Success")
                .build();
    }

}
