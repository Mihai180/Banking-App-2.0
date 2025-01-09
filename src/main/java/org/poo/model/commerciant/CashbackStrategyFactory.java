package org.poo.model.commerciant;

import org.poo.model.account.Account;
import org.poo.service.commerciant.CommerciantService;

public class CashbackStrategyFactory {
    public static CashbackStrategy getStrategy(String commerciantName) {
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
