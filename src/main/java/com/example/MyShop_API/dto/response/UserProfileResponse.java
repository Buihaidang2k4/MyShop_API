package com.example.MyShop_API.dto.response;


import com.example.MyShop_API.entity.Address;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data // Auto create getter setter constructor toString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileResponse {
    Long profile_id;
    String firstName;
    String lastName;
    String mobileNumber;
    Address addressResponse;
    CartResponse cartResponse;
}
