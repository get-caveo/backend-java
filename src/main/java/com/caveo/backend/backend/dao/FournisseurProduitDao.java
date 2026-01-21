package com.caveo.backend.backend.dao;

import com.caveo.backend.backend.model.FournisseurProduit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FournisseurProduitDao extends JpaRepository<FournisseurProduit, Integer> {

    List<FournisseurProduit> findByProduitId(Integer produitId);

    List<FournisseurProduit> findByFournisseurId(Integer fournisseurId);

    Optional<FournisseurProduit> findByProduitIdAndFournisseurId(Integer produitId, Integer fournisseurId);

    boolean existsByProduitIdAndFournisseurId(Integer produitId, Integer fournisseurId);

    void deleteByProduitId(Integer produitId);

    // Trouver le fournisseur avec le meilleur prix pour un produit
    @Query("SELECT fp FROM FournisseurProduit fp WHERE fp.produit.id = :produitId ORDER BY fp.prixFournisseur ASC")
    List<FournisseurProduit> findByProduitIdOrderByPrixAsc(@Param("produitId") Integer produitId);

    // Trouver le fournisseur avec le délai le plus court pour un produit
    @Query("SELECT fp FROM FournisseurProduit fp WHERE fp.produit.id = :produitId ORDER BY fp.delaiApproJours ASC")
    List<FournisseurProduit> findByProduitIdOrderByDelaiAsc(@Param("produitId") Integer produitId);
}
