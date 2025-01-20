package org.poo.model.account;

import org.poo.model.user.User;
import java.util.ArrayList;

/**
 * Clasa finală SavingsAccount reprezintă un tip de cont de economii,
 * extinzând clasa de bază Account
 */
public final class SavingsAccount extends Account {
    private double interestRate;
    public SavingsAccount(final String iban, final User owner, final String currency,
                          final double interestRate) {
        super(iban, owner, currency);
        this.interestRate = interestRate;
    }

    /**
     * Adaugă dobânda la soldul contului. Soldul crește cu sold * rata_dobânzii
     */
    @Override
    public double addInterest() {
        double oldBalance = balance;
        balance += balance * interestRate;
        return oldBalance * interestRate;
    }

    /**
     * Modifică rata dobânzii asociată contului
     * @param newInterestRate este noua rată a dobânzii
     */
    @Override
    public void changeInterestRate(final double newInterestRate) {
        this.interestRate = newInterestRate;
    }

    /**
     * Returnează tipul contului ca fiind "savings"
     * @return "savings"
     */
    @Override
    public String getAccountType() {
        return "savings";
    }

    @Override
    public void addManager(final User user) {

    }

    @Override
    public void addEmployee(final User user) {

    }

    @Override
    public double getSpendingLimit() {
        return 0;
    }

    @Override
    public double getDepositLimit() {
        return 0;
    }

    @Override
    public boolean isEmployee(final String email) {
        return false;
    }

    @Override
    public void changeSpendingLimit(final double amount) {

    }

    @Override
    public void changeDepositLimit(final double amount) {

    }

    @Override
    public ArrayList<User> getManagers() {
        return null;
    }

    @Override
    public ArrayList<User> getEmployees() {
        return null;
    }
}
