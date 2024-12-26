package org.poo.command;

import org.poo.visitor.command.CommandVisitor;

public class WithdrawSavingsCommand implements Command {
    private String command;
    private String account;
    private double amount;
    private String currency;
    private int timestamp;

    public WithdrawSavingsCommand(String command, String account, double amount, String currency, int timestamp) {
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

    public void accept(final CommandVisitor visitor) {
        visitor.visit(this);
    }
}
