package com.example.MyShop_API.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class VnpayResponse {
    public String code;
    public String message;
    public String paymentUrl;
}
