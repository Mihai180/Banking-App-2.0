package org.poo.model.plan;

public interface PlanStrategy {
    /**
     * Calculate the commission for a given transaction amount in RON.
     * @param amount the transaction amount in RON
     * @return the commission fee
     */
    double calculateCommission(double amount);

    boolean isDowngrade (String requested);

    double calculateUpgradeFee(String requested);

    String getPlan();
}
