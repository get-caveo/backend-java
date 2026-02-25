package com.caveo.backend.backend.dao;

import com.caveo.backend.backend.model.ConditionnementProduit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConditionnementProduitDao extends JpaRepository<ConditionnementProduit, Integer> {

    List<ConditionnementProduit> findByProduitId(Integer produitId);

    List<ConditionnementProduit> findByProduitIdAndDisponibleTrue(Integer produitId);

    Optional<ConditionnementProduit> findFirstByProduitIdAndUniteConditionnementId(
            Integer produitId, Integer uniteConditionnementId);

    boolean existsByProduitIdAndUniteConditionnementId(
            Integer produitId, Integer uniteConditionnementId);

    void deleteByProduitId(Integer produitId);

    Optional<ConditionnementProduit> findByCodeBarre(String codeBarre);

    @Query("SELECT cp FROM ConditionnementProduit cp " +
            "LEFT JOIN FETCH cp.produit p " +
            "LEFT JOIN FETCH cp.uniteConditionnement " +
            "LEFT JOIN FETCH p.categorie " +
            "WHERE cp.codeBarre = :codeBarre")
    Optional<ConditionnementProduit> findByCodeBarreWithDetails(@Param("codeBarre") String codeBarre);
}
