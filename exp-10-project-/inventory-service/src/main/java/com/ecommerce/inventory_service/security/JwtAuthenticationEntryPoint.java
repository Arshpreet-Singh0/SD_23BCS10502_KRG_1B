package com.ecommerce.inventory_service.security;

import com.ecommerce.inventory_service.advices.ApiError;
import com.ecommerce.inventory_service.advices.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        ApiError apiError = ApiError.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .message("Authentication required or JWT invalid")
                .build();

        ApiResponse<?> apiResponse = new ApiResponse<>(apiError);

        response.getWriter().write(
                new ObjectMapper().writeValueAsString(apiResponse)
        );
    }
}
