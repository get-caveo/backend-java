package com.caveo.backend.backend.dao;

import com.caveo.backend.backend.model.LignePanier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LignePanierDao extends JpaRepository<LignePanier, Integer> {

    List<LignePanier> findByPanierId(Integer panierId);

    @Query("SELECT l FROM LignePanier l " +
            "WHERE l.panier.id = :panierId " +
            "AND l.produit.id = :produitId " +
            "AND l.uniteConditionnement.id = :uniteId")
    Optional<LignePanier> findByPanierAndProduitAndUnite(
            @Param("panierId") Integer panierId,
            @Param("produitId") Integer produitId,
            @Param("uniteId") Integer uniteId);

    void deleteByPanierId(Integer panierId);
}
