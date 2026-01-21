package com.caveo.backend.backend.dao;

import com.caveo.backend.backend.model.ConditionnementProduit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConditionnementProduitDao extends JpaRepository<ConditionnementProduit, Integer> {

    List<ConditionnementProduit> findByProduitId(Integer produitId);

    List<ConditionnementProduit> findByProduitIdAndDisponibleTrue(Integer produitId);

    Optional<ConditionnementProduit> findByProduitIdAndUniteConditionnementId(
            Integer produitId, Integer uniteConditionnementId);

    boolean existsByProduitIdAndUniteConditionnementId(
            Integer produitId, Integer uniteConditionnementId);

    void deleteByProduitId(Integer produitId);
}
