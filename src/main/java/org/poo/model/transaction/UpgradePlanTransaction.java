package org.poo.model.transaction;

import org.poo.visitor.transaction.TransactionVisitor;

/**
 * Clasa finală UpgradePlanTransaction reprezintă
 * o tranzacție specifică pentru upgrade-ul planului
 */
public final class UpgradePlanTransaction extends Transaction {
    private final String account;
    private final String newPlanType;
    public UpgradePlanTransaction(final String account,
                                  final String newPlanType,
                                  final int timestamp) {
        super(timestamp);
        this.account = account;
        this.newPlanType = newPlanType;
        this.description = "Upgrade plan";
    }

    public String getAccount() {
        return account;
    }

    public String getNewPlanType() {
        return newPlanType;
    }

    @Override
    public String getType() {
        return "UpgradePlan";
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
