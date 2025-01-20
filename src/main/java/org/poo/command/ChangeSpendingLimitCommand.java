package org.poo.command;

import org.poo.visitor.command.CommandVisitor;

/**
 * Comanda utilizată pentru schimbarea limitei de cheltuit a unui cont business.
 * Această clasă stochează informațiile necesare pentru schimbarea limitei de
 * cheltuit a unui cont business, iar logica, comenzii este realizată de CommandVisitor
 */
public final class ChangeSpendingLimitCommand implements Command {
    private final String command;
    private final String email;
    private final String account;
    private final double amount;
    private final int timestamp;
    public ChangeSpendingLimitCommand(final String command, final String email,
                                      final String account, final double amount,
                                      final int timestamp) {
        this.command = command;
        this.email = email;
        this.account = account;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public String getCommand() {
        return command;
    }

    public String getEmail() {
        return email;
    }

    public String getAccount() {
        return account;
    }

    public double getAmount() {
        return amount;
    }

    public int getTimestamp() {
        return timestamp;
    }

    @Override
    public void accept(final CommandVisitor visitor) {
        visitor.visit(this);
    }
}
