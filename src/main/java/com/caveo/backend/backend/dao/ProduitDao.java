package com.caveo.backend.backend.dao;

import com.caveo.backend.backend.model.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProduitDao extends JpaRepository<Produit, Integer> {

    List<Produit> findByActifTrueOrderByNom();

    List<Produit> findByCategorieIdAndActifTrue(Integer categorieId);

    List<Produit> findByDomaineIdAndActifTrue(Integer domaineId);

    List<Produit> findByMillesimeAndActifTrue(Integer millesime);

    @Query("SELECT p FROM Produit p WHERE p.actif = true AND LOWER(p.nom) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Produit> searchByNom(@Param("search") String search);

    boolean existsBySkuIgnoreCase(String sku);

    boolean existsByCodeBarre(String codeBarre);

    Optional<Produit> findBySku(String sku);

    // Produit avec toutes ses relations (pour le détail)
    @Query("SELECT DISTINCT p FROM Produit p " +
            "LEFT JOIN FETCH p.categorie " +
            "LEFT JOIN FETCH p.domaine " +
            "LEFT JOIN FETCH p.conditionnements c " +
            "LEFT JOIN FETCH c.uniteConditionnement " +
            "LEFT JOIN FETCH p.fournisseurs f " +
            "LEFT JOIN FETCH f.fournisseur " +
            "WHERE p.id = :id")
    Optional<Produit> findByIdWithAllRelations(@Param("id") Integer id);

    // Produits sous le seuil de stock (sera utile pour les alertes)
    @Query("SELECT p FROM Produit p WHERE p.actif = true AND p.reapproAuto = true")
    List<Produit> findProduitsAvecReapproAuto();
}
