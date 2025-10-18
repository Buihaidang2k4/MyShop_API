package com.example.MyShop_API.mapper;

import com.example.MyShop_API.dto.request.RoleRequest;
import com.example.MyShop_API.dto.response.RoleResponse;
import com.example.MyShop_API.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleResponse toResponse(Role role);

    Role toEntity(RoleRequest roleRequest);

    void updateRole(RoleRequest request, @MappingTarget Role role);
}
