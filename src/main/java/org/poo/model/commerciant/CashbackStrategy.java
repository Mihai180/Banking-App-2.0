package org.poo.model.commerciant;

import org.poo.model.account.Account;
import org.poo.model.transaction.Transaction;
import org.poo.model.user.User;

public interface CashbackStrategy {
    double calculateCashback(Account account, Transaction transaction);
}
