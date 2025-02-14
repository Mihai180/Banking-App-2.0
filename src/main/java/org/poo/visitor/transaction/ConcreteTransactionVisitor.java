package org.poo.visitor.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.model.transaction.*;

/**
 * Clasa ConcreteTransactionVisitor implementează interfața TransactionVisitor
 * și oferă implementări concrete pentru fiecare tip de tranzacție. Aceasta este utilizată
 * pentru a genera reprezentări JSON detaliate ale diferitelor tipuri de tranzacții.
 */
public final class ConcreteTransactionVisitor implements TransactionVisitor {
    private final ObjectMapper mapper;
    private final ObjectNode transactionNode;

    public ConcreteTransactionVisitor(final ObjectNode transactionNode, final ObjectMapper mapper) {
        this.transactionNode = transactionNode;
        this.mapper = mapper;
    }

    @Override
    public void visit(final AccountCreationTransaction transaction) {
        transactionNode.put("timestamp", transaction.getTimestamp());
        transactionNode.put("description", transaction.getDescription());
    }

    @Override
    public void visit(final SendMoneyTransaction sendMoneyTransaction) {
        transactionNode.put("timestamp", sendMoneyTransaction.getTimestamp());
        transactionNode.put("description", sendMoneyTransaction.getDescription());
        double amount = sendMoneyTransaction.getAmount();
        String currency = sendMoneyTransaction.getCurrency();

        String amountWithCurrency = amount + " " + currency;

        transactionNode.put("senderIBAN", sendMoneyTransaction.getSender());
        transactionNode.put("receiverIBAN", sendMoneyTransaction.getReceiver());
        transactionNode.put("amount", amountWithCurrency);
        transactionNode.put("transferType", sendMoneyTransaction.getTransferType());
    }

    @Override
    public void visit(final CardCreationTransaction transaction) {
        transactionNode.put("timestamp", transaction.getTimestamp());
        transactionNode.put("description", transaction.getDescription());
        transactionNode.put("card", transaction.getCardNumber());
        transactionNode.put("cardHolder", transaction.getCardHolder());
        transactionNode.put("account", transaction.getAccount());
    }

    @Override
    public void visit(final CardPaymentTransaction transaction) {
        transactionNode.put("timestamp", transaction.getTimestamp());
        transactionNode.put("description", transaction.getDescription());
        transactionNode.put("amount", transaction.getPaymentAmount());
        transactionNode.put("commerciant", transaction.getPaymentCommerciant());
    }

    @Override
    public void visit(final InsufficientFundsTransaction transaction) {
        transactionNode.put("timestamp", transaction.getTimestamp());
        transactionNode.put("description", transaction.getDescription());
    }

    @Override
    public void visit(final CardDeletionTransaction transaction) {
        transactionNode.put("timestamp", transaction.getTimestamp());
        transactionNode.put("description", transaction.getDescription());
        transactionNode.put("card", transaction.getCardNumber());
        transactionNode.put("cardHolder", transaction.getCardHolder());
        transactionNode.put("account", transaction.getAccount());
    }

    @Override
    public void visit(final MinimumAmountOfFundsTransaction transaction) {
        transactionNode.put("timestamp", transaction.getTimestamp());
        transactionNode.put("description", transaction.getDescription());
    }

    @Override
    public void visit(final FrozenCardTransaction transaction) {
        transactionNode.put("timestamp", transaction.getTimestamp());
        transactionNode.put("description", transaction.getDescription());
    }

    @Override
    public void visit(final SplitPaymentTransaction transaction) {
        transactionNode.put("timestamp", transaction.getTimestamp());
        transactionNode.put("description", transaction.getDescription()
                + transaction.getAmount() + " " + transaction.getCurrency());
        transactionNode.put("splitPaymentType", "equal");
        transactionNode.put("currency", transaction.getCurrency());
        transactionNode.put("amount", transaction.getSplitAmount());
        ArrayNode involvedAccountsArray = mapper.createArrayNode();
        for (String iban : transaction.getInvolvedAccounts()) {
            involvedAccountsArray.add(iban);
        }
        transactionNode.set("involvedAccounts", involvedAccountsArray);
    }

    @Override
    public void visit(final InssuficientFundsForSplitTransaction transaction) {
        transactionNode.put("timestamp", transaction.getTimestamp());
        transactionNode.put("description", transaction.getDescription());
        transactionNode.put("splitPaymentType", "equal");
        transactionNode.put("currency", transaction.getCurrency());
        transactionNode.put("amount", transaction.getSplitAmount());
        ArrayNode involvedAccountsArray = mapper.createArrayNode();
        for (String iban : transaction.getInvolvedAccounts()) {
            involvedAccountsArray.add(iban);
        }
        transactionNode.set("involvedAccounts", involvedAccountsArray);
        transactionNode.put("error", transaction.getError());
    }

