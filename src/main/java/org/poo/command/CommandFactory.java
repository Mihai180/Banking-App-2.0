package org.poo.command;

import org.poo.fileio.CommandInput;

/**
 * Fabrica de comenzi care instanțiază tipul de comandă corect
 * în funcție de datele primite prin CommandInput.
 */
public final class CommandFactory {
    /**
     * Creează și returnează o instanță de Command corespunzătoare
     * tipului de comandă specificat în input.
     * @param input este un obiect ce conține datele necesare pentru crearea unei comenzi
     * @return instanța de Command corespunzătoare
     */
    public Command createCommand(final CommandInput input) {
        String commandName = input.getCommand();

        switch (commandName) {
            case "printUsers":
                return new PrintUsersCommand(commandName, input.getTimestamp());
            case "addAccount":
                return new AddAccountCommand(commandName, input.getTimestamp(), input.getEmail(),
                        input.getAccountType(), input.getCurrency(), input.getInterestRate());
            case "addFunds":
                return new AddFundsCommand(commandName, input.getTimestamp(), input.getAccount(),
                        input.getAmount());
            case "createCard":
            case "createOneTimeCard":
                return new CreateCardCommand(commandName, input.getTimestamp(), input.getAccount(),
                        input.getEmail());
            case "deleteAccount":
                return new DeleteAccountCommand(commandName, input.getTimestamp(),
                        input.getAccount(), input.getEmail());
            case "deleteCard":
                return new DeleteCardCommand(input.getTimestamp(), input.getCardNumber(),
                        input.getEmail());
            case "setMinimumBalance":
                return new SetMinBalanceCommand(input.getAccount(), input.getMinBalance());
            case "payOnline":
                return new PayOnlineCommand(commandName, input.getCardNumber(), input.getAmount(),
                        input.getCurrency(), input.getTimestamp(), input.getDescription(),
                        input.getCommerciant(), input.getEmail());
            case "sendMoney":
                return new SendMoneyCommand(input.getAccount(), input.getAmount(),
                        input.getReceiver(), input.getTimestamp(), input.getDescription());
            case "setAlias":
                return new SetAliasCommand(input.getEmail(), input.getAlias(), input.getAccount());
            case "printTransactions":
                return new PrintTransactionsCommand(input.getCommand(), input.getTimestamp(),
                        input.getEmail());
            case "checkCardStatus":
                return new CheckCardStatusCommand(input.getCardNumber(), input.getTimestamp(),
                        input.getCommand());
            case "changeInterestRate":
                return new ChangeInterestRateCommand(input.getTimestamp(), input.getAccount(),
                        input.getInterestRate(), input.getCommand());
            case "splitPayment":
                return new SplitPaymentCommand(input.getAccounts(), input.getTimestamp(),
                        input.getCurrency(), input.getAmount());
            case "report":
                return new ReportCommand(input.getCommand(), input.getTimestamp(),
                        input.getStartTimestamp(), input.getEndTimestamp(), input.getAccount());
            case "spendingsReport":
                return new SpendingsReportCommand(input.getStartTimestamp(),
                        input.getEndTimestamp(), input.getTimestamp(), input.getCommand(),
                        input.getAccount());
            case "addInterest":
                return new AddInterestCommand(input.getCommand(), input.getAccount(),
                        input.getTimestamp());
            case "withdrawSavings":
                return new WithdrawSavingsCommand(input.getCommand(), input.getAccount(),
                        input.getAmount(), input.getCurrency(), input.getTimestamp());
            default:
                return new NotImplementedCommand(commandName, input.getTimestamp());
        }
    }
}
