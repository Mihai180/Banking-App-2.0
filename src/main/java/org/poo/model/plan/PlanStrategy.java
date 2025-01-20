package org.poo.model.plan;

/**
 * Interfața pentru strategiile planurilor.
 * Definește metode pentru calcularea comisionului, verificarea retrogradării,
 * calcularea taxei de upgrade și obținerea numelui planului.
 */
public interface PlanStrategy {
    /**
     * Calculează comisionul pentru o sumă de tranzacție dată, exprimată în RON.
     *
     * @param amount este suma tranzacționată în RON
     * @return comisionul aferent tranzacției
     */
    double calculateCommission(double amount);

    /**
     * Verifică dacă un plan cerut reprezintă o retrogradare.
     *
     * @param requested etse tipul de plan solicitat
     * @return `true` dacă planul este inferior, altfel `false`
     */
    boolean isDowngrade(String requested);

    /**
     * Calculează taxa pentru un upgrade către un plan specificat.
     *
     * @param requested este tipul de plan solicitat pentru upgrade
     * @return taxa de upgrade sau o valoare negativă dacă cererea este invalidă
     */
    double calculateUpgradeFee(String requested);

    /**
     * Returnează numele planului asociat strategiei.
     *
     * @return numele planului ca șir de caractere
     */
    String getPlan();
}
