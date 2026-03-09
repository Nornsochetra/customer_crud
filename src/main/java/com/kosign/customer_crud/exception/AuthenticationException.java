package com.kosign.customer_crud.exception;

import com.kosign.customer_crud.dto.enumeration.AuthStatus;
import lombok.Getter;

@Getter
public class AuthenticationException extends RuntimeException {

    private final AuthStatus status;
    private final Object data;

    public AuthenticationException(AuthStatus status,String message){
        super(message);
        this.status = status;
        this.data = null;
    }

    public AuthenticationException(AuthStatus status,String message,Object data){
        super(message);
        this.status = status;
        this.data = data;
    }
}
