package com.example.MyShop_API.service.user;

import com.example.MyShop_API.dto.request.ChangePasswordRequest;
import com.example.MyShop_API.dto.request.UserCreationRequest;
import com.example.MyShop_API.dto.request.UserUpdateRequest;
import com.example.MyShop_API.dto.response.UserResponse;

import java.util.List;

public interface IUserService {
    List<UserResponse> getUsers();

    UserResponse getUserById(Long id);

    UserResponse createUser(UserCreationRequest request);

    void changePassword(ChangePasswordRequest request, Long id);

    UserResponse updateUser(UserUpdateRequest request, Long id);

    void deleteUserById(Long id);

    UserResponse getMyInfor();
}
