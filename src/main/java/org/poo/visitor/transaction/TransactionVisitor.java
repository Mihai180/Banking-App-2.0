package org.poo.visitor.transaction;

import org.poo.model.transaction.*;

/**
 * Interfața TransactionVisitor definește metodele de vizitare pentru diferite tipuri de tranzacții.
 * Aceasta permite implementarea modelului Visitor, facilitând adăugarea de noi operațiuni asupra
 * tranzacțiilor fără a modifica clasele de tranzacții existente.
 */
public interface TransactionVisitor {
    /**
     * Vizitează o tranzacție de creare a contului.
     *
     * @param transaction este tranzacția de creare a contului.
     */
    void visit(AccountCreationTransaction transaction);

    /**
     * Vizitează o tranzacție de creare a unui card.
     *
     * @param transaction este tranzacția de creare a cardului.
     */
    void visit(CardCreationTransaction transaction);

    /**
     * Vizitează o tranzacție de trimitere de bani.
     *
     * @param transaction este tranzacția de trimitere de bani.
     */
    void visit(SendMoneyTransaction transaction);

    /**
     * Vizitează o tranzacție de plată cu cardul.
     *
     * @param transaction este tranzacția de plată cu cardul.
     */
    void visit(CardPaymentTransaction transaction);

    /**
     * Vizitează o tranzacție de fonduri insuficiente.
     *
     * @param transaction este tranzacția de fonduri insuficiente.
     */
    void visit(InsufficientFundsTransaction transaction);

    /**
     * Vizitează o tranzacție de ștergere a unui card.
     *
     * @param transaction este tranzacția de ștergere a cardului.
     */
    void visit(CardDeletionTransaction transaction);

    /**
     * Vizitează o tranzacție de fonduri insuficiente pentru soldul minim.
     *
     * @param transaction este tranzacția de fonduri insuficiente pentru soldul minim.
     */
    void visit(MinimumAmountOfFundsTransaction transaction);

    /**
     * Vizitează o tranzacție de blocare a cardului.
     *
     * @param transaction este tranzacția de blocare a cardului.
     */
    void visit(FrozenCardTransaction transaction);

    /**
     * Vizitează o tranzacție de plată împărțită.
     *
     * @param transaction este tranzacția de plată împărțită.
     */
    void visit(SplitPaymentTransaction transaction);

    /**
     * Vizitează o tranzacție de fonduri insuficiente pentru o plată împărțită.
     *
     * @param transaction este tranzacția de fonduri insuficiente pentru plata împărțită.
     */
    void visit(InssuficientFundsForSplitTransaction transaction);

    /**
     * Vizitează o tranzacție de eroare la ștergerea contului.
     *
     * @param transaction este tranzacția de eroare la ștergerea contului.
     */
    void visit(AccountDeletionErrorTransaction transaction);

    /**
     * Vizitează o tranzacție de schimbare a ratei dobânzii.
     *
     * @param transaction este tranzacția de schimbare a ratei dobânzii.
     */
    void visit(InterestRateChangeTransaction transaction);

    /**
     * Vizitează o tranzacție de retragere din contul de savings.
     *
     * @param transaction este tranzacția de retragere din contul de savings.
     */
    void visit(SavingsWithdrawlTransaction transaction);

    /**
     * Vizitează o tranzacție pentru neîndeplinirea condiției de vârstă minimă.
     *
     * @param transaction este tranzacția pentru neîndeplinirea condiției de vârstă minimă.
     */
    void visit(NotMinimumAgeRequiredTransaction transaction);

    /**
     * Vizitează o tranzacție pentru neîndeplinirea condiției de cont clasic.
     *
     * @param transaction este tranzacția pentru neîndeplinirea condiției de cont clasic.
     */
    void visit(NotClassicAccountTransaction transaction);

    /**
     * Vizitează o tranzacție de upgrade al planului.
     *
     * @param transaction este tranzacția de upgrade al planului.
     */
    void visit(UpgradePlanTransaction transaction);

    /**
     * Vizitează o tranzacție de retragere de numerar.
     *
     * @param transaction este tranzacția de retragere de numerar.
     */
    void visit(CashWithdrawalTransaction transaction);

    /**
     * Vizitează o tranzacție de schimbare a interest rate-ului.
     *
     * @param transaction este tranzacția de schimbare a interest rate-ului.
     */
    void visit(InterestRateIncomeTransaction transaction);

    /**
     * Vizitează o tranzacție de custom split payment.
     *
     * @param transaction este tranzacția de custom split payment.
     */
    void visit(CustomSplitPaymentTransaction transaction);

    /**
     * Vizitează o tranzacție pentru fonduri insuficiente pentru un custom split payment.
     *
     * @param transaction este tranzacția pentru fonduri insuficiente
     * pentru un custom split payment.
     */
    void visit(InsufficientFundsForCustomSplitTransaction transaction);

    /**
     * Vizitează o tranzacție de rejectare a unei custom split payment.
     *
     * @param transaction este tranzacția de rejectare a unei custom split payment.
     */
    void visit(UserRejectedSplitTransaction transaction);

    /**
     * Vizitează o tranzacție de upgrade la același plan.
     *
     * @param transaction este tranzacția de upgrade la același plan.
     */
    void visit(UpgradeToSamePlanTransaction transaction);
}
