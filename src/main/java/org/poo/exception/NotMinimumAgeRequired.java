package org.poo.exception;

public class NotMinimumAgeRequired extends RuntimeException {
    public NotMinimumAgeRequired(final String message) {
        super(message);
    }
}
