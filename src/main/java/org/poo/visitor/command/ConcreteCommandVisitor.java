package org.poo.visitor.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.command.*;
import org.poo.exception.*;
import org.poo.model.account.Account;
import org.poo.model.card.Card;
import org.poo.model.commerciant.CashbackStrategy;
import org.poo.model.commerciant.CashbackStrategyFactory;
import org.poo.model.commerciant.Commerciant;
import org.poo.model.plan.GoldPlan;
import org.poo.model.plan.PlanStrategy;
import org.poo.model.transaction.*;
import org.poo.model.user.User;
import org.poo.service.*;
import org.poo.service.CommerciantService;
import org.poo.visitor.transaction.ConcreteTransactionVisitor;
import java.util.*;


/**
 * Clasa ConcreteCommandVisitor implementează interfața CommandVisitor
 * și oferă implementări concrete pentru fiecare tip de comandă
 */
public final class ConcreteCommandVisitor implements CommandVisitor {
    // Serviciile utilizate pentru gestionarea diferitelor aspecte ale operațiunilor bancare
    private final UserService userService;
    private final AccountService accountService;
    private final CardService cardService;
    private final TransactionService transactionService;
    private final ExchangeService exchangeService;
    private final CommerciantService commerciantService;
    // Nodurile JSON pentru a stoca rezultatele comenzilor
    private final ArrayNode output;
    private final ObjectMapper mapper;
    // Constantă pentru precizia zecimală
    //private static final int DECIMAL_PRECISION = 14;
    private final Map<Integer, SplitPaymentCommand> pendingSplits = new HashMap<>();
    private final Map<Integer, Map<String, Boolean>> acceptanceMaps = new HashMap<>();
    private final Map<String, Deque<Integer>> userPendingSplits = new HashMap<>();
    //Map<Account, Double> accountAmounts = new HashMap<>();

    public ConcreteCommandVisitor(final UserService userService,
                                  final AccountService accountService,
                                  final CardService cardService,
                                  final TransactionService transactionService,
                                  final ExchangeService exchangeService,
                                  final CommerciantService commerciantService,
                                  final ArrayNode output,
                                  final ObjectMapper mapper) {
        this.userService = userService;
        this.accountService = accountService;
        this.cardService = cardService;
        this.transactionService = transactionService;
        this.exchangeService = exchangeService;
        this.commerciantService = commerciantService;
        this.output = output;
        this.mapper = mapper;
    }

    /**
     * Metodă auxiliară pentru a crea un rezultat de tip "not found" în JSON.
     *
     * @param commandName este numele comenzii.
     * @param description este descrierea erorii.
     * @param timestamp este timestamp-ul la care a avut loc comanda.
     */
    private void notFoundResult(final String commandName, final String description,
                                final int timestamp) {
        ObjectNode cmdResult = mapper.createObjectNode();
        cmdResult.put("command", commandName);
        ObjectNode outputNode = mapper.createObjectNode();
        outputNode.put("description", description);
        outputNode.put("timestamp", timestamp);
        cmdResult.set("output", outputNode);
        cmdResult.put("timestamp", timestamp);
        this.output.add(cmdResult);
    }

    /**
     * Metodă auxiliară pentru a crea un nod JSON de raport pentru un cont specific.
     *
     * @param account este contul pentru care se generează raportul.
     * @param transactions este lista de tranzacții asociate contului.
     * @param startTimestamp este timestamp-ul de început pentru filtrarea tranzacțiilor.
     * @param endTimestamp este timestamp-ul de sfârșit pentru filtrarea tranzacțiilor.
     * @param transactionType este tipul tranzacțiilor de filtrat (poate fi null).
     * @param commerciantsTotals este maparea comerciantului la totalurile cheltuielilor
     * (poate fi null).
     * @return este nodul JSON care reprezintă raportul.
     */
    private ObjectNode createReportOutputNode(final Account account,
                                              final List<Transaction> transactions,
                                              final long startTimestamp,
                                              final long endTimestamp,
                                              final String transactionType,
                                              final Map<String, Double> commerciantsTotals) {
        ObjectNode outputNode = mapper.createObjectNode();
        outputNode.put("IBAN", account.getIban());
        outputNode.put("balance", account.getBalance());
        outputNode.put("currency", account.getCurrency());

        ArrayNode transactionsArray = mapper.createArrayNode();
        for (Transaction transaction : transactions) {
            // Filtrează tranzacțiile în funcție de intervalul de timp și tipul tranzacției
            if (transaction.getTimestamp() >= startTimestamp
                    && transaction.getTimestamp() <= endTimestamp
            && (transactionType == null || transaction.getType().equals(transactionType))) {
                ObjectNode transactionNode = mapper.createObjectNode();
                // Creează un vizitator pentru tranzacție pentru a genera detaliile JSON
                ConcreteTransactionVisitor transactionVisitor =
                        new ConcreteTransactionVisitor(transactionNode, mapper);
                transaction.accept(transactionVisitor);
                transactionsArray.add(transactionNode);
                // Dacă tranzacția este de tip "CardPayment", actualizează totalul cheltuielilor
                // pe comerciant
                if ("CardPayment".equals(transaction.getType()) && commerciantsTotals != null) {
                    String commerciant = transaction.getPaymentCommerciant();
                    double amount = transaction.getPaymentAmount();
                    commerciantsTotals.merge(commerciant, amount, Double::sum);
                }
            }
        }
        outputNode.set("transactions", transactionsArray);
        return outputNode;
    }

    /**
     * Metodă auxiliară pentru a crea un rezultat de tip "not a savings account" în JSON.
     *
     * @param commandName este numele comenzii.
     * @param description este descrierea erorii.
     * @param timestamp este timestamp-ul la care a avut loc comanda.
     */
    private void notSavingsAccountResult(final String commandName, final String description,
                                         final long timestamp) {
        ObjectNode cmdResult = mapper.createObjectNode();
        cmdResult.put("command", commandName);
        ObjectNode outputNode = mapper.createObjectNode();
        outputNode.put("timestamp", timestamp);
        outputNode.put("description", description);
        cmdResult.set("output", outputNode);
        cmdResult.put("timestamp", timestamp);
        this.output.add(cmdResult);
    }

