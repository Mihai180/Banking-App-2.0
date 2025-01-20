package org.poo.model.transaction;

import org.poo.visitor.transaction.TransactionVisitor;

/**
 * Clasa finală UpgradeToSamePlanTransaction reprezintă
 * o tranzacție specifică pentru încercarea de upgrade al planului
 * la același plan
 */
public final class UpgradeToSamePlanTransaction extends Transaction {
    public UpgradeToSamePlanTransaction(final int timestamp, final String description) {
        super(timestamp);
        this.description = description;
    }

    @Override
    public String getType() {
        return "UpgradeToSamePlan";
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
    public void accept(final TransactionVisitor visitor) {
        visitor.visit(this);
    }
}
