package com.kosign.customer_crud.dto.enumeration;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AuthStatus {
    LOGIN_SUCCESS(HttpStatus.OK),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED),
    ACCOUNT_LOCKED(HttpStatus.FORBIDDEN),
    ACCOUNT_INACTIVE(HttpStatus.FORBIDDEN),
    OAUTH2_LOGIN_FAILED(HttpStatus.UNAUTHORIZED);
    private final HttpStatus httpStatus;

    AuthStatus(HttpStatus httpStatus){
        this.httpStatus = httpStatus;
    }
}
