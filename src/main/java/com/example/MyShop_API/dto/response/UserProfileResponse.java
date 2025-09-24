package com.example.MyShop_API.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data // Auto create getter setter constructor toString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileResponse {
    Long account_id;
    String firstName;
    String lastName;
    String mobileNumber;
    AddressResponse addressResponse;
    CartResponse cartResponse;

}
