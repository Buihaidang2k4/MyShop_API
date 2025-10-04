package com.example.MyShop_API.service.userprofie;

import com.example.MyShop_API.dto.request.UserProfileRequest;
import com.example.MyShop_API.dto.response.UserProfileResponse;

import java.util.List;

public interface IUserProfileService {
    List<UserProfileResponse> getUserProfile();

    UserProfileResponse getUserProfileById(Long userProfileId);

    UserProfileResponse createUserProfile(Long userId, UserProfileRequest userProfileRequest);

    UserProfileResponse updateUserProfile(Long account_id, UserProfileRequest userProfileRequest);
}
