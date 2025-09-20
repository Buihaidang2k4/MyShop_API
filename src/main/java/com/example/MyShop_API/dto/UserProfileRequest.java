package com.example.MyShop_API.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data // Auto create getter setter constructor toString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileRequest {
    String firstName;
    String lastName;
    String mobileNumber;
}
