package org.poo.exception;

/**
 * Excepție unchecked care indică imposibilitatea depunerii într-un
 * cont de business deoarece limita de depozitare a fost atinsă.
 */
public class DepositLimitExcedeedException extends RuntimeException {
    public DepositLimitExcedeedException(final String message) {
        super(message);
    }
}
