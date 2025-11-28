package com.example.MyShop_API.config.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * check if this api requires login (cookies)
 */
@RequiredArgsConstructor
public class CookieBearerTokenResolver implements BearerTokenResolver {
    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            // auth
            "/api/v1/auth/login",
            "/api/v1/users/registration",
            "/api/v1/auth/forgot-password",
            "/api/v1/auth/reset-password",

            // product
            "/api/v1/products/product/*",
            "/api/v1/products/product/by-product-name",
            "/api/v1/products/page",
            "/api/v1/products/category/by-category-name",
            "/api/v1/products/all",

            // categories
            "/api/v1/categories/**"
    );

    private final String cookieName;

    @Override
    public String resolve(HttpServletRequest request) {
        String path = request.getRequestURI();

        // public endpoint -> pass
        if (isPublicEndpoint(path)) return null;

        // get token from cookies
        if (request.getCookies() == null) return null;

        return Arrays.stream(request.getCookies())
                .filter(c -> cookieName.equals(c.getName()))
                .map(Cookie::getValue)
                .filter(StringUtils::hasText)
                .findFirst()
                .orElse(null);
    }

    private boolean isPublicEndpoint(String path) {
        return SecurityWhitelist.PUBLIC_ENDPOINTS.stream()
                .anyMatch(patten -> matchPatten(patten, path))
                || SecurityWhitelist.SWAGGER_ENDPOINTS.stream()
                .anyMatch(patten -> matchPatten(patten, path));
    }

    // /* /** , exact
    private boolean matchPatten(String pattern, String path) {
        if (pattern.endsWith("/**")) {
            return path.startsWith(pattern.substring(0, pattern.length() - 3));
        }

        if (pattern.endsWith("/*"))
            return path.startsWith(pattern.substring(0, pattern.length() - 2));
        return path.equals(pattern);
    }
}
