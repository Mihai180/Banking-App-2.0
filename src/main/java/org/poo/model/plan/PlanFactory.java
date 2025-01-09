package org.poo.model.plan;

import org.poo.exception.UnknownPlanException;

public class PlanFactory {
    public static PlanStrategy getPlan(String planName) {
        switch (planName.toLowerCase()) {
            case "standard": return new StandardPlan();
            case "student":  return new StudentPlan();
            case "silver":   return new SilverPlan();
            case "gold":     return new GoldPlan();
            default:
                throw new UnknownPlanException("Unknown plan: " + planName);
        }
    }
}
