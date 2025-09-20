package com.example.MyShop_API.mapper;

import com.example.MyShop_API.dto.UserProfileRequest;
import com.example.MyShop_API.dto.UserProfileResponse;
import com.example.MyShop_API.entity.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {
    UserProfile toEntity(UserProfileRequest userProfileRequest);

    @Mapping(source = "address", target = "addressResponse")
    @Mapping(source = "cart", target = "cartResponse")
    UserProfileResponse toResponse(UserProfile userProfile);

    void UpdateUserProfile(UserProfileRequest userProfileRequest, @MappingTarget UserProfile userProfile);
}
