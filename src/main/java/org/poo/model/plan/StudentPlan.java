package org.poo.model.plan;

public class StudentPlan implements PlanStrategy {
    @Override
    public double calculateCommission(double amount) {
        return 0.0;
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
        return "Student";
    }
}
