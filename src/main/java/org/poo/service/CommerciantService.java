package org.poo.service;

import org.poo.fileio.CommerciantInput;
import org.poo.model.commerciant.Commerciant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CommerciantService {
    private static Map<String, Commerciant> commerciantsByName = new HashMap<>();

    /**
     * Încarcă o listă de comercianți în sistem, mapându-i după numele lor.
     *
     * @param commerciantInputs lista de obiecte CommerciantInput care conțin
     * datele necesare pentru a crea instanțe de Commerciant.
     */
    public void loadCommerciants(final List<CommerciantInput> commerciantInputs) {
        for (CommerciantInput commerciantInput : commerciantInputs) {
            Commerciant commerciant = new Commerciant(commerciantInput.getCommerciant(),
                    commerciantInput.getId(), commerciantInput.getAccount(),
                    commerciantInput.getType(), commerciantInput.getCashbackStrategy());
            commerciantsByName.put(commerciantInput.getCommerciant(), commerciant);
        }
    }

    /**
     * Obține un obiect Commerciant după numele acestuia.
     *
     * @param name numele comerciantului pe care dorești să-l obții.
     * @return obiectul Commerciant asociat cu numele dat sau null
     * dacă nu există niciun comerciant cu numele specificat.
     */
    public static Commerciant getCommerciantByName(final String name) {
        return commerciantsByName.get(name);
    }
}
