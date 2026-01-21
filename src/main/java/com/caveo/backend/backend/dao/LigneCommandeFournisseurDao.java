package com.caveo.backend.backend.dao;

import com.caveo.backend.backend.model.LigneCommandeFournisseur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LigneCommandeFournisseurDao extends JpaRepository<LigneCommandeFournisseur, Integer> {

    List<LigneCommandeFournisseur> findByCommandeFournisseurId(Integer commandeId);

    // Lignes avec relations
    @Query("SELECT l FROM LigneCommandeFournisseur l " +
            "JOIN FETCH l.produit " +
            "JOIN FETCH l.uniteConditionnement " +
            "WHERE l.commandeFournisseur.id = :commandeId " +
            "ORDER BY l.produit.nom")
    List<LigneCommandeFournisseur> findByCommandeIdWithRelations(@Param("commandeId") Integer commandeId);

    // Lignes non entièrement reçues
    @Query("SELECT l FROM LigneCommandeFournisseur l " +
            "WHERE l.commandeFournisseur.id = :commandeId " +
            "AND l.quantiteRecue < l.quantite")
    List<LigneCommandeFournisseur> findLignesNonRecues(@Param("commandeId") Integer commandeId);

    void deleteByCommandeFournisseurId(Integer commandeId);
}
