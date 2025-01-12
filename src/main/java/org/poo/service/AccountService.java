package org.poo.service;

import org.poo.exception.*;
import org.poo.model.account.Account;
import org.poo.model.account.ClassicAccount;
import org.poo.model.account.SavingsAccount;
import org.poo.model.commerciant.Commerciant;
import org.poo.model.plan.PlanFactory;
import org.poo.model.plan.PlanStrategy;
import org.poo.model.transaction.Transaction;
import org.poo.model.user.User;
import org.poo.utils.Utils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clasa finală AccountService gestionează operațiunile legate de conturi
 */
public final class AccountService {
    // Instanța unică a clasei AccountService
    private static AccountService instance;
    private Map<String, Account> accountsByIban = new HashMap<>();
    private UserService userService;
    private ExchangeService exchangeService;

    // Constructor privat pentru a preveni instanțierea directă din exterior
    private AccountService(final UserService userService, final ExchangeService exchangeService) {
        this.userService = userService;
        this.exchangeService = exchangeService;
    }

    /**
     * Metodă statică pentru a obține instanța unică a clasei AccountService
     * Dacă instanța nu există, aceasta este creată
     * @param userService este instanța UserService necesară pentru AccountService
     * @param exchangeService este instanța ExchangeService necesară pentru AccountService
     * @return instanța unică a AccountService
     */
    public static AccountService getInstance(final UserService userService,
                                             final ExchangeService exchangeService) {
        if (instance == null) {
            instance = new AccountService(userService, exchangeService);
        }
        return instance;
    }

    /**
     * Metodă statică pentru a reseta instanța unică a clasei AccountService
     * Folosită pentru resetarea instanței între teste
     */
    public static void resetInstance() {
        instance = null;
    }

    /**
     * Creează un cont nou pentru un utilizator specificat prin email
     * @param email este adresa de email a utilizatorului pentru care se creează contul
     * @param accountType este tipul contului
     * @param currency este moneda în care este exprimat contul
     * @param interestRate este rata dobânzii pentru conturile de economii
     * @return contul creat
     * @throws UserNotFoundException dacă utilizatorul cu email-ul specificat nu este găsit
     */
    public Account createAccount(final String email, final String accountType,
                                 final String currency, final Double interestRate) {
        User user = userService.getUserByEmail(email);

        if (user == null) {
            throw new UserNotFoundException("User not found with email: " + email);
        }

        String iban = Utils.generateIBAN();
        Account account;
        if (accountType.equals("savings")) {
            account = new SavingsAccount(iban, user, currency, interestRate);
        } else {
            account = new ClassicAccount(iban, user, currency);
        }

        user.addAccount(account);
        accountsByIban.put(iban, account);
        return account;
    }

    /**
     * Adaugă fonduri la un cont specificat prin IBAN
     * @param iban este IBAN-ul contului în care se vor adăuga fondurile
     * @param amount este suma de adăugat
     * @throws AccountNotFoundException dacă contul cu IBAN-ul specificat nu este găsit
     */
    public void addFunds(final String iban, final Double amount) {
        Account account = getAccountByIBAN(iban);
        if (account == null) {
            throw new AccountNotFoundException("Account not found with IBAN: from addFunds "
                    + iban);
        }
        account.deposit(amount);
    }

    /**
     * Șterge un cont asociat unui utilizator, dacă soldul contului este zero
     * @param iban este IBAN-ul contului care trebuie șters
     * @param email este adresa de email a utilizatorului asociat contului
     * @throws UserNotFoundException dacă utilizatorul cu email-ul specificat nu este găsit
     * @throws AccountNotFoundException dacă contul cu IBAN-ul specificat nu este găsit în
     * conturile utilizatorului
     * @throws AccountCanNotBeDeletedException dacă contul are un sold diferit de zero și
     * nu poate fi șters
     */
    public void deleteAccount(final String iban, final String email) {
        User user = userService.getUserByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found with email: " + email);
        }

        Account accountToRemove = null;
        for (Account account : user.getAccounts()) {
            if (account.getIban().equals(iban)) {
                accountToRemove = account;
                break;
            }
        }

        if (accountToRemove == null) {
            throw new AccountNotFoundException("Account not found with IBAN: from deleteAccount"
                    + iban);
        }

        if (accountToRemove.getBalance() != 0.0) {
            throw new AccountCanNotBeDeletedException("Account couldn't be deleted -"
                    + " see org.poo.transactions for details");
        }

