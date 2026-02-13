package com.caveo.backend.backend.dao;

import com.caveo.backend.backend.model.LigneCommandeClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LigneCommandeClientDao extends JpaRepository<LigneCommandeClient, Integer> {

    List<LigneCommandeClient> findByCommandeClientId(Integer commandeClientId);

    @Query("SELECT l FROM LigneCommandeClient l " +
            "JOIN FETCH l.produit " +
            "JOIN FETCH l.uniteConditionnement " +
            "WHERE l.commandeClient.id = :commandeId " +
            "ORDER BY l.produit.nom")
    List<LigneCommandeClient> findByCommandeIdWithRelations(@Param("commandeId") Integer commandeId);

    void deleteByCommandeClientId(Integer commandeClientId);
}
