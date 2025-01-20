package org.poo.model.transaction;

import org.poo.visitor.transaction.TransactionVisitor;

import java.util.List;

/**
 * Clasa finală InsufficientFundsForCustomSplitTransaction reprezintă
 * o tranzacție specifică pentru fonduri insuficiente la un custom
 * split payment
 */
public final class InsufficientFundsForCustomSplitTransaction extends Transaction {
    private final double amount;
    private final List<Double> amountForUsers;
    private final String currency;
    private final String error;
    private final List<String> involvedAccounts;

    public InsufficientFundsForCustomSplitTransaction(final double amount, final String currency,
                                                final List<String> involvedAccounts,
                                                final int timestamp, final String error,
                                                final List<Double> amountForUsers) {
        super(timestamp);
        this.amountForUsers = amountForUsers;
        this.description = "Split payment of " + String.format("%.2f", amount) + " " + currency;
        this.amount = amount;
        this.error = error;
        this.currency = currency;
        this.involvedAccounts = involvedAccounts;
    }

    public double getAmount() {
        return amount;
    }

    public List<Double> getAmountForUsers() {
        return amountForUsers;
    }

    public String getCurrency() {
        return currency;
    }

    public String getError() {
        return error;
    }

    public List<String> getInvolvedAccounts() {
        return involvedAccounts;
    }

    /**
     * Folosită doar la card payment transaction
     * @return null
     */
    @Override
    public String getPaymentCommerciant() {
        return null;
    }

    /**
     * Folosită doar la card payment transaction
     * @return 0
     */
    @Override
    public double getPaymentAmount() {
        return 0;
    }

    @Override
    public String getType() {
        return "InsufficientFundsForCustomSplit";
    }

    /**
     * Acceptă un vizitator
     * @param visitor este obiectul care implementează interfața TransactionVisitor
     */
    @Override
    public void accept(final TransactionVisitor visitor) {
        visitor.visit(this);
    }
}