    @Override
    public void visit(final AccountDeletionErrorTransaction transaction) {
        transactionNode.put("timestamp", transaction.getTimestamp());
        transactionNode.put("description", transaction.getDescription());
    }

    @Override
    public void visit(final InterestRateChangeTransaction transaction) {
        transactionNode.put("timestamp", transaction.getTimestamp());
        transactionNode.put("description", transaction.getDescription());
    }

    @Override
    public void visit(final SavingsWithdrawlTransaction transaction) {
        transactionNode.put("timestamp", transaction.getTimestamp());
        transactionNode.put("description", transaction.getDescription());
        transactionNode.put("savingsAccountIBAN", transaction.getSavingsAccountIBAN());
        transactionNode.put("classicAccountIBAN", transaction.getClassicAccountIBAN());
        transactionNode.put("amount", transaction.getAmount());
    }

    @Override
    public void visit(final NotMinimumAgeRequiredTransaction transaction) {
        transactionNode.put("timestamp", transaction.getTimestamp());
        transactionNode.put("description", transaction.getDescription());
    }

    @Override
    public void visit(final NotClassicAccountTransaction transaction) {
        transactionNode.put("timestamp", transaction.getTimestamp());
        transactionNode.put("description", transaction.getDescription());
    }

    @Override
    public void visit(final UpgradePlanTransaction transaction) {
        transactionNode.put("timestamp", transaction.getTimestamp());
        transactionNode.put("description", transaction.getDescription());
        transactionNode.put("accountIBAN", transaction.getAccount());
        transactionNode.put("newPlanType", transaction.getNewPlanType());
    }

    @Override
    public void visit(final CashWithdrawalTransaction transaction) {
        transactionNode.put("timestamp", transaction.getTimestamp());
        transactionNode.put("description", transaction.getDescription());
        transactionNode.put("amount", transaction.getAmount());
    }

    @Override
    public void visit(final InterestRateIncomeTransaction transaction) {
        transactionNode.put("timestamp", transaction.getTimestamp());
        transactionNode.put("description", transaction.getDescription());
        transactionNode.put("amount", transaction.getAmount());
        transactionNode.put("currency", transaction.getCurrency());
    }

    @Override
    public void visit(final CustomSplitPaymentTransaction transaction) {
        transactionNode.put("timestamp", transaction.getTimestamp());
        transactionNode.put("description", transaction.getDescription()
                + transaction.getAmount() + " " + transaction.getCurrency());
        transactionNode.put("splitPaymentType", "custom");
        transactionNode.put("currency", transaction.getCurrency());
        ArrayNode amountForUsers = mapper.createArrayNode();
        for (Double amount : transaction.getAmountForUsers()) {
            amountForUsers.add(amount);
        }
        transactionNode.set("amountForUsers", amountForUsers);
        ArrayNode involvedAccountsArray = mapper.createArrayNode();
        for (String iban : transaction.getInvolvedAccounts()) {
            involvedAccountsArray.add(iban);
        }
        transactionNode.set("involvedAccounts", involvedAccountsArray);
    }

    @Override
    public void visit(final InsufficientFundsForCustomSplitTransaction transaction) {
        transactionNode.put("timestamp", transaction.getTimestamp());
        transactionNode.put("description", transaction.getDescription());
        transactionNode.put("splitPaymentType", "custom");
        transactionNode.put("currency", transaction.getCurrency());
        ArrayNode amountForUsers = mapper.createArrayNode();
        for (Double amount : transaction.getAmountForUsers()) {
            amountForUsers.add(amount);
        }
        transactionNode.set("amountForUsers", amountForUsers);
        ArrayNode involvedAccountsArray = mapper.createArrayNode();
        for (String iban : transaction.getInvolvedAccounts()) {
            involvedAccountsArray.add(iban);
        }
        transactionNode.set("involvedAccounts", involvedAccountsArray);
        transactionNode.put("error", transaction.getError());
    }

    @Override
    public void visit(final UserRejectedSplitTransaction transaction) {
        transactionNode.put("timestamp", transaction.getTimestamp());
        transactionNode.put("description", transaction.getDescription());
        transactionNode.put("splitPaymentType", transaction.getSplitPaymentType());
        transactionNode.put("currency", transaction.getCurrency());
        ArrayNode amountForUsers = mapper.createArrayNode();
        for (Double amount : transaction.getAmountForUsers()) {
            amountForUsers.add(amount);
        }
        transactionNode.set("amountForUsers", amountForUsers);
        ArrayNode involvedAccountsArray = mapper.createArrayNode();
        for (String iban : transaction.getInvolvedAccounts()) {
            involvedAccountsArray.add(iban);
        }
        transactionNode.set("involvedAccounts", involvedAccountsArray);
        transactionNode.put("error", transaction.getError());
    }

    @Override
    public void visit(final UpgradeToSamePlanTransaction transaction) {
        transactionNode.put("timestamp", transaction.getTimestamp());
        transactionNode.put("description", transaction.getDescription());
    }
}
