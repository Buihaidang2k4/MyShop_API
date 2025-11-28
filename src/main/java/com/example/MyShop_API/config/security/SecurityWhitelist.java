package com.example.MyShop_API.config.security;

import java.util.List;

public final class SecurityWhitelist {
    public static final List<String> PUBLIC_ENDPOINTS = List.of(
            // Auth
            "/api/v1/auth/**",
            "/api/v1/auth/google",
            "/api/v1/users/registration",
            "/api/v1/auth/forgot-password",
            "/api/v1/auth/reset-password",

            // categories
            "/api/v1/categories/**",

            // product
            "/api/v1/products/product/by-product-name",
            "/api/v1/products/page",
            "/api/v1/products/category/by-category-name",
            "/api/v1/products/all",

            // image
            "/api/v1/images/**"
    );
    // swagger
    public static final List<String> SWAGGER_ENDPOINTS = List.of(
            "/swagger-ui/**", "/v3/api-docs/**",
            "/swagger-resources/**", "/webjars/**"
    );


    private SecurityWhitelist() {
    }
}
