package com.example.MyShop_API.controller;

import com.example.MyShop_API.dto.request.ChangePasswordRequest;
import com.example.MyShop_API.dto.request.UserCreationRequest;
import com.example.MyShop_API.dto.request.UserUpdateRequest;
import com.example.MyShop_API.dto.response.ApiResponse;
import com.example.MyShop_API.dto.response.UserResponse;
import com.example.MyShop_API.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
public class UserController {
    UserService userService;

    @GetMapping
    ApiResponse<List<UserResponse>> getUsers() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Username: {}", authentication.getName());
        authentication.getAuthorities().forEach(authority -> log.info(authority.getAuthority()));

        return ApiResponse.<List<UserResponse>>builder()
                .code(200)
                .data(userService.getUsers())
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<UserResponse> getUserById(@PathVariable Long id) {
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .data(userService.getUserById(id))
                .build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .data(userService.createUser(request))
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<UserResponse> updateUser(
            @Valid @RequestBody UserUpdateRequest request
            , @PathVariable Long id) {
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .data(userService.updateUser(request, id))
                .build();
    }

    @PutMapping("/{id}/change-password")
    ResponseEntity<Void> changePassword(
            @PathVariable(name = "id") Long id
            , @RequestBody @Valid ChangePasswordRequest request) {
        userService.changePassword(request, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    void deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
    }

    @GetMapping("/myInfo")
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .data(userService.getMyInfor())
                .build();
    }
}
