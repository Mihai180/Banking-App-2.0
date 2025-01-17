package org.poo.exception;

public class PaymentLimitExcedeedException extends RuntimeException {
    public PaymentLimitExcedeedException(final String message) {
        super(message);
    }
}
