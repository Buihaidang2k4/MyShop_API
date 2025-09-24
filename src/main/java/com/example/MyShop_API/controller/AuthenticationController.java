package com.example.MyShop_API.controller;

import com.example.MyShop_API.dto.request.AuthenticationRequest;
import com.example.MyShop_API.dto.request.IntrosprectRequest;
import com.example.MyShop_API.dto.response.ApiResponse;
import com.example.MyShop_API.dto.response.AuthenticationResponse;
import com.example.MyShop_API.dto.response.IntrospectResponse;
import com.example.MyShop_API.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@Slf4j
@RequiredArgsConstructor
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/auth")
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/token")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest) {
        var results = authenticationService.authenticate(authenticationRequest);

        return ApiResponse.<AuthenticationResponse>builder()
                .code(1000)
                .data(results)
                .build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> introspect(@RequestBody IntrosprectRequest request) throws ParseException, JOSEException {
        var results = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .code(1000)
                .data(results)
                .build();
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody IntrosprectRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);
        log.info("Logout ");
        return ApiResponse.<Void>builder()
                .code(1000)
                .build();
    }

    @PostMapping("/refresh")
    ApiResponse<AuthenticationResponse> refreshToken(@RequestBody IntrosprectRequest request) throws ParseException, JOSEException {
        return ApiResponse.<AuthenticationResponse>builder()
                .code(1000)
                .data(authenticationService.refreshToken(request))
                .build();
    }
}
