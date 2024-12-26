package org.poo.exception;

public class AccountTypeIsNotSavings extends RuntimeException {
    public AccountTypeIsNotSavings(final String message) {
        super(message);
    }
}
