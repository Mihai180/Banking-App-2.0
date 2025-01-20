package org.poo.command;

import org.poo.visitor.command.CommandVisitor;

/**
 * Comanda utilizată pentru trimiterea banilor.
 * Această clasă stochează informațiile necesare pentru trimiterea banilor,
 * iar logica comenzii este realizată de CommandVisitor
 */
public final class SendMoneyCommand implements Command {
    private final String account;
    private final double amount;
    private final String reciever;
    private final int timestamp;
    private final String description;
    private final String email;

    public SendMoneyCommand(final String account, final double amount, final String reciever,
                            final int timestamp, final String description, final String email) {
        this.account = account;
        this.amount = amount;
        this.reciever = reciever;
        this.timestamp = timestamp;
        this.description = description;
        this.email = email;
    }

    public String getAccount() {
        return account;
    }

    public double getAmount() {
        return amount;
    }

    public String getReciever() {
        return reciever;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public void accept(final CommandVisitor visitor) {
        visitor.visit(this);
    }
}
