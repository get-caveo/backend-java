package com.caveo.backend.backend.dao;

import com.caveo.backend.backend.model.LigneInventaire;
import com.caveo.backend.backend.model.StatutLigneInventaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LigneInventaireDao extends JpaRepository<LigneInventaire, Integer> {

    List<LigneInventaire> findByInventaireId(Integer inventaireId);

    List<LigneInventaire> findByInventaireIdAndStatut(Integer inventaireId, StatutLigneInventaire statut);

    // Lignes avec relations chargées
    @Query("SELECT l FROM LigneInventaire l " +
            "JOIN FETCH l.produit " +
            "JOIN FETCH l.uniteConditionnement " +
            "WHERE l.inventaire.id = :inventaireId " +
            "ORDER BY l.produit.nom, l.uniteConditionnement.ordreTri")
    List<LigneInventaire> findByInventaireIdWithRelations(@Param("inventaireId") Integer inventaireId);

    // Compter les lignes par statut pour un inventaire
    @Query("SELECT COUNT(l) FROM LigneInventaire l WHERE l.inventaire.id = :inventaireId AND l.statut = :statut")
    long countByInventaireIdAndStatut(@Param("inventaireId") Integer inventaireId, 
                                       @Param("statut") StatutLigneInventaire statut);

    // Lignes avec différence (écarts)
    @Query("SELECT l FROM LigneInventaire l " +
            "JOIN FETCH l.produit " +
            "JOIN FETCH l.uniteConditionnement " +
            "WHERE l.inventaire.id = :inventaireId " +
            "AND l.difference IS NOT NULL AND l.difference <> 0")
    List<LigneInventaire> findLignesAvecEcart(@Param("inventaireId") Integer inventaireId);

    void deleteByInventaireId(Integer inventaireId);
}
