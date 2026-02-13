package com.caveo.backend.backend.model;

import java.util.Map;
import java.util.Set;

public enum StatutCommandeClient {
    EN_ATTENTE,
    CONFIRMEE,
    EN_PREPARATION,
    EXPEDIEE,
    LIVREE,
    ANNULEE;

    private static final Map<StatutCommandeClient, Set<StatutCommandeClient>> TRANSITIONS = Map.of(
            EN_ATTENTE, Set.of(CONFIRMEE, ANNULEE),
            CONFIRMEE, Set.of(EN_PREPARATION, ANNULEE),
            EN_PREPARATION, Set.of(EXPEDIEE, ANNULEE),
            EXPEDIEE, Set.of(LIVREE),
            LIVREE, Set.of(),
            ANNULEE, Set.of()
    );

    public boolean peutTransitionnerVers(StatutCommandeClient cible) {
        return TRANSITIONS.getOrDefault(this, Set.of()).contains(cible);
    }
}
