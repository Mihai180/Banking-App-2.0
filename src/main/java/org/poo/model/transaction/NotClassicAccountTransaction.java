package org.poo.model.transaction;

import org.poo.visitor.transaction.TransactionVisitor;

public class NotClassicAccountTransaction extends Transaction {
    public NotClassicAccountTransaction(final int timestamp) {
        super(timestamp);
        this.description = "You do not have a classic account.";
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
