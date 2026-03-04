package com.kosign.customer_crud.exception;

import lombok.Getter;

@Getter
public class ContactValidationException extends RuntimeException{
    public ContactValidationException(String message){
        super(message);
    }
}
