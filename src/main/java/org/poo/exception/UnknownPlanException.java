package org.poo.exception;

/**
 * Excepție unchecked care indică imposibilitatea de a face upgrade la un plan
 * necunoscut.
 */
public class UnknownPlanException extends RuntimeException {
    public UnknownPlanException(final String message) {
        super(message);
    }
}