    /**
     * Metodă auxiliară pentru a efectua conversia valutară în contextul unei plăți online,
     * aplicarea cashback-ului dacă este cazul și a comisionului.
     *
     * @param command este comanda de plată online.
     * @param associatedAccount este contul asociat cardului utilizat pentru plată.
     */
    private void convert(final PayOnlineCommand command, final Account associatedAccount) {
        // Conversia folosind serviciul de schimb valutar
        double convertedAmount = exchangeService.convertCurrency(command.getCurrency(),
                associatedAccount.getCurrency(), command.getAmount());
        if (command.getAmount() != 0) {
            CardPaymentTransaction transaction = new CardPaymentTransaction(command.getTimestamp(),
                    command.getCommerciant(), convertedAmount);

            Commerciant commerciant =
                    CommerciantService.getCommerciantByName(command.getCommerciant());
            double spentInRON =
                    exchangeService.convertCurrency(associatedAccount.getCurrency(),
                            "RON", convertedAmount);
            User user = associatedAccount.getOwner();
            PlanStrategy plan = user.getCurrentPlan();
            if (spentInRON >= 300 && plan.getPlan().equals("Silver")) {
                associatedAccount.increaseNumOfTransactionsOver300RON();
            }

            if (plan.getPlan().equals("Silver")
                    && associatedAccount.getNumOfTransactionsOver300RON() >= 5) {
                PlanStrategy newPlan = new GoldPlan();
                user.setCurrentPlan(newPlan);
            }

            plan = user.getCurrentPlan();

            CashbackStrategy cashbackStrategy =
                    CashbackStrategyFactory.getStrategy(commerciant.getCommerciant());

            if (!associatedAccount.isCahsbackEarned()) {
                double amount = cashbackStrategy.calculateCashback(associatedAccount, transaction);
                associatedAccount.deposit(amount);
                associatedAccount.cashbackEarned();

                double commission = plan.calculateCommission(spentInRON);
                double convertedCommission = exchangeService.convertCurrency("RON",
                        associatedAccount.getCurrency(), commission);
                associatedAccount.withdraw(convertedCommission);
                associatedAccount.decreaseTotalSpent(convertedCommission);

                if (cashbackStrategy.getCashbackType().equalsIgnoreCase("SpendingTreshold")) {
                    associatedAccount.cashbackNotEarned();
                }

                associatedAccount.addTransaction(transaction);
            } else {
                double commission = plan.calculateCommission(spentInRON);
                double convertedCommission = exchangeService.convertCurrency("RON",
                        associatedAccount.getCurrency(), commission);
                associatedAccount.withdraw(convertedCommission);
                associatedAccount.decreaseTotalSpent(convertedCommission);
                associatedAccount.addTransaction(transaction);
            }
        }
    }

    /**
     * Execută comanda de afișare a utilizatorilor și generează un nod JSON
     * cu detaliile acestora.
     *
     * @param command este comanda PrintUsersCommand care trebuie procesată.
     */
    @Override
    public void visit(final PrintUsersCommand command) {
        ObjectNode cmdResult = mapper.createObjectNode();
        cmdResult.put("command", command.getCommandName());
        ArrayNode outputUsers = mapper.createArrayNode();
        // Iterează prin toți utilizatorii și adaugă detaliile fiecăruia în JSON
        for (User user : userService.getAllUsers().values()) {
            ObjectNode userNode = mapper.createObjectNode();
            userNode.put("firstName", user.getFirstName());
            userNode.put("lastName", user.getLastName());
            userNode.put("email", user.getEmail());
            ArrayNode accountsArray = mapper.createArrayNode();
            // Iterează prin conturile utilizatorului
            for (Account account : user.getAccounts()) {
                ObjectNode accountNode = mapper.createObjectNode();
                accountNode.put("IBAN", account.getIban());
                accountNode.put("balance", account.getBalance());
                accountNode.put("currency", account.getCurrency());
                String accountType = account.getAccountType();
                accountNode.put("type", accountType);
                ArrayNode cardsArray = mapper.createArrayNode();
                // Iterează prin cardurile asociate contului
                for (Card card : account.getCards()) {
                    ObjectNode cardNode = mapper.createObjectNode();
                    cardNode.put("cardNumber", card.getCardNumber());
                    cardNode.put("status", card.checkStatus());
                    cardsArray.add(cardNode);
                }
                accountNode.set("cards", cardsArray);
                accountsArray.add(accountNode);
            }
            userNode.set("accounts", accountsArray);
            outputUsers.add(userNode);
        }
        cmdResult.set("output", outputUsers);
        this.output.add(cmdResult);
        cmdResult.put("timestamp", command.getTimestamp());
    }

    /**
     * Execută comanda de adăugare a unui cont nou și înregistrează tranzacția
     * de creare a contului.
     *
     * @param command este comanda AddAccountCommand care trebuie procesată.
     */
    @Override
    public void visit(final AddAccountCommand command) {
        // Creează un cont nou utilizând serviciul de conturi
        Account newAccount = accountService.createAccount(command.getEmail(),
                command.getAccountType(), command.getCurrency(), command.getInterestRate());
        if (newAccount != null) {
            // Adaugă o tranzacție de creare a contului
            Transaction transaction = new AccountCreationTransaction(command.getTimestamp());
            newAccount.addTransaction(transaction);
        }
    }

    /**
     * Execută comanda de adăugare a fondurilor într-un cont specificat.
     *
     * @param command este comanda AddFundsCommand care trebuie procesată.
     */
    @Override
    public void visit(final AddFundsCommand command) {
        // Adaugă fonduri într-un cont specificat
        try {
            accountService.addFunds(command.getAccountIBAN(),
                    command.getAmount(), command.getEmail());
        } catch (DepositLimitExcedeedException exception) {

        }
    }

    /**
     * Execută comanda de creare a unui card nou și înregistrează tranzacția de creare a cardului.
     *
     * @param command este comanda CreateCardCommand care trebuie procesată.
     */
    @Override
    public void visit(final CreateCardCommand command) {
        // Creează un card nou utilizând serviciul de carduri
        Card result = cardService.createCard(command.getAccountIBAN(), command.getCardType(),
                command.getEmail());
        if (result != null) {
            // Adaugă o tranzacție de creare a cardului
            Transaction transaction = new CardCreationTransaction(command.getTimestamp(),
                    command.getEmail(), command.getAccountIBAN(), result.getCardNumber());
            accountService.getAccountByIBAN(command.getAccountIBAN()).addTransaction(transaction);
        }
    }

