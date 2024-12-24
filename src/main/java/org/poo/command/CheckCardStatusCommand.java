package org.poo.command;

import org.poo.visitor.command.CommandVisitor;

/**
 * Comanda utilizată pentru verificarea statusului unui card.
 * Această clasă stochează informațiile necesare pentru verificarea statusului unui card,
 * iar logica comenzii este realizată de CommandVisitor
 */
public final class CheckCardStatusCommand implements Command {
    private final String command;
    private final String cardNumber;
    private final int timestamp;

    public CheckCardStatusCommand(final String cardNumber, final int timestamp,
                                  final String command) {
        this.command = command;
        this.cardNumber = cardNumber;
        this.timestamp = timestamp;
    }

    public String getCommand() {
        return command;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public int getTimestamp() {
        return timestamp;
    }

    @Override
    public void accept(final CommandVisitor visitor) {
        visitor.visit(this);
    }
}
