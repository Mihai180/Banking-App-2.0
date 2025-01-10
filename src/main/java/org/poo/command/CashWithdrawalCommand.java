package org.poo.command;

import org.poo.visitor.command.CommandVisitor;

public class CashWithdrawalCommand implements Command {
    private final String command;
    private final String cardNumber;
    private final double amount;
    private final String email;
    private final String location;
    private final int timestamp;

    public CashWithdrawalCommand(String command, String cardNumber, double amount, String email, String location, int timestamp) {
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
