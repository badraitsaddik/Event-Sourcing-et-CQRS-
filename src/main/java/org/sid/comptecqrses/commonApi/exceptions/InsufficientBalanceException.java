package org.sid.comptecqrses.commonApi.exceptions;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String insufficientBalance) {
        super(insufficientBalance);
    }
}
