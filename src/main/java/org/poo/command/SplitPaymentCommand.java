package org.poo.command;

import org.poo.visitor.command.CommandVisitor;

import java.util.List;

/**
 * Comanda utilizată pentru o plată împărțită între mai multe conturi.
 * Această clasă stochează informațiile necesare pentru efectuarea plății,
 * iar logica comenzii este realizată de CommandVisitor
 */
public final class SplitPaymentCommand implements Command {
    private final List<String> accounts;
    private final int timestamp;
    private final String currency;
    private final double amount;
    private final String type;
    private final List<Double> amountForUsers;

    public SplitPaymentCommand(final List<String> accounts, final int timestamp,
                               final String currency, final double amount, final String type, final List<Double> amountForUsers) {
        this.accounts = accounts;
        this.timestamp = timestamp;
        this.currency = currency;
        this.amount = amount;
        this.type = type;
        this.amountForUsers = amountForUsers;
    }

    public List<String> getAccounts() {
        return accounts;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public String getCurrency() {
        return currency;
    }

    public double getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }

    public List<Double> getAmountForUsers() {
        return amountForUsers;
    }

    @Override
    public void accept(final CommandVisitor visitor) {
        visitor.visit(this);
    }
}
