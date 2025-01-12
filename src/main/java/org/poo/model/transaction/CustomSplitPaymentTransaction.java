package org.poo.model.transaction;

import org.poo.visitor.transaction.TransactionVisitor;

import java.util.List;

public class CustomSplitPaymentTransaction extends Transaction {
    private final String currency;
    private final List<Double> amountForUsers;
    private final List<String> involvedAccounts;
    private final String amount;

    public CustomSplitPaymentTransaction(final int timestamp, final String currency, final List<Double> amountForUsers,
                                   final List<String> involvedAccounts, final String amount) {
        super(timestamp);
        this.description = "Split payment of ";
        this.currency = currency;
        this.amountForUsers = amountForUsers;
        this.involvedAccounts = involvedAccounts;
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public List<Double> getAmountForUsers() {
        return amountForUsers;
    }

    public List<String> getInvolvedAccounts() {
        return involvedAccounts;
    }

    public String getAmount() {
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
        return "CustomSplitPayment";
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
