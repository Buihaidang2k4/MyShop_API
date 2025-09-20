package com.example.MyShop_API.mapper;

import com.example.MyShop_API.dto.AddressRequest;
import com.example.MyShop_API.dto.AddressResponse;
import com.example.MyShop_API.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    Address toEntity(AddressRequest addressRequest);

    @Mapping(source = "userProfiles.account_id", target = "userProfileId")
    AddressResponse toResponse(Address address);

    void updateAddress(AddressRequest addressRequest, @MappingTarget Address address);
}
