package com.example.MyShop_API.controller;

import com.example.MyShop_API.dto.request.AuthenticationRequest;
import com.example.MyShop_API.dto.request.IntrosprectRequest;
import com.example.MyShop_API.dto.response.ApiResponse;
import com.example.MyShop_API.dto.response.AuthenticationResponse;
import com.example.MyShop_API.dto.response.IntrospectResponse;
import com.example.MyShop_API.service.authentication.AuthenticationService;
import com.example.MyShop_API.service.authentication.IAuthenticationService;
import com.example.MyShop_API.service.token_blacklist.TokenBlacklistService;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("${api.prefix}/auth")
public class AuthenticationController {
    private final static String REFRESH_COOKIES_NAME = "refresh_token";
    private final static Duration REFRESH_COOKIES_EXPIRATION = Duration.ofDays(7);
    IAuthenticationService authenticationService;

    @PostMapping("/login")
    ResponseEntity<String> login(@RequestBody AuthenticationRequest request,
                                 HttpServletResponse response
    ) {
        try {
            AuthenticationResponse tokens = authenticationService.authenticate(request);
            ResponseCookie cookie = authenticationService.buildRefreshCookie(tokens.getRefreshToken());
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            return ResponseEntity.ok(tokens.getAccessToken());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Credentials " + e.getMessage());
        }
    }

    @PostMapping("/refresh")
    ResponseEntity<?> refreshToken(HttpServletRequest request) throws ParseException, JOSEException {
        String refreshToken = authenticationService.getRefreshTokenFromRequest(request);

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token not found in cookies");
        }

        String newAccessToken = authenticationService.refreshToken(refreshToken);
        return ResponseEntity.ok(newAccessToken);

    }

    @PostMapping("/introspect")
    ResponseEntity<IntrospectResponse> introspect(
            @CookieValue(name = REFRESH_COOKIES_NAME, required = false) String refreshToken
    ) {

        if (refreshToken == null) {
            return ResponseEntity.ok(IntrospectResponse.builder().valid(false).build());
        }

        try {
            IntrospectResponse response = authenticationService.introspect(new IntrosprectRequest(refreshToken));
            return ResponseEntity.ok(response);
        } catch (ParseException | JOSEException e) {
            return ResponseEntity.ok(IntrospectResponse.builder()
                    .valid(false)
                    .build());
        }
    }

    @PostMapping("/logout")
    ResponseEntity<ApiResponse> logout(HttpServletRequest request, HttpServletResponse response) throws ParseException, JOSEException {
        String refreshToken = authenticationService.getRefreshTokenFromRequest(request);

        if (refreshToken != null && !refreshToken.isBlank()) {
            try {
                authenticationService.logout(refreshToken);
            } catch (ParseException | JOSEException e) {
                throw new RuntimeException("Error logout: " + e);
            }
        }

        ResponseCookie responseCookie = authenticationService.logoutAndGetCookie(refreshToken);
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());

        return ResponseEntity.ok(new ApiResponse(200, "Logout successful", null));
    }


}
