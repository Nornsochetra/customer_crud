package com.kosign.customer_crud.exception.handler;

import com.kosign.customer_crud.dto.enumeration.AuthStatus;
import com.kosign.customer_crud.dto.model.exceptionModel.StatusInfo;
import com.kosign.customer_crud.dto.response.APIResponse.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        StatusInfo statusInfo = StatusInfo.builder()
                .code(String.valueOf(AuthStatus.OAUTH2_LOGIN_FAILED))
                .message("Unable to login with google")
                .build();
        ApiResponse<Void> errorResponse = ApiResponse.<Void>builder()
                .status(statusInfo)
                .data(null)
                .build();

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    }
}
