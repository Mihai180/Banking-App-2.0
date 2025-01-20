package org.poo.model.account;

import org.poo.model.card.Card;
import org.poo.model.transaction.Transaction;
import org.poo.model.user.User;
import java.util.ArrayList;
import java.util.List;

/**
 * Clasa abstractă Account reprezintă o bază comună pentru tipurile de conturi bancare
 */
public abstract class Account {
    protected String iban;
    protected double balance;
    protected String currency;
    protected ArrayList<Transaction> transactions;
    protected User owner;
    protected Double minimumBalance;
    protected ArrayList<Card> cards;
    protected double totalSpent;
    protected int numOfTransactions;
    protected int numOfTransactionsOver300RON;
    protected boolean cahsbackEarned;
    protected double amountForSplit;

    /**
     * Constructorul inițializează contul cu IBAN, proprietar și monedă.
     * Soldul inițial este 0, nu există o limită minimă la crearea contului,
     * iar listele pentru tranzacții și carduri sunt goale
     * */
    public Account(final String iban, final User owner, final String currency) {
        this.iban = iban;
        this.owner = owner;
        this.currency = currency;
        this.transactions = new ArrayList<>();
        this.minimumBalance = null;
        this.cards = new ArrayList<>();
        this.balance = 0.0;
        this.totalSpent = 0.0;
        this.numOfTransactions = 0;
        this.numOfTransactionsOver300RON = 0;
        this.cahsbackEarned = false;
        this.amountForSplit = 0.0;
    }

    /**
     * @return IBAN-ul contului
     */
    public String getIban() {
        return iban;
    }

    /**
     * @return Soldul curent al contului
     */
    public double getBalance() {
        return balance;
    }

    /**
     * @return Moneda contului
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * @return Lista de tranzacții ale contului
     */
    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    /**
     * @return Proprietarul contului
     */
    public User getOwner() {
        return owner;
    }

    /**
     * @return Balanța minimă a contului
     */
    public Double getMinimumBalance() {
        return minimumBalance;
    }

    /**
     * @return Lista de carduri ale contului
     */
    public ArrayList<Card> getCards() {
        return cards;
    }

    /**
     * @return Suma totală cheltuită din cont
     */
    public double getTotalSpent() {
        return totalSpent;
    }

    /**
     * @return Numărul total de tranzacții efectuate
     */
    public int getNumOfTransactions() {
        return numOfTransactions;
    }

    /**
     * @return Numărul tranzacțiilor peste 300 RON
     */
    public int getNumOfTransactionsOver300RON() {
        return numOfTransactionsOver300RON;
    }

    /**
     * @return `true` dacă cashback-ul a fost câștigat, altfel `false`
     */
    public boolean isCahsbackEarned() {
        return cahsbackEarned;
    }

    /**
     * @return Suma acumulată pentru split
     */
    public double getAmountForSplit() {
        return amountForSplit;
    }

    /**
     * Marchează cashback-ul ca fiind câștigat.
     */
    public void cashbackEarned() {
        cahsbackEarned = true;
    }

    /**
     * Marchează cashback-ul ca nefiind câștigat.
     */
    public void cashbackNotEarned() {
        cahsbackEarned = false;
    }

    /**
     * Adaugă o sumă la valoarea acumulată pentru split.
     *
     * @param amount suma de adăugat
     */
    public void addAmountForSplit(final double amount) {
        if (amount <= this.balance) {
            amountForSplit += amount;
        }
    }

    /**
     * Scade o sumă din valoarea acumulată pentru split.
     *
     * @param amount suma de scăzut
     */
    public void decreaseAmountForSplit(final double amount) {
        amountForSplit -= amount;
    }

    /**
     * Crește numărul tranzacțiilor peste 300 RON.
     */
    public void increaseNumOfTransactionsOver300RON() {
        numOfTransactionsOver300RON++;
    }

    /**
     * Crește numărul total de tranzacții.
     */
    public void increaseNumberOfTransactions() {
        this.numOfTransactions++;
    }

    /**
     * Scade suma totală cheltuită din cont.
     *
     * @param amount suma scăzută
     */
    public void decreaseTotalSpent(final double amount) {
        this.totalSpent -= amount;
    }

    /**
     * Setează soldul minim permis al contului
     * @param minimumBalance este noua balanță minimă a contului
     */
    public void setMinimumBalance(final Double minimumBalance) {
        this.minimumBalance = minimumBalance;
    }

    /**
     * Depune o sumă de bani în cont
     * @param amount este suma de depus
     */
    public void deposit(final Double amount) {
        this.balance += amount;
    }

    /**
     * Retrage o sumă de bani din cont dacă soldul este suficient
     * @param amount este suma de retras
     * @return "Success" dacă operațiunea a reușit, altfel "Insufficient funds"
     */
    public String withdraw(final Double amount) {
        if (balance - amount < 0) {
            return "Insufficient funds";
        }
        this.balance -= amount;
        this.totalSpent += amount;
        return "Success";
    }

    /**
     * Adaugă o tranzacție la lista tranzacțiilor acestui cont
     * @param transaction este tranzacția de adăugat
     */
    public void addTransaction(final Transaction transaction) {
        this.transactions.add(transaction);
    }

    /**
     * Adaugă un card la lista de carduri asociate contului
     * @param card este cardul de adăugat
     */
    public void addCard(final Card card) {
        this.cards.add(card);
    }

    /**
     * @return Tipul contului, pentru clasa de bază, returnează null.
     */
    public String getAccountType() {
        return null;
    }

    /**
     * Metodă abstractă pentru schimbarea ratei dobânzii
     * @param newInterestRate este noua rată a dobânzii
     */
    public abstract void changeInterestRate(double newInterestRate);

    /**
     * Metodă abstractă pentru aplicarea dobânzii în cont
     */
    public abstract double addInterest();

    /**
     * Adaugă un angajat la cont.
     *
     * @param user utilizatorul care va fi adăugat ca angajat
     */
    public abstract void addEmployee(User user);

    /**
     * Adaugă un manager la cont.
     *
     * @param user utilizatorul care va fi adăugat ca manager
     */
    public abstract void addManager(User user);

    /**
     * Returnează limita de cheltuieli a contului.
     *
     * @return limita de cheltuieli
     */
    public abstract double getSpendingLimit();

    /**
     * Returnează limita de depozit a contului.
     *
     * @return limita de depozit
     */
    public abstract double getDepositLimit();

    /**
     * Verifică dacă un utilizator este angajat, pe baza adresei de email.
     *
     * @param email adresa de email a utilizatorului
     * @return `true` dacă utilizatorul este angajat, altfel `false`
     */
    public abstract boolean isEmployee(String email);

    /**
     * Modifică limita de depozit a contului.
     *
     * @param amount noua limită de depozit
     */
    public abstract void changeDepositLimit(double amount);

    /**
     * Modifică limita de cheltuieli a contului.
     *
     * @param amount noua limită de cheltuieli
     */
    public abstract void changeSpendingLimit(double amount);

    /**
     * Returnează lista managerilor asociați contului.
     *
     * @return lista managerilor
     */
    public abstract List<User> getManagers();

    /**
     * Returnează lista angajaților asociați contului.
     *
     * @return lista angajaților
     */
    public abstract List<User> getEmployees();
}
