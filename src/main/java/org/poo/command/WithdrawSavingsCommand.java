package org.poo.command;

import org.poo.visitor.command.CommandVisitor;

/**
 * Comanda utilizată pentru a retrage dintr-un cont de economii.
 * Această clasă stochează informațiile necesare pentru a retrage
 * dintr-un cont de economii, iar logica, comenzii este realizată de CommandVisitor
 */
public final class WithdrawSavingsCommand implements Command {
    private String command;
    private String account;
    private double amount;
    private String currency;
    private int timestamp;

    public WithdrawSavingsCommand(final String command, final String account,
                                  final double amount, final String currency,
                                  final int timestamp) {
        this.command = command;
        this.account = account;
        this.amount = amount;
        this.currency = currency;
        this.timestamp = timestamp;
    }

    public String getCommand() {
        return command;
    }

    public String getAccount() {
        return account;
    }

    public double getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public int getTimestamp() {
        return timestamp;
    }

    @Override
    public void accept(final CommandVisitor visitor) {
        visitor.visit(this);
    }
}
