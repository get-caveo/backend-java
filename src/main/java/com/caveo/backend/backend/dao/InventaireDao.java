package com.caveo.backend.backend.dao;

import com.caveo.backend.backend.model.Inventaire;
import com.caveo.backend.backend.model.StatutInventaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventaireDao extends JpaRepository<Inventaire, Integer> {

    List<Inventaire> findByStatutOrderByCreeLe(StatutInventaire statut);

    List<Inventaire> findByUtilisateurIdOrderByCreeLe(Integer utilisateurId);

    @Query("SELECT i FROM Inventaire i ORDER BY i.creeLe DESC")
    List<Inventaire> findAllOrderByDateDesc();

    // Inventaire avec ses lignes chargées
    @Query("SELECT DISTINCT i FROM Inventaire i " +
            "LEFT JOIN FETCH i.lignes l " +
            "LEFT JOIN FETCH l.produit " +
            "LEFT JOIN FETCH l.uniteConditionnement " +
            "WHERE i.id = :id")
    Optional<Inventaire> findByIdWithLignes(@Param("id") Integer id);

    // Vérifier s'il y a un inventaire en cours
    @Query("SELECT COUNT(i) > 0 FROM Inventaire i WHERE i.statut = 'EN_COURS'")
    boolean existsInventaireEnCours();
}
