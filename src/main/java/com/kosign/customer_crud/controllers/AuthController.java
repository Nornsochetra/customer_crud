package com.kosign.customer_crud.controllers;

import com.kosign.customer_crud.dto.model.exceptionModel.StatusInfo;
import com.kosign.customer_crud.dto.request.AuthRequest;
import com.kosign.customer_crud.dto.response.APIResponse.ApiResponse;
import com.kosign.customer_crud.dto.response.ModelResponse.AuthResponse;
import com.kosign.customer_crud.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
                .code("LOGIN_SUCCESS")
                .message("Login successful.")
                .build();

        ApiResponse<AuthResponse> response = ApiResponse.<AuthResponse>builder()
                .status(statusInfo)
                .data(authResponse)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
