package org.poo.model.account;

import org.poo.model.user.User;

import java.util.ArrayList;
import java.util.List;

public final class BusinessAccount extends Account {
    private List<User> managers;
    private List<User> employees;
    private double spendingLimit;
    private double depositLimit;
    private static final double DEFAULT_LIMIT = 500.0;

    public BusinessAccount(final String iban, final User owner, final String currency) {
        super(iban, owner, currency);
        this.managers = new ArrayList<>();
        this.employees = new ArrayList<>();
        this.spendingLimit = DEFAULT_LIMIT;
        this.depositLimit = DEFAULT_LIMIT;
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

    /**
     * Adaugă un manager la lista de manageri ai contului.
     *
     * @param user utilizatorul care va fi adăugat ca manager
     */
    @Override
    public void addManager(final User user) {
        if (!managers.contains(user)) {
            managers.add(user);
        }
    }

    /**
     * Adaugă un angajat la lista de angajați ai contului.
     *
     * @param user utilizatorul care va fi adăugat ca angajat
     */
    @Override
    public void addEmployee(final User user) {
        if (!employees.contains(user)) {
            employees.add(user);
        }
    }

    /**
     * Returnează limita de cheltuieli pentru acest cont.
     *
     * @return limita de cheltuieli
     */
    @Override
    public double getSpendingLimit() {
        return spendingLimit;
    }

    /**
     * Returnează limita de depozit pentru acest cont.
     *
     * @return limita de depozit
     */
    @Override
    public double getDepositLimit() {
        return depositLimit;
    }

    /**
     * Verifică dacă un utilizator este angajat pe baza email-ului.
     *
     * @param email email-ul utilizatorului
     * @return `true` dacă utilizatorul este angajat, altfel `false`
     */
    @Override
    public boolean isEmployee(final String email) {
        return employees.stream().anyMatch(user -> user.getEmail().equals(email));
    }

    /**
     * Modifică limita de cheltuieli a contului.
     *
     * @param amount noua limită de cheltuieli
     */
    @Override
    public void changeSpendingLimit(final double amount) {
        this.spendingLimit = amount;
    }

    /**
     * Modifică limita de depozit a contului.
     *
     * @param amount noua limită de depozit
     */
    @Override
    public void changeDepositLimit(final double amount) {
        this.depositLimit = amount;
    }

    /**
     * Returnează lista angajaților asociați acestui cont.
     *
     * @return lista angajaților
     */
    @Override
    public List<User> getEmployees() {
        return employees;
    }

    /**
     * Returnează lista managerilor asociați acestui cont.
     *
     * @return lista managerilor
     */
    @Override
    public List<User> getManagers() {
        return managers;
    }
}
