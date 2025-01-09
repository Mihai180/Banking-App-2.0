package org.poo.model.commerciant;

import org.poo.command.PayOnlineCommand;
import org.poo.model.account.Account;
import org.poo.model.transaction.CardPaymentTransaction;
import org.poo.model.transaction.Transaction;
import org.poo.model.user.User;

public class NrOfTransactionsCashbackStrategy implements CashbackStrategy {
    @Override
    public double calculateCashback(Account account, CardPaymentTransaction transaction) {

    }
}
