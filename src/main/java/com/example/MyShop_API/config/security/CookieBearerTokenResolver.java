package com.example.MyShop_API.config.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Conver bearer token -> cookies
 */
@RequiredArgsConstructor
public class CookieBearerTokenResolver implements BearerTokenResolver {
    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/api/v1/auth/login", "/api/v1/products/*", "/api/v1/categories/*"
    );

    private final String cookieName;

    @Override
    public String resolve(HttpServletRequest request) {
        String path = request.getRequestURI();
        // if public endpoint -> pass
        if (PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith)) {
            return null;
        }

        // get token from cookies
        if (request.getCookies() == null) return null;

        for (Cookie cookie : request.getCookies())
            if (cookieName.equals(cookie.getName()) && StringUtils.hasText(cookie.getValue())) return cookie.getValue();

        return null;
    }
}
