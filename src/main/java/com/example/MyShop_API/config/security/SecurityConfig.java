package com.example.MyShop_API.config.security;

import com.example.MyShop_API.anotation.AdminOnly;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Allow or block requests
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SecurityConfig {
    private static final String[] PUBLIC_ENDPOINT = {
            // Auth
            "/api/v1/auth/**", "/api/v1/auth/google",
            "/api/v1/users/registration",
            "/api/v1/auth/forgot-password",
            "/api/v1/auth/reset-password",

            // categories
            "/api/v1/categories/**",

            // product
            "/api/v1/products/product/*",
            "/api/v1/products/product/by-product-name",
            "/api/v1/products/page",
            "/api/v1/products/category/by-category-name",
            "/api/v1/products/product/{slug}/slug",
            "/api/v1/products/all",

            // Image
            "/api/v1/images/**",
    };
    private static final String ACCESS_TOKEN_NAME = "access_token";

    @Autowired
    CustomJwtDecoder customJwtDecoder;

    @Bean
    public SecurityFilterChain securityWebFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .httpBasic(httpBasicAuth -> httpBasicAuth.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(SecurityWhitelist.PUBLIC_ENDPOINTS.toArray(String[]::new)).permitAll()
                        .requestMatchers(SecurityWhitelist.SWAGGER_ENDPOINTS.toArray(new String[0])).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth -> oauth
                        .bearerTokenResolver(new CookieBearerTokenResolver(ACCESS_TOKEN_NAME))
                        .jwt(jwtConfigurer -> jwtConfigurer
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                                .decoder(customJwtDecoder))
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                );

        return http.build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000", "http://127.0.0.1:5173", "http://127.0.0.1:3000")); // frontend Vite
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
