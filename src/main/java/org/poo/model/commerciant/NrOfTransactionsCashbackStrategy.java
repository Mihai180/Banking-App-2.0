package org.poo.model.commerciant;

import org.poo.command.PayOnlineCommand;
import org.poo.model.account.Account;
import org.poo.model.transaction.CardPaymentTransaction;
import org.poo.model.transaction.Transaction;
import org.poo.model.user.User;
import org.poo.service.commerciant.CommerciantService;

public class NrOfTransactionsCashbackStrategy implements CashbackStrategy {
    @Override
    public double calculateCashback(Account account, CardPaymentTransaction transaction) {
        Commerciant commerciant = CommerciantService.getCommerciantByName(transaction.getPaymentCommerciant());
        String commerciantType = commerciant.getType();
        double amount = transaction.getPaymentAmount();
        if (commerciantType.equals("Food") && account.getNumOfTransactions() >= 2) {
            return 0.02 * amount;
        } else if (commerciantType.equals("Clothes") && account.getNumOfTransactions() >= 5) {
            return 0.05 * amount;
        } else if (commerciantType.equals("Tech") && account.getNumOfTransactions() >= 10) {
            return 0.10 * amount;
        }
        return 0;
    }

    @Override
    public String getCashbackType() {
        return "NrOfTransactions";
    }
}
