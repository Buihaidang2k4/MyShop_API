package com.example.MyShop_API.dto.response;


import com.example.MyShop_API.entity.Address;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileResponse {
    Long profileId;
    String username;
    String mobileNumber;
    Boolean gender;
    LocalDate birthDate;
    AddressResponse addressResponse;
    CartResponse cartResponse;
}
