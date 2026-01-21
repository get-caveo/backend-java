package com.caveo.backend.backend.dao;

import com.caveo.backend.backend.model.CommandeFournisseur;
import com.caveo.backend.backend.model.StatutCommandeFournisseur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommandeFournisseurDao extends JpaRepository<CommandeFournisseur, Integer> {

    List<CommandeFournisseur> findByStatutOrderByCreeLe(StatutCommandeFournisseur statut);

    List<CommandeFournisseur> findByFournisseurIdOrderByCreeLe(Integer fournisseurId);

    @Query("SELECT c FROM CommandeFournisseur c ORDER BY c.creeLe DESC")
    List<CommandeFournisseur> findAllOrderByDateDesc();

    // Commande avec ses lignes
    @Query("SELECT DISTINCT c FROM CommandeFournisseur c " +
            "LEFT JOIN FETCH c.lignes l " +
            "LEFT JOIN FETCH l.produit " +
            "LEFT JOIN FETCH l.uniteConditionnement " +
            "LEFT JOIN FETCH c.fournisseur " +
            "WHERE c.id = :id")
    Optional<CommandeFournisseur> findByIdWithLignes(@Param("id") Integer id);

    // Commandes en attente de réception
    @Query("SELECT c FROM CommandeFournisseur c " +
            "WHERE c.statut IN ('ENVOYEE', 'CONFIRMEE', 'PARTIELLEMENT_RECUE') " +
            "ORDER BY c.dateLivraisonPrevue")
    List<CommandeFournisseur> findCommandesEnAttente();

    // Générer un numéro de commande unique
    @Query("SELECT MAX(c.id) FROM CommandeFournisseur c")
    Integer getMaxId();

    Optional<CommandeFournisseur> findByNumero(String numero);

    boolean existsByNumero(String numero);
}
