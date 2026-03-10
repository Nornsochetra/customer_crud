package com.kosign.customer_crud.exception.handler;

import com.kosign.customer_crud.dto.model.CustomerModel;
import com.kosign.customer_crud.dto.model.CustomerOAuth2User;
import com.kosign.customer_crud.dto.model.UserInfo;
import com.kosign.customer_crud.dto.response.ModelResponse.AuthResponse;
import com.kosign.customer_crud.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;

    @Value("${jwt.expiration}")
    private String expireIn;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomerOAuth2User oAuth2User = (CustomerOAuth2User) authentication.getPrincipal();
        CustomerModel customerModel = oAuth2User.getCustomerModel();

        String token = jwtService.generateToken(customerModel.getUsername());

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(Long.valueOf(expireIn))
                .userInfo(UserInfo.builder()
                        .customerId(customerModel.getCustomerId())
                        .username(customerModel.getUsername())
                        .roles(customerModel.toUserInfo().getRoles())
                        .build()
                )
                .build();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("""
                {
                  "accessToken": "%s",
                  "tokenType": "%s",
                  "expiresIn": %d,
                  "data": {
                    "customerId": %d,
                    "userName": "%s",
                    "roles": %s
                  }
                }
                """.formatted(
                authResponse.getAccessToken(),
                authResponse.getTokenType(),
                authResponse.getExpiresIn(),
                authResponse.getUserInfo().getCustomerId(),
                authResponse.getUserInfo().getUsername(),
                authResponse.getUserInfo().getRoles().toString()
        ));
    }
}
