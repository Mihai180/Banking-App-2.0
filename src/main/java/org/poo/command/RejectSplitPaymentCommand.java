package org.poo.command;

import org.poo.visitor.command.CommandVisitor;

/**
 * Comanda utilizată pentru a refuza un split payment.
 * Această clasă stochează informațiile necesare pentru a refuza un split payment,
 * iar logica, comenzii este realizată de CommandVisitor
 */
public final class RejectSplitPaymentCommand implements Command {
    private final String command;
    private final String email;
    private final String splitPaymentType;
    private final int timestamp;

    public RejectSplitPaymentCommand(final String command, final String email,
                                     final String splitPaymentType,
                                     final int timestamp) {
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
