package org.poo.command;

import org.poo.visitor.command.CommandVisitor;

/**
 * Comanda utilizată pentru retragerea de numerar dintr-un cont.
 * Această clasă stochează informațiile necesare pentru retragerea de numerar
 * dintr-un cont, iar logica, comenzii este realizată de CommandVisitor
 */
public final class CashWithdrawalCommand implements Command {
    private final String command;
    private final String cardNumber;
    private final double amount;
    private final String email;
    private final String location;
    private final int timestamp;

    public CashWithdrawalCommand(final String command, final String cardNumber,
                                 final double amount, final String email,
                                 final String location, final int timestamp) {
        this.command = command;
        this.cardNumber = cardNumber;
        this.amount = amount;
        this.email = email;
        this.location = location;
        this.timestamp = timestamp;
    }

    public String getCommand() {
        return command;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public double getAmount() {
        return amount;
    }

    public String getEmail() {
        return email;
    }

    public String getLocation() {
        return location;
    }

    public int getTimestamp() {
        return timestamp;
    }

    @Override
    public void accept(final CommandVisitor visitor) {
        visitor.visit(this);
    }
}
