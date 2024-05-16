package com.producer.producer_service.exception;

public class InsufficientFundsOnTheCardException extends RuntimeException {
    public InsufficientFundsOnTheCardException(String message) {
        super(message);
    }
}
