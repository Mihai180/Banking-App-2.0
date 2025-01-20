package org.poo.exception;

/**
 * Excepție unchecked care indică imposibilitatea de a face downgrade
 * al planului.
 */
public class PlanDowngradeException extends RuntimeException {
    public PlanDowngradeException(final String message) {
        super(message);
    }
}
