package com.kosign.customer_crud.config;

import com.kosign.customer_crud.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter {

    private final JwtService jwtService;

}
