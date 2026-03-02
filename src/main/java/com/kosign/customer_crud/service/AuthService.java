package com.kosign.customer_crud.service;


import com.kosign.customer_crud.dto.request.AuthRequest;
import com.kosign.customer_crud.dto.response.ModelResponse.AuthResponse;
import jakarta.validation.Valid;

public interface AuthService {
    AuthResponse signInUser(@Valid AuthRequest authRequest);
}
