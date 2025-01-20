package org.poo.model.plan;

public final class StandardPlan implements PlanStrategy {
    private static final double COMMISSION_RATE = 0.002;
    private static final double SILVER_UPGRADE_FEE = 100.0;
    private static final double GOLD_UPGRADE_FEE = 350.0;
    private static final double INVALID_UPGRADE_FEE = -1.0;

    @Override
    public double calculateCommission(final double amount) {
        return amount * COMMISSION_RATE;
    }

    /**
     * Verifică dacă planul cerut reprezintă o retrogradare.
     *
     * @param requested tipul de plan solicitat
     * @return `false` deoarece planul Standard nu poate fi retrogradat
     */
    public boolean isDowngrade(final String requested) {
        return false;
    }

    /**
     * Calculează taxa pentru un upgrade către un plan specificat.
     *
     * @param requested tipul de plan solicitat pentru upgrade
     * @return taxa de upgrade către planurile "silver" sau "gold", altfel -1
     */
    public double calculateUpgradeFee(final String requested) {
        if (requested.equals("silver")) {
            return SILVER_UPGRADE_FEE;
        }
        if (requested.equals("gold")) {
            return GOLD_UPGRADE_FEE;
        }
        return INVALID_UPGRADE_FEE;
    }

    /**
     * Returnează numele planului.
     *
     * @return numele planului sub formă de șir de caractere
     */
    public String getPlan() {
        return "Standard";
    }
}
