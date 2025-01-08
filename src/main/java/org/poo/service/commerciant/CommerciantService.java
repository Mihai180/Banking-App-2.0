package org.poo.service.commerciant;

import org.poo.fileio.CommerciantInput;
import org.poo.model.commerciant.Commerciant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CommerciantService {
    private Map<String, Commerciant> commerciantsByName = new HashMap<>();

    /**
     *
     * @param commerciantInputs
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
     *
     * @param name
     * @return
     */
    public Commerciant getCommerciantByName(final String name) {
        return commerciantsByName.get(name);
    }

    public List<Commerciant> getAllCommerciants() {
        return new ArrayList<>(commerciantsByName.values());
    }
}
