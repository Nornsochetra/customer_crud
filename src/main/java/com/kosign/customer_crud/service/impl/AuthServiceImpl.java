package com.kosign.customer_crud.service.impl;

import com.kosign.customer_crud.dto.request.AuthRequest;
import com.kosign.customer_crud.dto.response.ModelResponse.AuthResponse;
import com.kosign.customer_crud.repository.CustomerRepository;
import com.kosign.customer_crud.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final CustomerRepository customerRepository;

    @Override
    public AuthResponse signInUser(AuthRequest authRequest) {
        return null;
    }
}
