package org.poo.model.plan;

public final class StudentPlan implements PlanStrategy {
    private static final double UPGRADE_FEE_SILVER = 100.0;
    private static final double UPGRADE_FEE_GOLD = 350.0;
    private static final double INVALID_UPGRADE_FEE = -1.0;

    @Override
    public double calculateCommission(final double amount) {
        return 0.0;
    }

    /**
     * Verifică dacă un plan cerut reprezintă o retrogradare.
     *
     * @param requested tipul de plan solicitat
     * @return `false`, deoarece planul Student nu poate fi retrogradat
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
            return UPGRADE_FEE_SILVER;
        }
        if (requested.equals("gold")) {
            return UPGRADE_FEE_GOLD;
        }
        return INVALID_UPGRADE_FEE;
    }

    /**
     * Returnează numele planului.
     *
     * @return numele planului sub formă de șir de caractere
     */
    public String getPlan() {
        return "Student";
    }
}