        user.getAccounts().remove(accountToRemove);
        accountsByIban.remove(iban);
    }

    /**
     * Returnează contul asociat unui IBAN specificat
     * @param iban este IBAN-ul contului
     * @return Contul asociat IBAN-ului sau null dacă nu este găsit
     */
    public Account getAccountByIBAN(final String iban) {
        return accountsByIban.get(iban);
    }

    /**
     * Setează soldul minim pentru un cont specificat prin IBAN
     * @param accountIBAN este IBAN-ul contului pentru care se setează soldul minim
     * @param minBalance este soldul minim care trebuie menținut
     * @throws AccountNotFoundException dacă contul cu IBAN-ul specificat nu este găsit
     */
    public void setMinBalance(final String accountIBAN, final double minBalance) {
        Account account = getAccountByIBAN(accountIBAN);
        account.setMinimumBalance(minBalance);
    }

    /**
     * Transferă bani de la un cont la altul, posibil cu conversie valutară
     * @param senderIban este IBAN-ul contului expeditor
     * @param amount este suma de transferat
     * @param receiverAliasOrIBAN este alias-ul sau IBAN-ul contului destinatar
     * @return mesaj de succes dacă transferul a avut loc cu succes
     * @throws AccountNotFoundException dacă contul expeditor sau destinatar nu este găsit
     * @throws InsufficientFundsException dacă contul expeditor nu are fonduri suficiente
     */
    public String sendMoney(final String senderIban, final double amount,
                            final String receiverAliasOrIBAN) {
        String receiverIban = resolveAliasOrIBAN(receiverAliasOrIBAN);
        if (receiverIban == null) {
            throw new AccountNotFoundException("Receiver account not found or invalid alias.");
        }

        Account senderAccount = getAccountByIBAN(senderIban);
        Account receiverAccount = getAccountByIBAN(receiverIban);

        if (senderAccount == null) {
            throw new AccountNotFoundException("Sender account not found.");
        }

        if (receiverAccount == null) {
            throw new AccountNotFoundException("Receiver account not found.");
        }

        double convertedAmount = exchangeService.convertCurrency(
                senderAccount.getCurrency(),
                receiverAccount.getCurrency(),
                amount
        );

        if (senderAccount.getBalance() < amount || senderAccount.getBalance() - senderAccount.getAmountForSplit() < amount) {
            throw new InsufficientFundsException("Insufficient funds in sender's account");
        }

        senderAccount.withdraw(amount);
        receiverAccount.deposit(convertedAmount);
        return "Success";
    }

    /**
     * Transformă un alias (dacă există) în IBAN-ul corespunzător
     * @param aliasOrIban este alias-ul sau IBAN-ul care trebuie rezolvat
     * @return IBAN-ul corespunzător alias-ului sau IBAN-ul dacă nu există alias
     */
    private String resolveAliasOrIBAN(final String aliasOrIban) {
        for (User user : userService.getAllUsers().values()) {
            if (user.getAliases().containsKey(aliasOrIban)) {
                return user.getAliases().get(aliasOrIban);
            }
        }
        return aliasOrIban;
    }

    /**
     * Schimbă rata dobânzii pentru un cont de economii specificat prin IBAN
     * @param iban este IBAN-ul contului
     * @param interestRate este noua rată a dobânzii
     * @return mesaj de succes dacă schimbarea a avut loc cu succes sau un mesaj de eroare
     * dacă contul nu este de economii
     * @throws AccountNotFoundException dacă contul cu IBAN-ul specificat nu este găsit
     */
    public String changeInterestRate(final String iban, final Double interestRate) {
        Account account = getAccountByIBAN(iban);
        if (account == null) {
            throw new AccountNotFoundException("Account not found with IBAN: " + iban);
        }
        if (account.getAccountType().equals("classic")) {
            return "This is not a savings account";
        }
        if (account.getAccountType().equals("savings")) {
            account.changeInterestRate(interestRate);
        }
        return "Success";
    }

    /**
     * Efectuează o plată împărțită între mai multe conturi
     * @param accounts este lista de IBAN-uri ale conturilor între care se va face plata
     * @param currency este moneda în care este exprimată suma totală a plății
     * @param amount este suma totală a plății care trebuie împărțită
     * @return mesaj de succes dacă plata a fost împărțită cu succes sau un mesaj de eroare
     * dacă un cont are fonduri insuficiente
     * @throws AccountNotFoundException dacă unul dintre conturile specificate nu este găsit
     */
    public String splitPayment(final List<String> accounts, final String currency,
                               final double amount, final String type, final List<Double> amountForUsers) {
        if (type.equals("equal")) {
            int nrOfAccounts = accounts.size();
            double splitAmount = amount / nrOfAccounts;
            String lastIban = null;
            for (String iban : accounts) {
                Account account = getAccountByIBAN(iban);
                if (account == null) {
                    throw new AccountNotFoundException("Account not found with IBAN: " + iban);
                }
                double convertedAmount = splitAmount;
                if (!account.getCurrency().equals(currency)) {
                    convertedAmount = exchangeService.convertCurrency(currency,
                            account.getCurrency(), splitAmount);
                }
                if (account.getBalance() < convertedAmount) {
                    lastIban = iban;
                }
            }
            if (lastIban != null) {
                return "Account " + lastIban + " has insufficient funds for a split payment.";
            }
            for (String iban : accounts) {
                Account account = getAccountByIBAN(iban);
                if (account != null) {
                    double convertedAmount = splitAmount;
                    if (!account.getCurrency().equals(currency)) {
                        convertedAmount = exchangeService.convertCurrency(currency,
                                account.getCurrency(),
                                splitAmount);
                    }
                    account.withdraw(convertedAmount);
                }
            }
            return "Success";
        }
        else if (type.equals("custom")) {
        String lastIban = null;
        for (int i = 0; i < accounts.size(); i++) {
            Account account = getAccountByIBAN(accounts.get(i));
            double amountForUser = amountForUsers.get(i);
            if (account == null) {
                throw new AccountNotFoundException("Account not found with IBAN: " + accounts.get(i));
            }
            if (!account.getCurrency().equals(currency)) {
                amountForUser = exchangeService.convertCurrency(currency,
                        account.getCurrency(), amountForUser);
            }
            account.withdraw(amountForUser);
            }
        return "Success";
        }
        return "Error";
    }

    /**
     * Returnează lista de tranzacții pentru un cont specificat prin IBAN
     * @param iban este IBAN-ul contului pentru care se solicită tranzacțiile
     * @return lista de tranzacții asociate contului
     * @throws AccountNotFoundException dacă contul cu IBAN-ul specificat nu este găsit
     */
    public List<Transaction> getTransactions(final String iban) {
        Account account = getAccountByIBAN(iban);
        if (account == null) {
            throw new AccountNotFoundException("Account not found with IBAN: " + iban);
        }
        return account.getTransactions();
    }

    /**
     * Adaugă dobândă la un cont de economii specificat prin IBAN
     * @param iban este IBAN-ul contului la care se adaugă dobânda
     * @return Mmsaj de succes dacă dobânda a fost adăugată sau un
     * mesaj de eroare dacă contul nu este de economii
     */
    public String addInterestRate(final String iban) {
        Account account = getAccountByIBAN(iban);
        if (account == null) {
            return "Account not found with IBAN: " + iban;
        }
        if (account.getAccountType().equals("classic")) {
            return "This is not a savings account";
        }
        double amount = account.addInterest();
        if (amount < 0) {
            return null;
        }
        return "Success: " + amount;
    }

    private Account findClassicAccountByCurrency(User user, String currency) {
        for (Account account : user.getAccounts()) {
            if ("classic".equals(account.getAccountType()) && account.getCurrency().equals(currency)) {
                return account;
            }
        }
        return null;
    }

    public String withdrawSavings(final String iban, final double amount, final String currency) {
        Account account = getAccountByIBAN(iban);
        if (account == null) {
            throw new AccountNotFoundException("Account not found");
        }

        User owner = account.getOwner();
        Account classicAccount = findClassicAccountByCurrency(owner, currency);

        if (classicAccount == null) {
            throw new NotClassicAccountException("You do not have a classic account.");
        }

        if (!owner.isUserOldEnough()) {
            throw new NotMinimumAgeRequired("You don't have the minimum age required. " + classicAccount.getIban());
        }

        if (!account.getAccountType().equals("savings")) {
            throw new AccountTypeIsNotSavings("Account is not of type savings.");
        }

        double convertedAmount = exchangeService.convertCurrency(account.getCurrency(),
                classicAccount.getCurrency(), amount);

        if (account.getBalance() < convertedAmount) {
            throw new InsufficientFundsException("Insufficient funds");
        }
        account.withdraw(amount);
        classicAccount.deposit(convertedAmount);

        return "Savings withdrawal to " + classicAccount.getIban();
    }

    public void upgradePlan(final String iban, final String NewPlanType) {
        Account account = getAccountByIBAN(iban);
        if (account == null) {
            throw new AccountNotFoundException("Account not found with IBAN: " + iban);
        }

        User owner = account.getOwner();
        if (owner == null) {
            throw new UserNotFoundException("User not found");
        }

        PlanStrategy currentPlan = owner.getCurrentPlan();

        if (currentPlan.equals(NewPlanType)) {
            throw new SamePlanException("The user already has the " + NewPlanType + " plan.");
        }
        PlanStrategy requestedPlan = PlanFactory.getPlan(NewPlanType);

        if (currentPlan.isDowngrade(NewPlanType)) {
            throw new PlanDowngradeException("You cannot downgrade your plan.");
        }

        if (currentPlan.calculateUpgradeFee(NewPlanType) == -1) {
            throw new UnknownPlanException("Unknown plan.");
        }

        double fee = currentPlan.calculateUpgradeFee(NewPlanType);
        if (!account.getCurrency().equals("RON")) {
            fee = exchangeService.convertCurrency("RON", account.getCurrency(), fee);
        }

        if (account.getBalance() < fee) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        account.withdraw(fee);
        owner.setCurrentPlan(requestedPlan);
    }
}
