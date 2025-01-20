package org.poo.model.transaction;

import org.poo.visitor.transaction.TransactionVisitor;

/**
 * Clasa finală SavingsWithdrawlTransaction reprezintă
 * o tranzacție specifică pentru retragerea de bani
 * dintr-un cont de tip savings
 */
public final class SavingsWithdrawlTransaction extends Transaction {
    private String savingsAccountIBAN;
    private String classicAccountIBAN;
    private double amount;
    public SavingsWithdrawlTransaction(final int timestamp, final String savingsAccountIBAN,
                                       final String classicAccountIBAN, final double amount) {
        super(timestamp);
        this.description = "Savings withdrawal";
        this.amount = amount;
        this.savingsAccountIBAN = savingsAccountIBAN;
        this.classicAccountIBAN = classicAccountIBAN;
    }

    public String getClassicAccountIBAN() {
        return classicAccountIBAN;
    }

    public String getSavingsAccountIBAN() {
        return savingsAccountIBAN;
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
        return "SavingsWithdrawal";
    }

    @Override
    public void accept(final TransactionVisitor visitor) {
        visitor.visit(this);
    }
}
