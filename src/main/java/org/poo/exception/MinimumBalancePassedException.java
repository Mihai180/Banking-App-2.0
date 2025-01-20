package org.poo.exception;

/**
 * Excepție unchecked care indică faptul că balanța minimă
 * a fost depășită.
 */
public class MinimumBalancePassedException extends RuntimeException {
    public MinimumBalancePassedException(final String message) {
        super(message);
    }
}
