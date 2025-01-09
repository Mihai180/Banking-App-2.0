package org.poo.model.plan;

public class SilverPlan implements PlanStrategy {
    @Override
    public double calculateCommission(double amount) {
        if (amount < 500) {
            return 0.0;
        }
        return amount * 0.001;
    }

    public boolean isDowngrade (String requested) {
        if (requested.equals("standard") || requested.equals("student")) {
            return true;
        }
        return false;
    }

    public double calculateUpgradeFee(String requested) {
        if (requested.equals("gold")) {
            return 250;
        }
        return -1;
    }
}
