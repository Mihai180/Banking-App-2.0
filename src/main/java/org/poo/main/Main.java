package org.poo.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.checker.Checker;
import org.poo.checker.CheckerConstants;
import org.poo.command.Command;
import org.poo.command.CommandFactory;
import org.poo.fileio.CommandInput;
import org.poo.fileio.ObjectInput;
import org.poo.fileio.UserInput;
import org.poo.service.AccountService;
import org.poo.service.TransactionService;
import org.poo.service.UserService;
import org.poo.service.CardService;
import org.poo.service.ExchangeService;
import org.poo.utils.Utils;
import org.poo.visitor.command.ConcreteCommandVisitor;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

/**
 * The entry point to this homework. It runs the checker that tests your implementation.
 */
public final class Main {
    /**
     * for coding style
     */
    private Main() {
    }

    /**
     * DO NOT MODIFY MAIN METHOD
     * Call the checker
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(CheckerConstants.TESTS_PATH);
        Path path = Paths.get(CheckerConstants.RESULT_PATH);

        if (Files.exists(path)) {
            File resultFile = new File(String.valueOf(path));
            for (File file : Objects.requireNonNull(resultFile.listFiles())) {
                file.delete();
            }
            resultFile.delete();
        }
        Files.createDirectories(path);

        var sortedFiles = Arrays.stream(Objects.requireNonNull(directory.listFiles())).
                sorted(Comparator.comparingInt(Main::fileConsumer))
                .toList();

        for (File file : sortedFiles) {
            String filepath = CheckerConstants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                action(file.getName(), filepath);
            }
        }

        Checker.calculateScore();
    }

    /**
     * @param filePath1 for input file
     * @param filePath2 for output file
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void action(final String filePath1,
                              final String filePath2) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(CheckerConstants.TESTS_PATH + filePath1);
        ObjectInput inputData = objectMapper.readValue(file, ObjectInput.class);

        ArrayNode output = objectMapper.createArrayNode();

        /*
         * TODO Implement your function here
         *
         * How to add output to the output array?
         * There are multiple ways to do this, here is one example:
         *
         * ObjectMapper mapper = new ObjectMapper();
         *
         * ObjectNode objectNode = mapper.createObjectNode();
         * objectNode.put("field_name", "field_value");
         *
         * ArrayNode arrayNode = mapper.createArrayNode();
         * arrayNode.add(objectNode);
         *
         * output.add(arrayNode);
         * output.add(objectNode);
         *
         */

        // Resetez seed-urile pentru generarea de IBAN și număr de card între teste
        Utils.resetRandom();

        // Inițializez serviciile care reprezintă logica de lucru cu utilizatorii,
        // conturi, carduri și tranzacții
        UserService userService = UserService.getInstance();
        ExchangeService exchangeService = ExchangeService.getInstance();
        AccountService accountService = AccountService.getInstance(userService, exchangeService);
        CardService cardService = CardService.getInstance(userService,
                accountService, exchangeService);
        TransactionService transactionService = TransactionService.getInstance(userService);

        // Dacă inputData conține informații despre utilizatori, aceștia sunt creați
        // prin intermediul userService
        if (inputData.getUsers() != null) {
            for (UserInput userInput : inputData.getUsers()) {
                userService.createUser(userInput);
            }
        }

        // Dacă inputData conține rate de schimb valutar, acestea sunt încărcate
        // în exchangeService
        if (inputData.getExchangeRates() != null) {
            exchangeService.loadExchangeRates(Arrays.asList(inputData.getExchangeRates()));
        }

        // Se creează o fabrică de comenzi care va produce obiecte de tip Command pe baza inputului
        CommandFactory commandFactory = new CommandFactory();

        // Se instanțiază un vizitator concret care va executa logica aferentă fiecărei comenzi,
        // folosind serviciile disponibile
        ConcreteCommandVisitor visitor = new ConcreteCommandVisitor(
                userService,
                accountService,
                cardService,
                transactionService,
                exchangeService,
                output,
                objectMapper
        );

        // Dacă există comenzi în inputData, pentru fiecare se creează comanda corespunzătoare,
        // apoi se apelează metoda accept, care permite vizitatorului să proceseze comanda și să
        // producă rezultatul
        if (inputData.getCommands() != null) {
            for (CommandInput cmdInput : inputData.getCommands()) {
                Command command = commandFactory.createCommand(cmdInput);
                command.accept(visitor);
            }
        }

        // După ce toate operațiunile au fost efectuate, se resetează instanțele serviciilor.
        UserService.resetInstance();
        AccountService.resetInstance();
        ExchangeService.resetInstance();
        CardService.resetInstance();
        TransactionService.resetInstance();

        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePath2), output);
    }

    /**
     * Method used for extracting the test number from the file name.
     *
     * @param file the input file
     * @return the extracted numbers
     */
    public static int fileConsumer(final File file) {
        return Integer.parseInt(
                file.getName()
                        .replaceAll(CheckerConstants.DIGIT_REGEX, CheckerConstants.EMPTY_STR)
        );
    }
}
