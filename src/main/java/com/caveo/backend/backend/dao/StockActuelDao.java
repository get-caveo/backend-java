package com.caveo.backend.backend.dao;

import com.caveo.backend.backend.model.StockActuel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockActuelDao extends JpaRepository<StockActuel, Integer> {

    List<StockActuel> findByProduitId(Integer produitId);

    Optional<StockActuel> findByProduitIdAndUniteConditionnementId(Integer produitId, Integer uniteConditionnementId);

    // Stock avec quantité disponible > 0
    @Query("SELECT s FROM StockActuel s WHERE s.quantiteDisponible > 0")
    List<StockActuel> findStockDisponible();

    // Produits sous le seuil minimal (pour alertes)
    @Query("SELECT s FROM StockActuel s " +
            "JOIN s.produit p " +
            "WHERE p.actif = true " +
            "AND s.quantite <= p.seuilStockMinimal")
    List<StockActuel> findStockSousSeuil();

    // Compter les produits sous le seuil minimal
    @Query("SELECT COUNT(s) FROM StockActuel s " +
            "JOIN s.produit p " +
            "WHERE p.actif = true " +
            "AND s.quantite <= p.seuilStockMinimal")
    long countStockSousSeuil();

    // Produits sous le seuil avec réappro auto activé (pour commandes automatiques)
    @Query("SELECT s FROM StockActuel s " +
            "JOIN s.produit p " +
            "WHERE p.actif = true " +
            "AND p.reapproAuto = true " +
            "AND s.quantite <= p.seuilStockMinimal")
    List<StockActuel> findStockSousSeuilAvecReapproAuto();

    // Stock total en unité de base pour un produit (somme de tous les conditionnements)
    @Query("SELECT COALESCE(SUM(s.quantiteUniteBase), 0) FROM StockActuel s WHERE s.produit.id = :produitId")
    Integer getTotalUniteBaseByProduitId(@Param("produitId") Integer produitId);

    // Stock par produit avec relations chargées
    @Query("SELECT s FROM StockActuel s " +
            "JOIN FETCH s.produit p " +
            "JOIN FETCH s.uniteConditionnement " +
            "WHERE p.actif = true " +
            "ORDER BY p.nom, s.uniteConditionnement.ordreTri")
    List<StockActuel> findAllWithRelations();
}
