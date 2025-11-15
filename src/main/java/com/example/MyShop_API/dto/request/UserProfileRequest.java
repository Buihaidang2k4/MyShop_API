package com.example.MyShop_API.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data // Auto create getter setter constructor toString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileRequest {
    @Size(min = 5, max = 20, message = "First Name must be between 5 and 30 characters long")
    String firstName;

    @Size(min = 5, max = 20, message = "Last Name must be between 5 and 30 characters long")
    String lastName;

    @Size(min = 10, max = 10, message = "Mobile Number must be exactly 10 digits long")
    @Pattern(regexp = "^\\d{10}$", message = "Mobile Number must contain only Numbers")
    String mobileNumber;
    Boolean gender;
    LocalDate birthDate;
}
