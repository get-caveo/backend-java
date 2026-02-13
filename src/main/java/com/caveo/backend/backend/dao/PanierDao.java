package com.caveo.backend.backend.dao;

import com.caveo.backend.backend.model.Panier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PanierDao extends JpaRepository<Panier, Integer> {

    Optional<Panier> findByClientId(Integer clientId);

    Optional<Panier> findBySessionId(String sessionId);

    @Query("SELECT DISTINCT p FROM Panier p " +
            "LEFT JOIN FETCH p.lignes l " +
            "LEFT JOIN FETCH l.produit " +
            "LEFT JOIN FETCH l.uniteConditionnement " +
            "WHERE p.client.id = :clientId")
    Optional<Panier> findByClientIdWithDetails(@Param("clientId") Integer clientId);

    @Query("SELECT DISTINCT p FROM Panier p " +
            "LEFT JOIN FETCH p.lignes l " +
            "LEFT JOIN FETCH l.produit " +
            "LEFT JOIN FETCH l.uniteConditionnement " +
            "WHERE p.sessionId = :sessionId")
    Optional<Panier> findBySessionIdWithDetails(@Param("sessionId") String sessionId);
}
