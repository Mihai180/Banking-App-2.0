package org.poo.exception;

/**
 * Excepție unchecked care indică faptul că nu este un cont clasic
 * chiar dacă ar trebui.
 */
public class NotClassicAccountException extends RuntimeException {
    public NotClassicAccountException(final String message) {
        super(message);
    }
}
