package org.poo.model.account;

import org.poo.model.user.User;
import java.util.ArrayList;

/**
 * Clasa finală ClassicAccount reprezintă un tip specific de cont,
 * care extinde clasa abstractă Account
 */
public final class ClassicAccount extends Account {
    public ClassicAccount(final String iban, final User owner, final String currency) {
        super(iban, owner, currency);
    }

    /**
     * Returnează tipul contului ca fiind "classic"
     * @return "classic"
     */
    @Override
    public String getAccountType() {
        return "classic";
    }

    /**
     * Metoda este goală deoarece nu este nevoie de aceasta operațiune la un cont classic
     * @param newInterestRate este noua rată a dobânzii
     */
    @Override
    public void changeInterestRate(final double newInterestRate) {

    }

    /**
     * Metoda este goală deoarece nu este nevoie de aceasta operațiune la un cont classic
     */
    @Override
    public double addInterest() {
        return -1;
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
