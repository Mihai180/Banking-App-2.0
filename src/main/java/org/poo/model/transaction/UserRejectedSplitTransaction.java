package org.poo.model.transaction;

import org.poo.visitor.transaction.TransactionVisitor;

import java.util.List;

public class UserRejectedSplitTransaction extends Transaction {
    private final double amount;
    private final List<Double> amountForUsers;
    private final String currency;
    private final String error;
    private final List<String> involvedAccounts;
    private final String splitPaymentType;

    public UserRejectedSplitTransaction(final double amount, final String currency,
                                        final List<String> involvedAccounts,
                                        final int timestamp, final String error,
                                        final List<Double> amountForUsers,
                                        final String splitPaymentType) {
        super(timestamp);
        this.description = "Split payment of " + String.format("%.2f", amount) + " " + currency;
        this.amount = amount;
        this.currency = currency;
        this.involvedAccounts = involvedAccounts;
        this.error = error;
        this.amountForUsers = amountForUsers;
        this.splitPaymentType = splitPaymentType;
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

    public String getSplitPaymentType() {
        return splitPaymentType;
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
        return "UserRejectedCustomSplit";
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
