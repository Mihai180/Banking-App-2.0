package org.poo.model.commerciant;

import org.poo.service.commerciant.CommerciantService;

public final class CashbackStrategyFactory {
    /**
     * Creează și returnează strategia de cashback corespunzătoare unui comerciant specific.
     *
     * @param commerciantName numele comerciantului pentru care se dorește strategia de cashback
     * @return o instanță a strategiei de cashback corespunzătoare
     * ("nrOfTransactions" sau "spendingThreshold"),
     * sau `null` dacă strategia nu este recunoscută
     */
    public static CashbackStrategy getStrategy(final String commerciantName) {
        Commerciant commerciant = CommerciantService.getCommerciantByName(commerciantName);
        String strategyName = commerciant.getCashbackStrategy();
        switch (strategyName) {
            case "nrOfTransactions":
                return new NrOfTransactionsCashbackStrategy();
            case "spendingThreshold":
                return new SpendingThresholdCashbackStrategy();
            default:
                return null;
        }
    }
}
