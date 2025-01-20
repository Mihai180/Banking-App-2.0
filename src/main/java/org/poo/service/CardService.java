package org.poo.service;

import org.poo.exception.*;
import org.poo.model.account.Account;
import org.poo.model.card.Card;
import org.poo.model.card.OneTimePayCard;
import org.poo.model.card.RegularCard;
import org.poo.model.plan.PlanStrategy;
import org.poo.model.user.User;
import org.poo.utils.Utils;
import java.util.HashMap;
import java.util.Map;

/**
 * Clasa finală CardService gestionează operațiunile legate de carduri
 */
public final class CardService {
    // Instanța unică a clasei CardService
    private static CardService instance;
    private Map<String, Card> cardsByNumber = new HashMap<>();
    private UserService userService;
    private AccountService accountService;
    private ExchangeService exchangeService;

    // Constructor privat pentru a preveni instanțierea directă din exterior
    private CardService(final UserService userService,
                       final AccountService accountService,
                       final ExchangeService exchangeService) {
        this.userService = userService;
        this.accountService = accountService;
        this.exchangeService = exchangeService;
    }

    /**
     * Metodă statică pentru a obține instanța unică a clasei CardService
     * Dacă instanța nu există, aceasta este creată
     * @param userService este instanța UserService necesară pentru CardService
     * @param accountService este instanța AccountService necesară pentru CardService
     * @param exchangeService este instanța ExchangeService necesară pentru CardService
     * @return instanța unică a CardService
     */
    public static CardService getInstance(final UserService userService,
                                          final AccountService accountService,
                                          final ExchangeService exchangeService) {
        if (instance == null) {
            instance = new CardService(userService, accountService, exchangeService);
        }
        return instance;
    }

    /**
     * Metodă statică pentru a reseta instanța unică a clasei CardService
     * Folosită pentru resetarea instanței între teste
     */
    public static void resetInstance() {
        instance = null;
    }

    /**
     * Creează un card nou pentru un cont specificat
     * @param accountIBAN este IBAN-ul contului pentru care se creează cardul
     * @param cardType este tipul cardului
     * @param email este adresa de email a utilizatorului asociat cardului
     * @return cardul creat sau null dacă utilizatorul nu este autorizat
     * @throws AccountNotFoundException dacă contul cu IBAN-ul specificat nu este găsit
     */
    public Card createCard(final String accountIBAN, final String cardType, final String email) {
        Account account = accountService.getAccountByIBAN(accountIBAN);

        if (account == null) {
            throw new AccountNotFoundException("Account not found with IBAN: " + accountIBAN);
        }

        User user = userService.getUserByEmail(email);
        String cardNumber = Utils.generateCardNumber();
        Card card = null;
        if (cardType.equals("regularCard")) {
            card = new RegularCard(cardNumber, account, user);
        } else if (cardType.equals("oneTimeCard")) {
            card = new OneTimePayCard(cardNumber, account, user);
        }
        cardsByNumber.put(cardNumber, card);
        account.addCard(card);

        return card;
    }

    /**
     * Șterge un card asociat unui utilizator
     * @param cardNumber este numărul cardului care trebuie șters
     * @param email este adresa de email a utilizatorului asociat cardului
     */
    public void deleteCard(final String cardNumber, final String email) {
        Card card = cardsByNumber.get(cardNumber);
        if (card == null) {
            return;
        }

        cardsByNumber.remove(cardNumber);
        card.getAccount().getCards().remove(card);
    }

