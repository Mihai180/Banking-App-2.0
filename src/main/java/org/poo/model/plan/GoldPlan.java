package org.poo.model.plan;

public final class GoldPlan implements PlanStrategy {
    @Override
    public double calculateCommission(final double amount) {
        return 0.0;
    }

    /**
     * Verifică dacă un plan cerut reprezintă o retrogradare.
     *
     * @param requested tipul de plan solicitat
     * @return `true`, deoarece planul Gold poate fi retrogradat
     */
    public boolean isDowngrade(final String requested) {
        return true;
    }

    /**
     * Calculează taxa pentru un upgrade către un plan specificat.
     *
     * @param requested tipul de plan solicitat pentru upgrade
     * @return `0.0`, deoarece planul Gold nu are costuri de upgrade
     */
    public double calculateUpgradeFee(final String requested) {
        return 0.0;
    }

    /**
     * Returnează numele planului.
     *
     * @return numele planului sub formă de șir de caractere
     */
    public String getPlan() {
        return "Gold";
    }
}