    /**
     * Generează un mesaj corespunzător în JSON, cu care se identifică ușor ce
     * comandă nu a fost implementată încă
     *
     * @param command este comanda NotImplementedCommand care trebuie procesată.
     */
    @Override
    public void visit(final NotImplementedCommand command) {
        ObjectNode resultNode = mapper.createObjectNode();
        resultNode.put("result", "Command '" + command.getCommandName()
                + "' at timestamp " + command.getTimestamp() + " is not implemented yet.");
        output.add(resultNode);
    }

    /**
     * Execută comanda de ștergere a unui cont și gestionează rezultatul în JSON.
     *
     * @param command este comanda DeleteAccountCommand care trebuie procesată.
     */
    @Override
    public void visit(final DeleteAccountCommand command) {
        ObjectNode cmdResult = mapper.createObjectNode();
        cmdResult.put("command", command.getCommandName());

        ObjectNode outputNode = mapper.createObjectNode();
        try {
            // Încearcă să șteargă contul utilizând serviciul de conturi
            accountService.deleteAccount(command.getAccount(), command.getEmail());

            // Dacă ștergerea este reușită, adaugă un mesaj de succes în JSON
            outputNode.put("success", "Account deleted");
            outputNode.put("timestamp", command.getTimestamp());
        } catch (Exception e) {
            // Dacă apare o eroare, adaugă mesajul de eroare în JSON și
            // înregistrează o tranzacție de eroare
            outputNode.put("error", e.getMessage());
            outputNode.put("timestamp", command.getTimestamp());
            Transaction transaction = new AccountDeletionErrorTransaction(command.getTimestamp());
            accountService.getAccountByIBAN(command.getAccount()).addTransaction(transaction);
        }

        cmdResult.set("output", outputNode);
        this.output.add(cmdResult);
        cmdResult.put("timestamp", command.getTimestamp());
    }

    /**
     * Execută comanda de ștergere a unui card și înregistrează tranzacția de ștergere
     * a cardului în contul asociat.
     *
     * @param command este comanda DeleteCardCommand care trebuie procesată.
     */
    @Override
    public void visit(final DeleteCardCommand command) {
        Card card = cardService.getCardByNumber(command.getCardNumber());
        if (card != null) {
            String iban = card.getAccount().getIban();
            if (iban != null) {
                // Creează o tranzacție de ștergere a cardului
                Transaction transaction = new CardDeletionTransaction(command.getTimestamp(),
                        command.getCardNumber(), command.getEmail(), iban);
                accountService.getAccountByIBAN(iban).addTransaction(transaction);
            }
        }
        cardService.deleteCard(command.getCardNumber(), command.getEmail());
    }

    /**
     * Execută comanda de setare a soldului minim pentru un cont specificat.
     *
     * @param command este comanda SetMinBalanceCommand care trebuie procesată.
     */
    @Override
    public void visit(final SetMinBalanceCommand command) {
        // Setează soldul minim pentru un cont specificat
        accountService.setMinBalance(command.getAccountIban(), command.getMinBalance());
    }

    /**
     * Execută comanda de plată online, efectuează conversia valutară dacă este
     * necesar și gestionează tranzacțiile corespunzătoare.
     *
     * @param command este comanda PayOnlineCommand care trebuie procesată.
     */
    @Override
    public void visit(final PayOnlineCommand command) {
        try {
            // Încearcă să efectueze o plată online utilizând serviciul de carduri
            String result = cardService.payOnline(command.getCardNumber(),
                    command.getAmount(),
                    command.getCurrency(),
                    command.getEmail());

            Card card = cardService.getCardByNumber(command.getCardNumber());
            if (card != null) {
                Account associatedAccount = card.getAccount();

                if (result.equals("Success")) {
                    // Dacă plata a fost reușită, efectuează conversia
                    // valutară și adaugă tranzacția
                    convert(command, associatedAccount);
                }

                if (result.startsWith("New card generated successfully")) {
                    // Dacă plata a generat un nou card, înregistrează
                    // tranzacțiile de ștergere a vechiului card și de creare a celui nou
                    String[] resultParts = result.split(": ");
                    if (resultParts.length > 1) {
                        String newCardNumber = resultParts[1];
                        convert(command, associatedAccount);

                        Transaction transaction1 = new CardDeletionTransaction(
                                command.getTimestamp(), command.getCardNumber(),
                                command.getEmail(), associatedAccount.getIban());
                        associatedAccount.addTransaction(transaction1);

                        Transaction transaction2 = new CardCreationTransaction(
                                command.getTimestamp(), command.getEmail(),
                                associatedAccount.getIban(), newCardNumber);
                        associatedAccount.addTransaction(transaction2);
                    }
                }
            }

        } catch (Exception e) {
            String errorMessage = e.getMessage();

            // Gestionează erorile specifice care apar în timpul plății online
            if (errorMessage.equals("Card not found")
                    || errorMessage.equals("You can't pay this amount because is used")) {

                ObjectNode cmdResult = mapper.createObjectNode();
                cmdResult.put("command", command.getName());

                ObjectNode outputNode = mapper.createObjectNode();
                outputNode.put("timestamp", command.getTimestamp());
                outputNode.put("description", "Card not found");

                cmdResult.set("output", outputNode);
                this.output.add(cmdResult);
                cmdResult.put("timestamp", command.getTimestamp());
            } else {
                // Alte tipuri de erori
                if (errorMessage.equals("Insufficient funds")) {
                    Card card = cardService.getCardByNumber(command.getCardNumber());
                    if (card != null) {
                        Account associatedAccount = card.getAccount();
                        // Înregistrează o tranzacție de fonduri insuficiente
                        Transaction transaction =
                                new InsufficientFundsTransaction(command.getTimestamp());
                        associatedAccount.addTransaction(transaction);
                    }
                } else if (errorMessage.equals("Card is frozen")) {
                    Card card = cardService.getCardByNumber(command.getCardNumber());
                    if (card != null) {
                        Account associatedAccount = card.getAccount();
                        // Înregistrează o tranzacție de card blocat
                        Transaction transaction = new FrozenCardTransaction(command.getTimestamp());
                        associatedAccount.addTransaction(transaction);
                    }
                }
            }
        }
    }

