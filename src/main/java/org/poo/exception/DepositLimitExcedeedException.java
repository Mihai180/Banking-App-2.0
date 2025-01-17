package org.poo.exception;

public class DepositLimitExcedeedException extends RuntimeException {
    public DepositLimitExcedeedException(final String message) {
        super(message);
    }
}
