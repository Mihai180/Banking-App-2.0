package org.poo.model.user;

import org.poo.model.account.Account;
import org.poo.model.plan.PlanStrategy;
import org.poo.model.plan.StandardPlan;
import org.poo.model.plan.StudentPlan;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Clasa finală User reprezintă un utilizator al sistemului bancar, conținând informații personale
 * și gestionând conturile asociate acestuia
 */
public final class User {
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String birthDate;
    private final String occupation;
    private final ArrayList<Account> accounts;
    private final Map<String, String> aliases;
    private PlanStrategy currentPlan;

    public User(final String firstName, final String lastName, final String email,
                final String birthDate, final String occupation) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthDate = birthDate;
        this.occupation = occupation;
        this.accounts = new ArrayList<>();
        this.aliases = new HashMap<>();
        if (occupation.equals("student")) {
            this.currentPlan = new StudentPlan();
        } else {
            this.currentPlan = new StandardPlan();
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public ArrayList<Account> getAccounts() {
        return accounts;
    }

    public Map<String, String> getAliases() {
        return aliases;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getOccupation() {
        return occupation;
    }

    public PlanStrategy getCurrentPlan() {
        return currentPlan;
    }

    public void setCurrentPlan(final PlanStrategy currentPlan) {
        this.currentPlan = currentPlan;
    }

    /**
     * Adaugă un cont nou la lista de conturi ale utilizatorului
     * Dacă contul nu este null, acesta este adăugat în listă
     * @param account este contul care trebuie adăugat
     */
    public void addAccount(final Account account) {
        if (account != null) {
            accounts.add(account);
        }
   }

    /**
     * Adaugă un alias pentru un cont specificat prin IBAN
     * @param aliasName este numele aliasului care va fi asociat contului
     * @param accountIban este IBAN-ul contului pentru care se creează aliasul
     */
   public void addAlias(final String aliasName, final String accountIban) {
        aliases.put(aliasName, accountIban);
   }

   public boolean isUserOldEnough() {
       DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
       LocalDate birthDate = LocalDate.parse(this.birthDate, formatter);
       LocalDate now = LocalDate.now();
       return Period.between(birthDate, now).getYears() >= 21;
   }
}
