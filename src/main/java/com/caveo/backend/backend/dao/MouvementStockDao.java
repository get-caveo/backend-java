package com.caveo.backend.backend.dao;

import com.caveo.backend.backend.model.MouvementStock;
import com.caveo.backend.backend.model.TypeMouvement;
import com.caveo.backend.backend.model.TypeReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MouvementStockDao extends JpaRepository<MouvementStock, Integer> {

    List<MouvementStock> findByProduitIdOrderByCreeLe(Integer produitId);

    List<MouvementStock> findByProduitIdAndUniteConditionnementIdOrderByCreeLe(
            Integer produitId, Integer uniteConditionnementId);

    List<MouvementStock> findByTypeMouvementOrderByCreeLe(TypeMouvement typeMouvement);

    List<MouvementStock> findByTypeReferenceOrderByCreeLe(TypeReference typeReference);

    List<MouvementStock> findByTypeReferenceAndReferenceIdOrderByCreeLe(
            TypeReference typeReference, Integer referenceId);

    // Mouvements dans une période
    @Query("SELECT m FROM MouvementStock m WHERE m.creeLe BETWEEN :debut AND :fin ORDER BY m.creeLe DESC")
    List<MouvementStock> findByPeriode(@Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);

    // Mouvements par utilisateur
    List<MouvementStock> findByUtilisateurIdOrderByCreeLe(Integer utilisateurId);

    // Derniers mouvements (pour tableau de bord)
    @Query("SELECT m FROM MouvementStock m " +
            "JOIN FETCH m.produit " +
            "JOIN FETCH m.uniteConditionnement " +
            "JOIN FETCH m.utilisateur " +
            "ORDER BY m.creeLe DESC")
    List<MouvementStock> findRecentWithRelations();

    // Historique complet d'un produit avec relations
    @Query("SELECT m FROM MouvementStock m " +
            "JOIN FETCH m.produit " +
            "JOIN FETCH m.uniteConditionnement " +
            "JOIN FETCH m.utilisateur " +
            "WHERE m.produit.id = :produitId " +
            "ORDER BY m.creeLe DESC")
    List<MouvementStock> findByProduitIdWithRelations(@Param("produitId") Integer produitId);
}
