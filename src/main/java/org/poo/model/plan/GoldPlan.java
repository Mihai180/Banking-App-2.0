package org.poo.model.plan;

public class GoldPlan implements PlanStrategy {
    @Override
    public double calculateCommission(double amount) {
        return 0.0;
    }

    public boolean isDowngrade (String requested) {
        return true;
    }

    public double calculateUpgradeFee(String requested) {
        return 0.0;
    }

    public String getPlan() {
        return "Gold";
    }
}
