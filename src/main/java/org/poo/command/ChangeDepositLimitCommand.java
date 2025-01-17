package org.poo.command;

import org.poo.visitor.command.CommandVisitor;

public class ChangeDepositLimitCommand implements Command {
    private final String command;
    private final String email;
    private final String account;
    private final double amount;
    private final int timestamp;
    public ChangeDepositLimitCommand(String command, String email, String account, double amount, int timestamp) {
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
