package com.kosign.customer_crud.exception;

import lombok.Getter;

@Getter
public class ActiveOrderException extends RuntimeException{
    private final Long customerId;
    private final Integer activeOrderCount;

    public ActiveOrderException(Long customerId, Integer activeOrderCount) {
        super("Customer cannot be deleted because they have active orders.");
        this.customerId = customerId;
        this.activeOrderCount = activeOrderCount;
    }
}
