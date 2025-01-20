package org.poo.exception;

/**
 * Excepție unchecked care indică imposibilitatea de a face upgrade la
 * același plan.
 */
public class SamePlanException extends RuntimeException {
    public SamePlanException(final String message) {
        super(message);
    }
}