    /**
     * Execută comanda de transfer de bani între conturi și înregistrează
     * tranzacțiile corespunzătoare.
     *
     * @param command este comanda SendMoneyCommand care trebuie procesată.
     */
    @Override
    public void visit(final SendMoneyCommand command) {
        try {
            // Încearcă să trimită bani utilizând serviciul de conturi
            String result = accountService.sendMoney(command.getAccount(), command.getAmount(),
                    command.getReciever(), command.getEmail());

            // Dacă transferul este reușit și contul expeditor există,
            // înregistrează tranzacția de trimitere
            if (result.equals("Success")
                    && accountService.getAccountByIBAN(command.getAccount()) != null) {
                String currency =
                        accountService.getAccountByIBAN(command.getAccount()).getCurrency();
                Transaction transaction = new SendMoneyTransaction(command.getTimestamp(),
                        command.getDescription(), command.getAccount(), command.getReciever(),
                        command.getAmount(), currency, "sent");
                accountService.getAccountByIBAN(command.getAccount()).addTransaction(transaction);
                Account account = accountService.getAccountByIBAN(command.getAccount());
                User user = account.getOwner();
                PlanStrategy plan = user.getCurrentPlan();
                double convertedAmount = exchangeService.convertCurrency(account.getCurrency(),
                        "RON", command.getAmount());
                double commission = plan.calculateCommission(convertedAmount);
                double convertedCommission = exchangeService.convertCurrency("RON",
                        account.getCurrency(), commission);
                account.withdraw(convertedCommission);
                account.decreaseTotalSpent(convertedCommission);
                double spentInRON = exchangeService.convertCurrency(account.getCurrency(),
                        "RON", command.getAmount());
                account.increaseNumberOfTransactions();
                if (spentInRON >= 300) {
                    account.increaseNumOfTransactionsOver300RON();
                }
            }

            // Dacă transferul este reușit și contul destinatar există, efectuează conversia
            // valutară și înregistrează tranzacția de primire
            if (result.equals("Success")) {
                String currency =
                        accountService.getAccountByIBAN(command.getReciever()).getCurrency();
                double convertedAmount =
                        exchangeService.convertCurrency(accountService.getAccountByIBAN(
                                        command.getAccount()).getCurrency(), currency,
                                command.getAmount());
                Transaction transaction =
                        new SendMoneyTransaction(command.getTimestamp(),
                                command.getDescription(), command.getAccount(),
                                command.getReciever(),
                                convertedAmount, currency, "received");
                accountService.getAccountByIBAN(command.getReciever()).addTransaction(transaction);
            }

        } catch (Exception e) {
            String errorMessage = e.getMessage();
            // Gestionează eroarea de fonduri insuficiente
            if ("Insufficient funds in sender's account".equals(errorMessage)) {
                Account senderAccount = accountService.getAccountByIBAN(command.getAccount());
                if (senderAccount != null) {
                    // Înregistrează o tranzacție de fonduri insuficiente
                    Transaction transaction =
                            new InsufficientFundsTransaction(command.getTimestamp());
                    senderAccount.addTransaction(transaction);
                }
            } else if (errorMessage.equals("Sender account not found.")
                    || errorMessage.equals("Receiver account not found.")) {
                ObjectNode cmdResult = mapper.createObjectNode();
                cmdResult.put("command", "sendMoney");
                ObjectNode outputNode = mapper.createObjectNode();
                outputNode.put("timestamp", command.getTimestamp());
                outputNode.put("description", "User not found");
                cmdResult.set("output", outputNode);
                this.output.add(cmdResult);
                cmdResult.put("timestamp", command.getTimestamp());
            }
        }
    }

    /**
     * Execută comanda de setare a unui alias pentru un cont specificat.
     *
     * @param command este comanda SetAliasCommand care trebuie procesată.
     */
    @Override
    public void visit(final SetAliasCommand command) {
        // Setează un alias pentru un cont specificat
        userService.setAlias(command.getEmail(), command.getAlias(), command.getAccount());
    }

    /**
     * Execută comanda de afișare a tranzacțiilor unui utilizator și generează
     * un nod JSON cu detaliile acestora.
     *
     * @param command este comanda PrintTransactionsCommand care trebuie procesată.
     */
    @Override
    public void visit(final PrintTransactionsCommand command) {
        ObjectNode cmdResult = mapper.createObjectNode();
        cmdResult.put("command", command.getCommand());

        ArrayNode outputTransactions = mapper.createArrayNode();

        // Obține tranzacțiile pentru utilizator și le sortează după timestamp
        List<Transaction> transactions =
                transactionService.getTransactionsForUser(command.getEmail());
        transactions.sort(Comparator.comparing(Transaction::getTimestamp));
        for (Transaction transaction : transactions) {
            ObjectNode transactionNode = mapper.createObjectNode();

            // Creează un vizitator pentru tranzacție pentru a genera detaliile JSON
            ConcreteTransactionVisitor transactionVisitor =
                    new ConcreteTransactionVisitor(transactionNode, mapper);

            transaction.accept(transactionVisitor);
            outputTransactions.add(transactionNode);
        }

        cmdResult.set("output", outputTransactions);
        cmdResult.put("timestamp", command.getTimestamp());

        this.output.add(cmdResult);
    }

    /**
     * Execută comanda de verificare a stării unui card și gestionează rezultatele în JSON.
     *
     * @param command este comanda CheckCardStatusCommand care trebuie procesată.
     */
    @Override
    public void visit(final CheckCardStatusCommand command) {
        // Verifică starea unui card utilizând serviciul de carduri
        String result = cardService.checkCardStatus(command.getCardNumber());
        if (result.equals("Card not found")) {
            // Dacă cardul nu este găsit, creează un rezultat de tip "not found" în JSON
            notFoundResult(command.getCommand(), result, command.getTimestamp());
        }
        if (result.equals("Insufficient funds")) {
            // Dacă cardul are fonduri insuficiente, înregistrează o tranzacție și blochează cardul
            Transaction transaction = new MinimumAmountOfFundsTransaction(command.getTimestamp());
            Account account = cardService.getCardByNumber(command.getCardNumber()).getAccount();
            account.addTransaction(transaction);
            cardService.getCardByNumber(command.getCardNumber()).block();
        }
    }