    /**
     * Efectuează o plată online folosind un card
     * @param cardNumber este cardul utilizat pentru plată
     * @param amount este suma de plată
     * @param currency este moneda în care este exprimată suma de plată
     * @param email este adresa de email a utilizatorului asociat cardului
     * @return mesaj de succes sau detalierea erorii
     * @throws CardNotFoundException dacă cardul nu este găsit
     * @throws FrozenCardException dacă cardul este blocat
     * @throws UnauthorizedAccessException dacă utilizatorul nu este autorizat să utilizeze cardul
     * @throws InsufficientFundsException dacă contul asociat cardului nu are fonduri suficiente
     * @throws CardIsUsedException dacă cardul a fost deja utilizat și nu mai poate fi folosit
     */
    public String payOnline(final String cardNumber, final double amount, final String currency,
                            final String email) {
        Card card = cardsByNumber.get(cardNumber);

        if (card == null) {
            throw new CardNotFoundException("Card not found");
        }

        if (card.isBlocked()) {
            throw new FrozenCardException("Card is frozen");
        }

        double finalAmount = amount;
        if (!card.getAccount().getCurrency().equals(currency)) {
            finalAmount = exchangeService.convertCurrency(currency,
                    card.getAccount().getCurrency(), amount);
        }

        if (card.getAccount().getBalance() < finalAmount) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        double amountInRon = exchangeService.convertCurrency(card.getAccount().getCurrency(),
                "RON", finalAmount);
        if (card.getAccount().getAccountType().equals("business")
                && amountInRon > card.getAccount().getSpendingLimit()
                && card.getAccount().isEmployee(email)) {
            throw new PaymentLimitExcedeedException("Payment limit exceeded");
        }

        User user = userService.getUserByEmail(email);
        if (card.getAccount().getAccountType().equals("business")) {
            user.addExpense(card.getAccount().getIban(), finalAmount);
        }

        String result = card.makePayment(finalAmount, cardsByNumber);

        if (result.equals("You can't pay this amount because is used")) {
            throw new CardIsUsedException("You can't pay this amount because is used");
        }

        // Pentru un card one time pay dupa ce i-am generat un nou numar după o plată cu succes
        // setez noul card ca fiind nefolosit
        if (result.equals("Success") && card.getCardType().equals("OneTimePayCard")) {
            String newCardNumber = card.getCardNumber();
            Card newCard = cardsByNumber.get(newCardNumber);

            if (newCard != null && newCard != card) {
                newCard.setIsUsed(false);
            }
            return "New card generated successfully: " + newCardNumber;
        }

        // Daca balanța minimă este null înseamnă că plata s-a efectuat cu succes
        // deoarece contul este clasic
        Double minBalance = card.getAccount().getMinimumBalance();
        if (minBalance == null) {
            return "Success";
        }

        return "Success";
    }

    /**
     * Obține un card după numărul acestuia
     * @param cardNumber este numărul cardului
     * @return cardul asociat numărului sau null dacă nu este găsit
     */
    public Card getCardByNumber(final String cardNumber) {
        return cardsByNumber.get(cardNumber);
    }

    /**
     * Verifică statusul unui card specificat
     * @param cardNumber este numărul cardului al cărui status se verifică
     * @return statusul cardului sau un mesaj dacă cardul nu este găsit
     */
    public String checkCardStatus(final String cardNumber) {
        // Iterez prin toți utilizatorii pentru a găsi cardul
        for (User user : userService.getAllUsers().values()) {
            for (Account account : user.getAccounts()) {
                for (Card actualcard : account.getCards()) {
                    if (actualcard.getCardNumber().equals(cardNumber)) {
                        Card card = actualcard;
                        if (card.getAccount().getBalance() == 0) {
                            return "Insufficient funds";
                        }
                        return card.checkStatus();
                    }
                }
            }
        }
        return "Card not found";
    }

    /**
     * Efectuează o operațiune de retragere de numerar folosind un card specific.
     *
     * @param cardNumber numărul cardului utilizat pentru retragere
     * @param amount     suma care urmează să fie retrasă (în RON, implicit)
     * @param email      email-ul utilizatorului asociat cardului
     * @return un mesaj de confirmare care indică succesul retragerii
     * @throws UserNotFoundException         dacă nu se găsește un utilizator cu email-ul furnizat
     * @throws CardNotFoundException         dacă numărul cardului este invalid sau
     * cardul este blocat/utilizat
     * @throws MinimumBalancePassedException dacă retragerea ar face ca soldul contului
     *                                       să scadă sub limita minimă
     * @throws InsufficientFundsException    dacă, contul nu are fonduri suficiente pentru retragere
     */
    public String cashWithdrawal(final String cardNumber, double amount, final String email) {
        User user = userService.getUserByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        Card card = cardsByNumber.get(cardNumber);
        if (card == null) {
            throw new CardNotFoundException("Card not found");
        }

        if (card.isBlocked()) {
            return "The card is frozen";
        }

        if (card.isUsed()) {
            throw new CardNotFoundException("Card not found");
        }

        Account account = card.getAccount();
        PlanStrategy plan = user.getCurrentPlan();
        double commission = plan.calculateCommission(amount);
        if (!account.getCurrency().equals("RON")) {
            amount = exchangeService.convertCurrency("RON", account.getCurrency(), amount);
            commission = exchangeService.convertCurrency("RON", account.getCurrency(), commission);
        }

        if (account.getMinimumBalance() != null
                && account.getBalance() - amount < account.getMinimumBalance()) {
            throw new MinimumBalancePassedException("Cannot perform "
                    + "payment due to a minimum balance being set");
        }

        if (amount > account.getBalance()) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        account.withdraw(amount);
        account.withdraw(commission);
        account.decreaseTotalSpent(commission);

        return "Cash withdrawal of" + amount + ".";
    }
}
