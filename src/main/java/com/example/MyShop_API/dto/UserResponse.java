package com.example.MyShop_API.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    Integer id;
    String username;
    String email;
    Set<RoleResponse> roles;
    UserProfileResponse userProfile;
}