    /**
     * Execută comanda de schimbare a ratei dobânzii pentru un cont și
     * înregistrează tranzacția corespunzătoare.
     *
     * @param command este comanda ChangeInterestRateCommand care trebuie procesată.
     */
    @Override
    public void visit(final ChangeInterestRateCommand command) {
        // Schimbă rata dobânzii pentru un cont utilizând serviciul de conturi
        String result = accountService.changeInterestRate(command.getAccount(),
                command.getIntrestRate());
        if (result.equals("This is not a savings account")) {
            // Dacă contul nu este de economii, creează un rezultat de
            // tip "not savings account" în JSON
            notSavingsAccountResult(command.getCommand(), result, command.getTimestamp());
        }
        if (result.equals("Success")) {
            // Dacă schimbarea a fost reușită, înregistrează o tranzacție de
            // schimbare a ratei dobânzii
            Transaction transaction = new InterestRateChangeTransaction(command.getTimestamp(),
                    command.getIntrestRate());
            accountService.getAccountByIBAN(command.getAccount()).addTransaction(transaction);
        }
    }

    /**
     * Execută comanda de împărțire a unei plăți între mai multe conturi și
     * înregistrează tranzacțiile corespunzătoare.
     *
     * @param command este comanda SplitPaymentCommand care trebuie procesată.
     */
    @Override
    public void visit(final SplitPaymentCommand command) {
        Integer splitTimestamp = command.getTimestamp();
        pendingSplits.put(splitTimestamp, command);
        Map<String, Boolean> acceptMap = new HashMap<>();
        for (String accountIban : command.getAccounts()) {
            Account account = accountService.getAccountByIBAN(accountIban);
            User user = account.getOwner();
            String email = user.getEmail();
            acceptMap.put(email, false);
            userPendingSplits.putIfAbsent(email, new ArrayDeque<>());
            userPendingSplits.get(email).offer(splitTimestamp);
        }

        acceptanceMaps.put(splitTimestamp, acceptMap);
    }

    /**
     * Execută comanda de generare a unui raport al tranzacțiilor pentru un cont
     * și adaugă rezultatul în JSON.
     *
     * @param command este comanda ReportCommand care trebuie procesată.
     */
    @Override
    public void visit(final ReportCommand command) {
        ObjectNode reportResult = mapper.createObjectNode();
        reportResult.put("command", command.getCommand());
        Account account = accountService.getAccountByIBAN(command.getAccount());
        if (account == null) {
            // Dacă contul nu este găsit, creează un rezultat de tip "not found" în JSON
            notFoundResult(command.getCommand(), "Account not found",
                    command.getTimestamp());
            return;
        }
        // Obține lista de tranzacții pentru cont
        List<Transaction> transactions = accountService.getTransactions(command.getAccount());
        ObjectNode outputNode;
        // Creează nodul de raport utilizând metoda auxiliară
        outputNode = createReportOutputNode(account, transactions, command.getStartTimestamp(),
                command.getEndTimestamp(), null, null);
        reportResult.set("output", outputNode);
        reportResult.put("timestamp", command.getTimestamp());
        this.output.add(reportResult);
    }

    /**
     * Execută comanda de generare a unui raport detaliat al cheltuielilor pe
     * comercianți pentru un cont și adaugă rezultatul în JSON.
     *
     * @param command este comanda SpendingsReportCommand care trebuie procesată.
     */
    @Override
    public void visit(final SpendingsReportCommand command) {
        ObjectNode reportResult = mapper.createObjectNode();
        reportResult.put("command", command.getCommand());
        Account account = accountService.getAccountByIBAN(command.getAccount());
        if (account == null) {
            // Dacă contul nu este găsit, creează un rezultat de tip "not found" în JSON
            notFoundResult(command.getCommand(), "Account not found",
                    command.getTimestamp());
            return;
        }
        if (account.getAccountType().equals("savings")) {
            // Dacă contul este de economii, generează un mesaj de eroare deoarece
            // acest tip de raport nu este suportat
            ObjectNode cmdResult = mapper.createObjectNode();
            cmdResult.put("command", command.getCommand());
            ObjectNode outputNode = mapper.createObjectNode();
            outputNode.put("error",
                    "This kind of report is not supported for a saving account");
            cmdResult.set("output", outputNode);
            cmdResult.put("timestamp", command.getTimestamp());
            this.output.add(cmdResult);
            return;
        }
        // Mapare pentru totalurile cheltuielilor pe comerciant
        Map<String, Double> commerciantsTotals = new HashMap<>();
        List<Transaction> transactions = accountService.getTransactions(command.getAccount());
        ObjectNode outputNode;
        // Creează nodul de raport filtrând tranzacțiile de tip "CardPayment"
        outputNode = createReportOutputNode(account, transactions,
                command.getStartTimestamp(), command.getEndTimestamp(),
                "CardPayment", commerciantsTotals);
        // Sortează comercianții după nume
        List<Map.Entry<String, Double>> commerciantsList =
                new ArrayList<>(commerciantsTotals.entrySet());
        commerciantsList.sort(Map.Entry.comparingByKey());
        ArrayNode commerciantsArray = mapper.createArrayNode();
        // Adaugă totalurile cheltuielilor pe fiecare comerciant în raport
        for (Map.Entry<String, Double> entry : commerciantsList) {
            ObjectNode commerciantNode = mapper.createObjectNode();
            commerciantNode.put("commerciant", entry.getKey());
            commerciantNode.put("total", entry.getValue());
            commerciantsArray.add(commerciantNode);
        }

        outputNode.set("commerciants", commerciantsArray);
        reportResult.set("output", outputNode);
        reportResult.put("timestamp", command.getTimestamp());
        this.output.add(reportResult);
    }

    /**
     * Execută comanda de adăugare a dobânzii la un cont de economii și gestionează
     * rezultatul în JSON.
     *
     * @param command este comanda AddInterestCommand care trebuie procesată.
     */
    @Override
    public void visit(final AddInterestCommand command) {
        String result = accountService.addInterestRate(command.getAccount());
        if (result.equals("This is not a savings account")) {
            // Dacă contul nu este de economii, creează un rezultat de tip
            // "not savings account" în JSON
            notSavingsAccountResult(command.getCommand(), result, command.getTimestamp());
        } else if (result.startsWith("Success")) {
            String[] resultParts = result.split(": ");
            if (resultParts.length > 1) {
                String amountString = resultParts[1];
                double amount = Double.parseDouble(amountString);
                Account account = accountService.getAccountByIBAN(command.getAccount());
                Transaction transaction = new InterestRateIncomeTransaction(amount,
                        account.getCurrency(), command.getTimestamp());
                account.addTransaction(transaction);
            }
        }
    }

