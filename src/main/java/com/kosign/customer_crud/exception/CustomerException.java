package com.kosign.customer_crud.exception;

import com.kosign.customer_crud.dto.enumeration.CustomerStatus;
import lombok.Getter;

@Getter
public class CustomerException extends RuntimeException {

    private final CustomerStatus status;
    private final Object data;

    public CustomerException(CustomerStatus status, String message) {
        super(message);
        this.status = status;
        this.data = null;
    }

    public CustomerException(CustomerStatus status, String message, Object data) {
        super(message);
        this.status = status;
        this.data = data;
    }
}