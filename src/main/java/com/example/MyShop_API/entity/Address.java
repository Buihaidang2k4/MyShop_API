package com.example.MyShop_API.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    Long addressId;
    String buildingName;
    String street;
    String city;
    String state;
    String country;

    // Mã bưu điện
    @Column(name = "pin_code", nullable = true)
    String pinCode = "1000";

    @OneToOne(mappedBy = "address")
    @JsonIgnore
    UserProfile userProfile;
}