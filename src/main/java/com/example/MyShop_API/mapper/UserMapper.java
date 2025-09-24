package com.example.MyShop_API.mapper;

import com.example.MyShop_API.dto.request.UserCreationRequest;
import com.example.MyShop_API.dto.request.UserUpdateRequest;
import com.example.MyShop_API.dto.response.UserResponse;
import com.example.MyShop_API.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    User toEntity(UserCreationRequest request);

    UserResponse toResponse(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    void update(UserUpdateRequest request, @MappingTarget User user);
}
