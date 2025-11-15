package com.example.MyShop_API.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressResponse {
    Long addressId;
    String street;
    String buildingName;
    String city;
    String state;
    String country;
    String pinCode;
//    Long profileId;
}
