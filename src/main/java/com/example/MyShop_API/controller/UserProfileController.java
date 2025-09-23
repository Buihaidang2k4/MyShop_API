package com.example.MyShop_API.controller;

import com.example.MyShop_API.dto.ApiResponse;
import com.example.MyShop_API.dto.UserProfileRequest;
import com.example.MyShop_API.dto.UserProfileResponse;
import com.example.MyShop_API.service.UserProfileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user-profiles")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserProfileController {
    UserProfileService userProfileService;

    @GetMapping
    ApiResponse<List<UserProfileResponse>> getUserProfiles() {
        return ApiResponse.<List<UserProfileResponse>>builder()
                .code(200)
                .message("Success")
                .data(userProfileService.getUserProfile())
                .build();
    }

    @GetMapping("/{account_id}")
    ApiResponse<UserProfileResponse> getUserProfile(@PathVariable Long account_id) {
        return ApiResponse.<UserProfileResponse>builder()
                .code(200)
                .message("Success")
                .data(userProfileService.getUserProfileById(account_id))
                .build();
    }

    @PostMapping("/user/{userId}")
    ApiResponse<UserProfileResponse> addUserProfile(@RequestBody UserProfileRequest userProfileRequest, @PathVariable Long userId) {
        return ApiResponse.<UserProfileResponse>builder()
                .code(200)
                .message("Success")
                .data(userProfileService.createUserProfile(userId, userProfileRequest))
                .build();
    }

    @PutMapping("/{account_id}")
    ApiResponse<UserProfileResponse> updateUserProfile(@RequestBody UserProfileRequest userProfileRequest, @PathVariable Long account_id) {
        return ApiResponse.<UserProfileResponse>builder()
                .code(200)
                .message("Success")
                .data(userProfileService.updateUserProfile(account_id, userProfileRequest))
                .build();
    }

}
