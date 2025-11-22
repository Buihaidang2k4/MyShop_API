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
    // số nhà tên đường
    String street;
    // xa phuong
    String ward;
    // Quận huyện
    String district;
    // Tỉnh thành phố
    String province;
    // Mã bưu điện
    String postalCode = "1000";

    @Column(name = "additional_info")
    String additionalInfo;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "profile_id")
    UserProfile profile;
}