package org.poo.model.account;

import org.poo.model.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BusinessAccount extends Account {
    private List<User> managers;
    private List<User> employees;
    private double spendingLimit;
    private double depositLimit;
    public BusinessAccount(final String iban, final User owner, final String currency) {
        super(iban, owner, currency);
        this.managers = new ArrayList<>();
        this.employees = new ArrayList<>();
        this.spendingLimit = 500;
        this.depositLimit = 500;
    }

    /**
     * Returnează tipul contului ca fiind "classic"
     * @return "business"
     */
    @Override
    public String getAccountType() {
        return "business";
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
        if (!managers.contains(user)) {
            managers.add(user);
        }
    }

    @Override
    public void addEmployee(final User user) {
        if (!employees.contains(user)) {
            employees.add(user);
        }
    }

    @Override
    public double getSpendingLimit() {
        return spendingLimit;
    }

    @Override
    public double getDepositLimit() {
        return depositLimit;
    }

    @Override
    public boolean isEmployee(String email) {
        return employees.stream().anyMatch(user -> user.getEmail().equals(email));
    }


    @Override
    public void changeSpendingLimit(double amount) {
        this.spendingLimit = amount;
    }

    @Override
    public void changeDepositLimit(double amount) {
        this.depositLimit = amount;
    }

    @Override
    public List<User> getEmployees() {
        return employees;
    }

    @Override
    public List<User> getManagers() {
        return managers;
    }
}
