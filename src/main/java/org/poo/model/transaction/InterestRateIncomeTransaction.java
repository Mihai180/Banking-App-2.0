package org.poo.model.transaction;

import org.poo.visitor.transaction.TransactionVisitor;

public class InterestRateIncomeTransaction extends Transaction {
    private final double amount;
    private final String currency;
    public InterestRateIncomeTransaction(final double amount, final String currency, final int timestamp) {
        super(timestamp);
        this.amount = amount;
        this.currency = currency;
        this.description = "Interest rate income";
    }

    public String getCurrency() {
        return currency;
    }

    public double getAmount() {
        return amount;
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
        return "InterestRateIncome";
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
