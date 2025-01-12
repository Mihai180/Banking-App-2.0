package org.poo.command;

import org.poo.visitor.command.CommandVisitor;

public class AcceptSplitPayment implements Command {
    private String command;
    private String email;
    private String splitPaymentType;
    private int timestamp;

    public AcceptSplitPayment(String command, String email, String splitPaymentType, int timestamp) {
        this.command = command;
        this.email = email;
        this.splitPaymentType = splitPaymentType;
        this.timestamp = timestamp;
    }

    public String getCommand() {
        return command;
    }

    public String getEmail() {
        return email;
    }

    public String getSplitPaymentType() {
        return splitPaymentType;
    }

    public int getTimestamp() {
        return timestamp;
    }

    @Override
    public void accept(final CommandVisitor visitor) {
        visitor.visit(this);
    }
}
