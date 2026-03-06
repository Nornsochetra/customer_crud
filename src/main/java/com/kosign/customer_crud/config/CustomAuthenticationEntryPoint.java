package com.kosign.customer_crud.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kosign.customer_crud.dto.model.exceptionModel.StatusInfo;
import com.kosign.customer_crud.dto.response.APIResponse.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // login endpoint - let it pass through to GlobalExceptionHandler
        if (request.getRequestURI().contains("/api/v1/auth/")) {
            throw authException; // rethrow so GlobalExceptionHandler catches it
        }

        // protected endpoints → no token
        StatusInfo statusInfo = StatusInfo.builder()
                .code("UNAUTHORIZED")
                .message("Access denied. Authorization: Bearer <accessToken> is required.")
                .build();


        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .status(statusInfo)
                .data(null)
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}