    /**
     * Procesează comanda de retragere din contul de economii și adaugă tranzacțiile aferente.
     *
     * @param command comanda de retragere din economii
     */
    @Override
    public void visit(final WithdrawSavingsCommand command) {
        try {
            String result = accountService.withdrawSavings(command.getAccount(),
                    command.getAmount(), command.getCurrency());
            if (result.startsWith("Savings withdrawal to")) {
                String prefix = "Savings withdrawal to ";
                if (result.length() > prefix.length()) {
                    String classicAccount = result.substring(prefix.length()).trim();
                    Transaction transaction =
                            new SavingsWithdrawlTransaction(command.getTimestamp(),
                            command.getAccount(), classicAccount, command.getAmount());
                    accountService.getAccountByIBAN(
                            command.getAccount()).addTransaction(transaction);
                    accountService.getAccountByIBAN(classicAccount).addTransaction(transaction);
                }
            }
        } catch (NotMinimumAgeRequired exception) {
            String exceptionMessage = exception.getMessage();
            if (exceptionMessage.startsWith("You don't have the minimum age required.")) {
                String prefix = "You don't have the minimum age required. ";
                if (exceptionMessage.length() > prefix.length()) {
                    String classicAccount = exceptionMessage.substring(prefix.length()).trim();
                    Transaction errorTransaction = new
                            NotMinimumAgeRequiredTransaction(command.getTimestamp());
                    accountService.getAccountByIBAN(
                            command.getAccount()).addTransaction(errorTransaction);
                }
            }
        } catch (NotClassicAccountException exception) {
            String exceptionMessage = exception.getMessage();
            if (exceptionMessage.equals("You do not have a classic account.")) {
                Transaction transaction = new NotClassicAccountTransaction(command.getTimestamp());
                accountService.getAccountByIBAN(command.getAccount()).addTransaction(transaction);
            }
        } catch (InsufficientFundsException exception) {
            String exceptionMessage = exception.getMessage();
            if (exceptionMessage.equals("Insufficient funds")) {
                Transaction transaction = new InsufficientFundsTransaction(command.getTimestamp());
                accountService.getAccountByIBAN(command.getAccount()).addTransaction(transaction);
            }
        }
    }

