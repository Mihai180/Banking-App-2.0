package org.poo.model.transaction;

import org.poo.visitor.transaction.TransactionVisitor;

/**
 * Clasa finală CashWithdrawalTransaction reprezintă o tranzacție specifică pentru
 * retragerea de numerar
 */
public final class CashWithdrawalTransaction extends Transaction {
    private final double amount;
    public CashWithdrawalTransaction(final double amount, final int timestamp) {
        super(timestamp);
        this.amount = amount;
        this.description = "Cash withdrawal of " + amount;
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
        return "CashWithdrawal";
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
