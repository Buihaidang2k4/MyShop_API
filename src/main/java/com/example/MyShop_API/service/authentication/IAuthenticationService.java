package com.example.MyShop_API.service.authentication;

import com.example.MyShop_API.dto.request.AuthenticationRequest;
import com.example.MyShop_API.dto.request.IntrosprectRequest;
import com.example.MyShop_API.dto.response.AuthenticationResponse;
import com.example.MyShop_API.dto.response.IntrospectResponse;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;

import java.text.ParseException;

public interface IAuthenticationService {

    AuthenticationResponse authenticate(AuthenticationRequest request);

    IntrospectResponse introspect(IntrosprectRequest request) throws ParseException, JOSEException;

    void logout(String refreshToken) throws ParseException, JOSEException;

    String refreshToken(String refreshToken) throws ParseException, JOSEException;

    ResponseCookie buildRefreshCookie(String refreshToken);

    ResponseCookie logoutAndGetCookie(String refreshToken);

    String getRefreshTokenFromRequest(HttpServletRequest request);
}
