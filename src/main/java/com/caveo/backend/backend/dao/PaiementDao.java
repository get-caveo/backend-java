package com.caveo.backend.backend.dao;

import com.caveo.backend.backend.model.Paiement;
import com.caveo.backend.backend.model.StatutPaiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaiementDao extends JpaRepository<Paiement, Integer> {

    Optional<Paiement> findByCommandeClientId(Integer commandeClientId);

    List<Paiement> findByStatutPaiement(StatutPaiement statut);
}
