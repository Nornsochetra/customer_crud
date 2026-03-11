package com.kosign.customer_crud.controllers;

import com.kosign.customer_crud.dto.enumeration.AuthStatus;
import com.kosign.customer_crud.dto.model.exceptionModel.StatusInfo;
import com.kosign.customer_crud.dto.request.AuthRequest;
import com.kosign.customer_crud.dto.response.APIResponse.ApiResponse;
import com.kosign.customer_crud.dto.response.ModelResponse.AuthResponse;
import com.kosign.customer_crud.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final CustomerService customerService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> loginUser(
            @RequestBody @Valid AuthRequest authRequest
    ){
        AuthResponse authResponse = customerService.signInUser(authRequest);

        StatusInfo statusInfo = StatusInfo.builder()
                .code(String.valueOf(AuthStatus.LOGIN_SUCCESS))
                .message("Login successful.")
                .build();

        ApiResponse<AuthResponse> response = ApiResponse.<AuthResponse>builder()
                .status(statusInfo)
                .data(authResponse)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/google")
    public void redirectToGoogle(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google");
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@RequestParam String refreshToken){
        AuthResponse authResponse = customerService.refreshToken(refreshToken);

        StatusInfo statusInfo = StatusInfo.builder()
                .code(String.valueOf(AuthStatus.REFRESH_SUCCESS))
                .message("Refresh token successful.")
                .build();

        ApiResponse<AuthResponse> response = ApiResponse.<AuthResponse>builder()
                .status(statusInfo)
                .data(authResponse)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String refreshToken
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.<Void>builder()
                            .status(StatusInfo.builder()
                                    .code(String.valueOf(AuthStatus.INVALID_CREDENTIALS))
                                    .message("Missing or invalid Authorization header.")
                                    .build())
                            .build());
        }

        String accessToken = authHeader.substring(7);
        customerService.logout(accessToken, refreshToken);

        StatusInfo statusInfo = StatusInfo.builder()
                .code(String.valueOf(AuthStatus.LOGOUT_SUCCESS))
                .message("Logout successful.")
                .build();

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(statusInfo)
                .build());
    }

}
