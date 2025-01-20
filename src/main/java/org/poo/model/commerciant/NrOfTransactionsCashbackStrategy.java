package org.poo.model.commerciant;

import org.poo.model.account.Account;
import org.poo.model.transaction.CardPaymentTransaction;
import org.poo.service.CommerciantService;

public final class NrOfTransactionsCashbackStrategy implements CashbackStrategy {
    private static final double FOOD_CASHBACK_RATE = 0.02;
    private static final double CLOTHES_CASHBACK_RATE = 0.05;
    private static final double TECH_CASHBACK_RATE = 0.10;
    private static final int FOOD_TRANSACTION_THRESHOLD = 2;
    private static final int CLOTHES_TRANSACTION_THRESHOLD = 5;
    private static final int TECH_TRANSACTION_THRESHOLD = 10;

    /**
     * Calculează valoarea cashback-ului pentru o tranzacție specifică, în funcție de numărul de
     * tranzacții efectuate și tipul comerciantului.
     *
     * @param account contul asociat tranzacției
     * @param transaction tranzacția pentru care se calculează cashback-ul
     * @return valoarea cashback-ului calculat sau `0` dacă nu se îndeplinesc condițiile
     */
    @Override
    public double calculateCashback(final Account account,
                                    final CardPaymentTransaction transaction) {
        Commerciant commerciant =
                CommerciantService.getCommerciantByName(
                        transaction.getPaymentCommerciant());
        String commerciantType = commerciant.getType();
        double amount = transaction.getPaymentAmount();
        if (commerciantType.equals("Food") && account.getNumOfTransactions()
                >= FOOD_TRANSACTION_THRESHOLD) {
            return FOOD_CASHBACK_RATE * amount;
        } else if (commerciantType.equals("Clothes") && account.getNumOfTransactions()
                >= CLOTHES_TRANSACTION_THRESHOLD) {
            return CLOTHES_CASHBACK_RATE * amount;
        } else if (commerciantType.equals("Tech") && account.getNumOfTransactions()
                >= TECH_TRANSACTION_THRESHOLD) {
            return TECH_CASHBACK_RATE * amount;
        }
        return 0;
    }

    /**
     * Returnează tipul strategiei de cashback.
     *
     * @return un șir de caractere care descrie tipul strategiei
     */
    @Override
    public String getCashbackType() {
        return "NrOfTransactions";
    }
}
