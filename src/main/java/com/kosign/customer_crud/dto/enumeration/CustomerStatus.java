package com.kosign.customer_crud.dto.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
public enum CustomerStatus {
    SUCCESS(HttpStatus.OK),
    CUSTOMER_NOT_FOUND(HttpStatus.NOT_FOUND),
    DUPLICATE_CUSTOMER_EMAIL(HttpStatus.CONFLICT),
    DUPLICATE_CUSTOMER_PHONE(HttpStatus.CONFLICT),
    CUSTOMER_HAS_ACTIVE_ORDERS(HttpStatus.CONFLICT),
    REQUIRED_FIELD_MISSING(HttpStatus.BAD_REQUEST),
    INVALID_FIELD_FORMAT(HttpStatus.BAD_REQUEST);

    private final HttpStatus httpStatus;

    CustomerStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}
