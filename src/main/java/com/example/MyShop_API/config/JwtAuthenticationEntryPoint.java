package com.example.MyShop_API.config;

import com.example.MyShop_API.dto.ApiResponse;
import com.example.MyShop_API.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    //     Convert khi callapi sai không trả về login mà trả về JSON tùy chỉnh Custom
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;

        // set
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // tao apiresponse set code status
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessageTemplate())
                .build();

        // Convert sang JSOn
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writer().writeValueAsString(apiResponse));
        response.flushBuffer();
    }
}
