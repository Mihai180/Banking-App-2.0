package org.poo.model.commerciant;

import org.poo.model.account.Account;
import org.poo.model.transaction.CardPaymentTransaction;
import org.poo.model.user.User;
import org.poo.service.ExchangeService;

public final class SpendingThresholdCashbackStrategy implements CashbackStrategy {
    private static final double HIGH_THRESHOLD = 500.0;
    private static final double MID_THRESHOLD = 300.0;
    private static final double LOW_THRESHOLD = 100.0;

    private static final double STANDARD_STUDENT_HIGH_RATE = 0.0025;
    private static final double STANDARD_STUDENT_MID_RATE = 0.0020;
    private static final double STANDARD_STUDENT_LOW_RATE = 0.0010;

    private static final double SILVER_HIGH_RATE = 0.0050;
    private static final double SILVER_MID_RATE = 0.0040;
    private static final double SILVER_LOW_RATE = 0.0030;

    private static final double GOLD_HIGH_RATE = 0.0070;
    private static final double GOLD_MID_RATE = 0.0055;
    private static final double GOLD_LOW_RATE = 0.0050;

    /**
     * Calculează valoarea cashback-ului pe
     * baza pragurilor de cheltuieli și a planului utilizatorului.
     *
     * @param account     contul asociat tranzacției
     * @param transaction tranzacția pentru care se calculează cashback-ul
     * @return valoarea cashback-ului calculat
     */
    @Override
    public double calculateCashback(final Account account,
                                    final CardPaymentTransaction transaction) {
        User user = account.getOwner();
        double amount = transaction.getPaymentAmount();
        double cashbackPercent = 0.0;
        double convertedTotalSpent =
                ExchangeService.getInstance().convertCurrency(
                        account.getCurrency(),
                        "RON", account.getTotalSpent());
        String plan = user.getCurrentPlan().getPlan();
        if (convertedTotalSpent >= HIGH_THRESHOLD) {
            switch (plan) {
                case "Standard":
                case "Student":
                    cashbackPercent = STANDARD_STUDENT_HIGH_RATE;
                    break;
                case "Silver":
                    cashbackPercent = SILVER_HIGH_RATE;
                    break;
                case "Gold":
                    cashbackPercent = GOLD_HIGH_RATE;
                    break;
            }
        } else if (convertedTotalSpent >= MID_THRESHOLD) {
            switch (plan) {
                case "Standard":
                case "Student":
                    cashbackPercent = STANDARD_STUDENT_MID_RATE;
                    break;
                case "Silver":
                    cashbackPercent = SILVER_MID_RATE;
                    break;
                case "Gold":
                    cashbackPercent = GOLD_MID_RATE;
                    break;
            }
        } else if (convertedTotalSpent >= LOW_THRESHOLD) {
            switch (plan) {
                case "Standard":
                case "Student":
                    cashbackPercent = STANDARD_STUDENT_LOW_RATE;
                    break;
                case "Silver":
                    cashbackPercent = SILVER_LOW_RATE;
                    break;
                case "Gold":
                    cashbackPercent = GOLD_LOW_RATE;
                    break;
            }
        }
        return amount * cashbackPercent;
    }

    /**
     * Returnează tipul strategiei de cashback.
     *
     * @return un șir de caractere care descrie tipul strategiei
     */
    public String getCashbackType() {
        return "SpendingTreshold";
    }
}
