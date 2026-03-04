package com.kosign.customer_crud.exception;

public class DuplicatedEmailException extends RuntimeException{
    public DuplicatedEmailException(String message){
        super(message);
    }
}
