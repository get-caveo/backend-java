package com.caveo.backend.backend.dao;

import com.caveo.backend.backend.model.CommandeClient;
import com.caveo.backend.backend.model.StatutCommandeClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommandeClientDao extends JpaRepository<CommandeClient, Integer> {

    List<CommandeClient> findByClientIdOrderByCreeLeDesc(Integer clientId);

    List<CommandeClient> findByStatutCommandeOrderByCreeLeDesc(StatutCommandeClient statut);

    @Query("SELECT c FROM CommandeClient c ORDER BY c.creeLe DESC")
    List<CommandeClient> findAllOrderByDateDesc();

    @Query("SELECT DISTINCT c FROM CommandeClient c " +
            "LEFT JOIN FETCH c.lignes l " +
            "LEFT JOIN FETCH l.produit " +
            "LEFT JOIN FETCH l.uniteConditionnement " +
            "LEFT JOIN FETCH c.client " +
            "LEFT JOIN FETCH c.adresseLivraison " +
            "LEFT JOIN FETCH c.adresseFacturation " +
            "WHERE c.id = :id")
    Optional<CommandeClient> findByIdWithDetails(@Param("id") Integer id);

    Optional<CommandeClient> findByNumero(String numero);

    boolean existsByNumero(String numero);
}
