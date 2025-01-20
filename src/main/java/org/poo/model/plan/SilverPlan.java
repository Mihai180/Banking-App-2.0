package org.poo.model.plan;

public final class SilverPlan implements PlanStrategy {
    private static final double MINIMUM_AMOUNT_FOR_COMMISSION = 500.0;
    private static final double COMMISSION_RATE = 0.001;
    private static final double GOLD_UPGRADE_FEE = 250.0;
    private static final double INVALID_UPGRADE_FEE = -1.0;

    @Override
    public double calculateCommission(final double amount) {
        if (amount < MINIMUM_AMOUNT_FOR_COMMISSION) {
            return 0.0;
        }
        return amount * COMMISSION_RATE;
    }

    /**
     * Verifică dacă cererea este pentru un plan inferior
     * @param requested este tipul de plan solicitat
     * @return `true` dacă planul cerut este "standard" sau "student", altfel `false`
     */
    public boolean isDowngrade(final String requested) {
        if (requested.equals("standard") || requested.equals("student")) {
            return true;
        }
        return false;
    }

    /**
     * Calculează taxa de upgrade pentru un plan specificat
     * @param requested este tipul de plan solicitat pentru upgrade
     * @return taxa pentru upgrade dacă planul este "gold", altfel -1
     */
    public double calculateUpgradeFee(final String requested) {
        if (requested.equals("gold")) {
            return GOLD_UPGRADE_FEE;
        }
        return INVALID_UPGRADE_FEE;
    }

    /**
     * Returnează numele planului curent.
     * @return numele planului sub formă de șir de caractere.
     */
    public String getPlan() {
        return "Silver";
    }
}
