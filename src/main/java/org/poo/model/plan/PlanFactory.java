package org.poo.model.plan;

import org.poo.exception.UnknownPlanException;

public final class PlanFactory {
    /**
     * Metodă pentru a obține o strategie de plan pe baza numelui planului
     * @param planName este numele planului
     * @return obiectul corespunzător de tip PlanStrategy
     * @throws UnknownPlanException dacă numele planului nu este recunoscut.
     */
    public static PlanStrategy getPlan(final String planName) {
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
