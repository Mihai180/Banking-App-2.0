package org.poo.model.commerciant;

import org.poo.model.account.Account;
import org.poo.model.transaction.CardPaymentTransaction;

public interface CashbackStrategy {
    /**
     * Calculează valoarea cashback-ului pentru o tranzacție specifică,
     * pe baza detaliilor contului și tranzacției.
     *
     * @param account contul asociat tranzacției
     * @param transaction tranzacția pentru care se calculează cashback-ul
     * @return valoarea cashback-ului calculat
     */
    double calculateCashback(Account account,
                             CardPaymentTransaction transaction);
    /**
     * Returnează tipul strategiei de cashback utilizate.
     *
     * @return un șir de caractere care descrie tipul strategiei de cashback
     */
    String getCashbackType();
}
