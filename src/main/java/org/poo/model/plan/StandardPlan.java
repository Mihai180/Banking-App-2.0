package org.poo.model.plan;

public class StandardPlan implements PlanStrategy {
    @Override
    public double calculateCommission(double amount) {
        return amount * 0.002;
    }

    public boolean isDowngrade (String requested) {
        return false;
    }

    public double calculateUpgradeFee(String requested) {
        if (requested.equals("silver")) {
            return 100;
        }
        if (requested.equals("gold")) {
            return 350;
        }
        return -1;
    }

    public String getPlan() {
        return "Standard";
    }
}
