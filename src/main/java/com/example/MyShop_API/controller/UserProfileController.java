package com.example.MyShop_API.controller;

import com.example.MyShop_API.dto.response.ApiResponse;
import com.example.MyShop_API.dto.request.UserProfileRequest;
import com.example.MyShop_API.dto.response.UserProfileResponse;
import com.example.MyShop_API.service.userprofie.IUserProfileService;
import com.example.MyShop_API.service.userprofie.UserProfileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/user-profiles")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserProfileController {
    IUserProfileService userProfileService;

    @GetMapping("/all")
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

    @PostMapping("/user/{userId}/addUserProfile")
    ApiResponse<UserProfileResponse> addUserProfile(@RequestBody UserProfileRequest userProfileRequest, @PathVariable Long userId) {
        return ApiResponse.<UserProfileResponse>builder()
                .code(200)
                .message("Success")
                .data(userProfileService.createUserProfile(userId, userProfileRequest))
                .build();
    }

    @PutMapping("/{account_id}/update")
    ApiResponse<UserProfileResponse> updateUserProfile(@RequestBody UserProfileRequest userProfileRequest, @PathVariable Long account_id) {
        return ApiResponse.<UserProfileResponse>builder()
                .code(200)
                .message("Success")
                .data(userProfileService.updateUserProfile(account_id, userProfileRequest))
                .build();
    }

}
