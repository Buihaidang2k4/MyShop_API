package com.example.MyShop_API.controller;

import com.example.MyShop_API.anotation.AllAccess;
import com.example.MyShop_API.dto.request.AuthenticationRequest;
import com.example.MyShop_API.dto.request.IntrospectRequest;
import com.example.MyShop_API.dto.response.ApiResponse;
import com.example.MyShop_API.dto.response.AuthenticationResponse;
import com.example.MyShop_API.dto.response.IntrospectResponse;
import com.example.MyShop_API.service.authentication.IAuthenticationService;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;

@Slf4j
@RequiredArgsConstructor
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("${api.prefix}/auth")
public class AuthenticationController {
    private final static String REFRESH_COOKIE_NAME = "refresh_token";
    private static final String ACCESS_COOKIE_NAME = "access_token";

    IAuthenticationService authenticationService;

    @PostMapping("/login")
    ResponseEntity<ApiResponse> login(@Valid @RequestBody AuthenticationRequest request,
                                      HttpServletResponse response) {
        log.info("Login");
        AuthenticationResponse tokens = authenticationService.authenticate(request);
        ResponseCookie cookieAccess = authenticationService.buildCookie(tokens.getAccessToken(), ACCESS_COOKIE_NAME);
        ResponseCookie cookieRefresh = authenticationService.buildCookie(tokens.getRefreshToken(), REFRESH_COOKIE_NAME);

        response.addHeader(HttpHeaders.SET_COOKIE, cookieAccess.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, cookieRefresh.toString());

        return ResponseEntity.ok(new ApiResponse(200, "Login jwt successful", null));
    }

    @AllAccess
    @PostMapping("/google")
    ResponseEntity<ApiResponse> handleGoogleLogin(@RequestBody IntrospectRequest request
            , HttpServletResponse response
    ) throws GeneralSecurityException, IOException {

        AuthenticationResponse tokens = authenticationService.authenticateGoogle(request);

        ResponseCookie cookieAccess = authenticationService.buildCookie(tokens.getAccessToken(), ACCESS_COOKIE_NAME);
        ResponseCookie cookieRefresh = authenticationService.buildCookie(tokens.getRefreshToken(), REFRESH_COOKIE_NAME);


        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieAccess.toString())
                .header(HttpHeaders.SET_COOKIE, cookieRefresh.toString())
                .body(new ApiResponse(200, "Login jwt successful", null));
    }

    @PostMapping("/refresh")
    ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) throws ParseException, JOSEException {
        log.info("Refresh Token");

        String refreshToken = authenticationService.getTokenFromCookie(request, REFRESH_COOKIE_NAME);
        log.info("Refresh token raw: {}", refreshToken);
        if (refreshToken == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No refresh token found");

        String newAccessToken = authenticationService.refreshToken(refreshToken);
        ResponseCookie newAccessCookie = authenticationService.buildCookie(newAccessToken, ACCESS_COOKIE_NAME);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, newAccessCookie.toString())
                .body(new ApiResponse(200, "Access token refreshed", null));
    }

    @PostMapping("/introspect")
    ResponseEntity<IntrospectResponse> introspect(HttpServletRequest request
    ) throws ParseException, JOSEException {
        String refreshToken = authenticationService.getTokenFromCookie(request, ACCESS_COOKIE_NAME);

        if (refreshToken == null) {
            return ResponseEntity.ok(IntrospectResponse.builder().valid(false).build());
        }

        IntrospectResponse response = authenticationService.introspect(IntrospectRequest.builder().token(refreshToken).build());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    ResponseEntity<ApiResponse> logout(HttpServletRequest request, HttpServletResponse response) throws ParseException, JOSEException {
        log.info("Logout");
        String refreshToken = authenticationService.getTokenFromCookie(request, REFRESH_COOKIE_NAME);

        if (refreshToken != null && !refreshToken.isBlank()) {
            try {
                authenticationService.logout(refreshToken);
            } catch (ParseException | JOSEException e) {
                throw new RuntimeException("Error logout: " + e);
            }
        }

        ResponseCookie clearAccessToken = authenticationService.clearCookie(ACCESS_COOKIE_NAME);
        ResponseCookie clearRefreshToken = authenticationService.clearCookie(REFRESH_COOKIE_NAME);

        response.addHeader(HttpHeaders.SET_COOKIE, clearAccessToken.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, clearRefreshToken.toString());

        return ResponseEntity.ok(new ApiResponse(200, "Logout successful", null));
    }
}
