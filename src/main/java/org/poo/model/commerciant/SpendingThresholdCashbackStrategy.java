package org.poo.model.commerciant;

import org.poo.model.account.Account;
import org.poo.model.transaction.CardPaymentTransaction;
import org.poo.model.user.User;
import org.poo.service.ExchangeService;

public class SpendingThresholdCashbackStrategy implements CashbackStrategy {
    @Override
    public double calculateCashback(Account account, CardPaymentTransaction transaction) {
        User user = account.getOwner();
        double amount = transaction.getPaymentAmount();
        double cashbackPercent = 0.0;
        double convertedTotalSpent = ExchangeService.getInstance().convertCurrency(account.getCurrency(), "RON", account.getTotalSpent());
        String plan = user.getCurrentPlan().getPlan();
        if (convertedTotalSpent >= 500) {
            switch (plan) {
                case "standard":
                case "student":
                    cashbackPercent = 0.0025;
                    break;
                case "silver":
                    cashbackPercent = 0.0050;
                    break;
                case "gold":
                    cashbackPercent = 0.0070;
                    break;
            }
        } else if (convertedTotalSpent >= 300) {
            switch (plan) {
                case "standard":
                case "student":
                    cashbackPercent = 0.0020;
                    break;
                case "silver":
                    cashbackPercent = 0.0040;
                    break;
                case "gold":
                    cashbackPercent = 0.0055;
                    break;
            }
        } else if (convertedTotalSpent >= 100) {
            switch (plan) {
                case "Standard":
                case "Student":
                    cashbackPercent = 0.0010;
                    break;
                case "Silver":
                    cashbackPercent = 0.0030;
                    break;
                case "Gold":
                    cashbackPercent = 0.0050;
                    break;
            }
        }
        return amount * cashbackPercent;
    }
}
