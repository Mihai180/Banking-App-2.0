package org.poo.exception;

/**
 * Excepție unchecked care indică faptul că limita de plată a fost depășită.
 */
public class PaymentLimitExcedeedException extends RuntimeException {
    public PaymentLimitExcedeedException(final String message) {
        super(message);
    }
}
