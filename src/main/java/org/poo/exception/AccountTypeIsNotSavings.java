package org.poo.exception;

/**
 * Excepție unchecked care indică faptul că un cont nu este de tip
 * savings, chiar dacă ar trebui.
 */
public class AccountTypeIsNotSavings extends RuntimeException {
    public AccountTypeIsNotSavings(final String message) {
        super(message);
    }
}
