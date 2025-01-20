package org.poo.exception;

/**
 * Excepție unchecked care indică faptul că vârsta minimă nu a
 * fost atinsă.
 */
public class NotMinimumAgeRequired extends RuntimeException {
    public NotMinimumAgeRequired(final String message) {
        super(message);
    }
}