    /**
     * Procesează comanda de actualizare a planului utilizatorului și
     * adaugă tranzacția aferentă.
     *
     * @param command comanda de actualizare a planului
     */
    @Override
    public void visit(final UpgradePlanCommand command) {
        try {
            accountService.upgradePlan(command.getAccount(), command.getNewPlanType());
            Account account = accountService.getAccountByIBAN(command.getAccount());
            Transaction transaction = new UpgradePlanTransaction(command.getAccount(),
                    command.getNewPlanType(), command.getTimestamp());
            account.addTransaction(transaction);
        } catch (InsufficientFundsException exception) {
            Transaction transaction = new InsufficientFundsTransaction(command.getTimestamp());
            accountService.getAccountByIBAN(command.getAccount()).addTransaction(transaction);
        } catch (UnknownPlanException exception) {

        } catch (org.poo.exception.AccountNotFoundException exception) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("command", command.getCommand());
            ObjectNode outputNode = mapper.createObjectNode();
            outputNode.put("timestamp", command.getTimestamp());
            outputNode.put("description", "Account not found");
            objectNode.set("output", outputNode);
            this.output.add(objectNode);
            objectNode.put("timestamp", command.getTimestamp());
        } catch (PlanDowngradeException exception) {

        } catch (SamePlanException exception) {
            Transaction transaction = new UpgradeToSamePlanTransaction(command.getTimestamp(),
                    "The user already has the " + command.getNewPlanType() + " plan.");
            accountService.getAccountByIBAN(command.getAccount()).addTransaction(transaction);
        }
    }

    /**
     * Procesează comanda de retragere de numerar folosind un card și adaugă tranzacțiile aferente.
     *
     * @param command comanda de retragere de numerar
     */
    @Override
    public void visit(final CashWithdrawalCommand command) {
        try {
            String result = cardService.cashWithdrawal(command.getCardNumber(),
                    command.getAmount(), command.getEmail());
            if (result.startsWith("Cash withdrawal of")) {
                Transaction transaction = new CashWithdrawalTransaction(command.getAmount(),
                        command.getTimestamp());
                Card card = cardService.getCardByNumber(command.getCardNumber());
                Account account = card.getAccount();
                account.addTransaction(transaction);
            }
        } catch (InsufficientFundsException exception) {
            Transaction transaction = new InsufficientFundsTransaction(command.getTimestamp());
            Card card = cardService.getCardByNumber(command.getCardNumber());
            Account account = card.getAccount();
            account.addTransaction(transaction);
        } catch (CardNotFoundException exception) {
            String exceptionMessage = exception.getMessage();
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("command", command.getCommand());
            ObjectNode outputNode = mapper.createObjectNode();
            outputNode.put("timestamp", command.getTimestamp());
            outputNode.put("description", exceptionMessage);
            objectNode.set("output", outputNode);
            this.output.add(objectNode);
            objectNode.put("timestamp", command.getTimestamp());
        } catch (UserNotFoundException exception) {

        } catch (AccountNotFoundException exception) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("command", command.getCommand());
            ObjectNode outputNode = mapper.createObjectNode();
            outputNode.put("timestamp", command.getTimestamp());
            outputNode.put("description", "Account not found");
            objectNode.set("output", outputNode);
            this.output.add(objectNode);
            objectNode.put("timestamp", command.getTimestamp());
        }
    }

    /**
     * Procesează comanda de acceptare a unei plăți împărțite și actualizează conturile implicate.
     *
     * @param command comanda de acceptare a plății împărțite
     */
    @Override
    public void visit(final AcceptSplitPayment command) {
        String email = command.getEmail();

        User user = userService.getUserByEmail(email);
        if (user == null) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("command", command.getCommand());
            ObjectNode outputNode = mapper.createObjectNode();
            outputNode.put("timestamp", command.getTimestamp());
            outputNode.put("description", "User not found");
            objectNode.put("output", outputNode);
            objectNode.put("timestamp", command.getTimestamp());
            this.output.add(objectNode);
        }

        Deque<Integer> queue = userPendingSplits.get(email);
        if (queue == null || queue.isEmpty()) {
            return;
        }

        Integer splitTimestamp = queue.peek();
        if (splitTimestamp == null) {
            return;
        }

        Map<String, Boolean> acceptMap = acceptanceMaps.get(splitTimestamp);
        if (acceptMap != null) {
            acceptMap.put(email, true);
            SplitPaymentCommand splitPaymentCommand = pendingSplits.get(splitTimestamp);
            if (splitPaymentCommand.getType().equals("equal")) {
                int nrOfAccounts = splitPaymentCommand.getAccounts().size();
                double splitAmount = splitPaymentCommand.getAmount() / nrOfAccounts;
                for (String iban : splitPaymentCommand.getAccounts()) {
                    Account account = accountService.getAccountByIBAN(iban);
                    double convertedAmount = splitAmount;
                    if (!account.getCurrency().equals(splitPaymentCommand.getCurrency())) {
                        convertedAmount = exchangeService.convertCurrency(
                                splitPaymentCommand.getCurrency(),
                                account.getCurrency(), splitAmount);
                    }
                    account.addAmountForSplit(convertedAmount);
                }
            } else if (splitPaymentCommand.getType().equals("custom")) {
                for (int i = 0; i < splitPaymentCommand.getAccounts().size(); i++) {
                    Account account =
                            accountService.getAccountByIBAN(
                                    splitPaymentCommand.getAccounts().get(i));
                    double amountForUser =
                            splitPaymentCommand.getAmountForUsers().get(i);
                    if (!account.getCurrency().equals(splitPaymentCommand.getCurrency())) {
                        amountForUser = exchangeService.convertCurrency(
                                splitPaymentCommand.getCurrency(),
                                account.getCurrency(), amountForUser);
                    }
                    account.addAmountForSplit(amountForUser);
                }
            }
        }

        boolean allAccepted = acceptMap.values().stream().allMatch(Boolean::booleanValue);
        if (allAccepted) {
            SplitPaymentCommand spCmd = pendingSplits.get(splitTimestamp);
            if (spCmd != null) {
                try {
                    String result = accountService.splitPayment(
                            spCmd.getAccounts(),
                            spCmd.getCurrency(),
                            spCmd.getAmount(),
                            spCmd.getType(),
                            spCmd.getAmountForUsers()
                    );

                    if ("Success".equals(result)) {
                        if (spCmd.getType().equals("equal")) {
                            double amount = spCmd.getAmount() / spCmd.getAccounts().size();
                            String formattedAmount = String.format("%.2f", spCmd.getAmount());
                            Transaction transaction = new SplitPaymentTransaction(
                                    spCmd.getTimestamp(),
                                    spCmd.getCurrency(),
                                    formattedAmount,
                                    spCmd.getAccounts(),
                                    amount
                            );
                            int nrOfAccounts = spCmd.getAccounts().size();
                            double splitAmount = spCmd.getAmount() / nrOfAccounts;
                            for (String iban : spCmd.getAccounts()) {
                                Account account = accountService.getAccountByIBAN(iban);
                                double convertedAmount = splitAmount;
                                if (!account.getCurrency().equals(spCmd.getCurrency())) {
                                    convertedAmount =
                                            exchangeService.convertCurrency(spCmd.getCurrency(),
                                            account.getCurrency(), splitAmount);
                                }
                                account.decreaseAmountForSplit(convertedAmount);
                                account.addTransaction(transaction);
                            }
                        } else if (spCmd.getType().equals("custom")) {
                            String formattedAmount = String.format("%.2f", spCmd.getAmount());
                            Transaction transaction = new CustomSplitPaymentTransaction(
                                    spCmd.getTimestamp(),
                                    spCmd.getCurrency(),
                                    spCmd.getAmountForUsers(), spCmd.getAccounts(),
                                    formattedAmount);
                            for (String iban : spCmd.getAccounts()) {
                                Account account = accountService.getAccountByIBAN(iban);
                                account.addTransaction(transaction);
                            }

                            for (int i = 0; i < spCmd.getAccounts().size(); i++) {
                                Account account =
                                        accountService.getAccountByIBAN(spCmd.getAccounts().get(i));
                                double amountForUser = spCmd.getAmountForUsers().get(i);
                                if (!account.getCurrency().equals(spCmd.getCurrency())) {
                                    amountForUser =
                                            exchangeService.convertCurrency(spCmd.getCurrency(),
                                            account.getCurrency(), amountForUser);
                                }
                                account.decreaseAmountForSplit(amountForUser);
                            }
                        }
                    }

                    String regex = "Account \\S+ has insufficient funds for a split payment\\.";
                    if (result.trim().matches(regex) && spCmd.getType().equals("equal")) {
                        double amount = spCmd.getAmount() / spCmd.getAccounts().size();
                        Transaction transaction = new InssuficientFundsForSplitTransaction(
                                spCmd.getAmount(),
                                spCmd.getCurrency(),
                                spCmd.getAccounts(),
                                spCmd.getTimestamp(),
                                result,
                                amount
                        );
                        for (String iban : spCmd.getAccounts()) {
                            Account account = accountService.getAccountByIBAN(iban);
                            account.addTransaction(transaction);
                        }
                    } else if (result.trim().matches(regex) && spCmd.getType().equals("custom")) {
                        Transaction transaction = new InsufficientFundsForCustomSplitTransaction(
                                spCmd.getAmount(),
                                spCmd.getCurrency(),
                                spCmd.getAccounts(),
                                spCmd.getTimestamp(),
                                result,
                                spCmd.getAmountForUsers());
                        for (String iban : spCmd.getAccounts()) {
                            Account account = accountService.getAccountByIBAN(iban);
                            account.addTransaction(transaction);
                        }
                    }

                    pendingSplits.remove(splitTimestamp);
                    acceptanceMaps.remove(splitTimestamp);

                } catch (org.poo.exception.AccountNotFoundException e) {

                }
            }
        }

        queue.poll();
    }

    /**
     * Procesează comanda de adăugare a unui asociat nou
     * (angajat sau manager) într-un cont business.
     *
     * @param command comanda de adăugare a unui asociat nou
     */
    @Override
    public void visit(final AddNewBusinessAssociateCommand command) {
        try {
            accountService.addNewBusinessAssociate(command.getAccount(), command.getRole(),
                    command.getEmail());
        } catch (Exception exception) {

        }
    }

    /**
     * Procesează comanda de modificare a limitei de cheltuieli pentru un cont business.
     *
     * @param command comanda de modificare a limitei de cheltuieli
     */
    @Override
    public void visit(final ChangeSpendingLimitCommand command) {
        try {
            accountService.changeSpendingLimit(command.getAccount(),
                    command.getAmount(), command.getEmail());
        } catch (UnauthorizedAccessException exception) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("command", command.getCommand());
            ObjectNode outputNode = mapper.createObjectNode();
            outputNode.put("timestamp", command.getTimestamp());
            outputNode.put("description", "You must be owner "
                    + "in order to change spending limit.");
            objectNode.put("output", outputNode);
            objectNode.put("timestamp", command.getTimestamp());
            this.output.add(objectNode);
        }
    }

    /**
     * Procesează comanda de modificare a limitei de depozit pentru un cont business.
     *
     * @param command comanda de modificare a limitei de depozit
     */
    @Override
    public void visit(final ChangeDepositLimitCommand command) {
        try {
            accountService.changeDepositLimit(command.getAccount(),
                    command.getAmount(), command.getEmail());
        } catch (Exception exception) {

        }
    }

    /**
     * Procesează comanda de generare a unui raport pentru un cont business, incluzând date
     * despre manageri, angajați și limitele financiare.
     *
     * @param command comanda de generare a raportului business
     */
    @Override
    public void visit(final BusinessReportCommand command) {
        Account account = accountService.getAccountByIBAN(command.getAccount());
        ObjectNode reportResult = mapper.createObjectNode();
        reportResult.put("command", command.getCommand());
        if (account == null) {
            // Dacă contul nu este găsit, creează un rezultat de tip "not found" în JSON
            notFoundResult(command.getCommand(), "Account not found",
                    command.getTimestamp());
            return;
        }
        if (!account.getAccountType().equals("business")) {
            return;
        }

        double depositLimit = account.getDepositLimit();
        depositLimit = exchangeService.convertCurrency("RON",
                account.getCurrency(), depositLimit);

        double spendingLimit = account.getSpendingLimit();
        spendingLimit = exchangeService.convertCurrency("RON",
                account.getCurrency(), spendingLimit);

        ObjectNode outputNode = mapper.createObjectNode();
        outputNode.put("IBAN", account.getIban());
        outputNode.put("balance", account.getBalance());
        outputNode.put("currency", account.getCurrency());
        outputNode.put("spending limit", spendingLimit);
        outputNode.put("deposit limit", depositLimit);
        outputNode.put("statistics type", "transaction");

        ArrayNode managersArray = mapper.createArrayNode();
        account.getManagers().stream()
                .map(manager -> {
                    ObjectNode managerNode = mapper.createObjectNode();
                    managerNode.put("username",
                            manager.getLastName() + " " + manager.getFirstName());
                    managerNode.put("spent", manager.getSpentForBusiness(account.getIban()));
                    managerNode.put("deposited", manager.getDepositedForBusiness(account));
                    return managerNode;
                })
                .forEach(managersArray::add);
        outputNode.set("managers", managersArray);

        ArrayNode employeesArray = mapper.createArrayNode();
        account.getEmployees().stream()
                .map(employee -> {
                    ObjectNode employeeNode = mapper.createObjectNode();
                    employeeNode.put("username",
                            employee.getLastName() + " " + employee.getFirstName());
                    employeeNode.put("spent", employee.getSpentForBusiness(account.getIban()));
                    employeeNode.put("deposited", employee.getDepositedForBusiness(account));
                    return employeeNode;
                })
                .forEach(employeesArray::add);
        outputNode.set("employees", employeesArray);
        double totalSpent = account.getManagers().stream()
                .mapToDouble(manager -> manager.getSpentForBusiness(account.getIban()))
                .sum();
        double totalDeposited = account.getManagers().stream()
                .mapToDouble(manager -> manager.getDepositedForBusiness(account))
                .sum();

        outputNode.put("total spent", totalSpent);
        outputNode.put("total deposited", totalDeposited);

        reportResult.set("output", outputNode);
        reportResult.put("timestamp", command.getTimestamp());
        this.output.add(reportResult);
    }

    /**
     * Procesează comanda de respingere a unei plăți împărțite de către un utilizator.
     *
     * @param command comanda de respingere a plății împărțite
     */
    @Override
    public void visit(final RejectSplitPaymentCommand command) {
        String email = command.getEmail();

        User user = userService.getUserByEmail(email);
        if (user == null) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("command", command.getCommand());
            ObjectNode outputNode = mapper.createObjectNode();
            outputNode.put("timestamp", command.getTimestamp());
            outputNode.put("description", "User not found");
            objectNode.put("output", outputNode);
            objectNode.put("timestamp", command.getTimestamp());
            this.output.add(objectNode);
        }

        Deque<Integer> queue = userPendingSplits.get(email);
        if (queue == null || queue.isEmpty()) {
            return;
        }

        Integer splitTimestamp = queue.peek();
        if (splitTimestamp == null) {
            return;
        }

        Map<String, Boolean> acceptMap = acceptanceMaps.get(splitTimestamp);
        if (acceptMap != null) {
            acceptMap.put(email, false);
            SplitPaymentCommand spCmd = pendingSplits.get(splitTimestamp);
            if (spCmd != null) {
                UserRejectedSplitTransaction transaction = new UserRejectedSplitTransaction(
                        spCmd.getAmount(),
                        spCmd.getCurrency(),
                        spCmd.getAccounts(),
                        spCmd.getTimestamp(),
                        "One user rejected the payment.",
                        spCmd.getAmountForUsers(),
                        spCmd.getType()
                );

                for (String iban : spCmd.getAccounts()) {
                    Account account = accountService.getAccountByIBAN(iban);
                    account.addTransaction(transaction);
                }
            }
        }
        queue.poll();
    }
}
