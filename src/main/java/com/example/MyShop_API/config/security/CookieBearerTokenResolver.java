package com.example.MyShop_API.config.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.util.StringUtils;

/**
 * Conver bearer token -> cookies
 */
@RequiredArgsConstructor
public class CookieBearerTokenResolver implements BearerTokenResolver {
    private final String cookieName;

    @Override
    public String resolve(HttpServletRequest request) {
        // get token from cookies
        if (request.getCookies() == null) return null;

        for (Cookie cookie : request.getCookies())
            if (cookieName.equals(cookie.getName()) && StringUtils.hasText(cookie.getValue())) return cookie.getValue();

        return null;
    }
}
