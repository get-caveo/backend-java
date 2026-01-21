package com.caveo.backend.backend.dao;

import com.caveo.backend.backend.model.LigneVentePOS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LigneVentePOSDao extends JpaRepository<LigneVentePOS, Integer> {

    List<LigneVentePOS> findByVentePOSId(Integer ventePOSId);

    @Query("SELECT l FROM LigneVentePOS l " +
            "LEFT JOIN FETCH l.produit " +
            "LEFT JOIN FETCH l.uniteConditionnement " +
            "WHERE l.ventePOS.id = :ventePOSId")
    List<LigneVentePOS> findByVentePOSIdWithDetails(@Param("ventePOSId") Integer ventePOSId);

    @Query("SELECT l FROM LigneVentePOS l " +
            "WHERE l.ventePOS.id = :ventePOSId " +
            "AND l.produit.id = :produitId " +
            "AND l.uniteConditionnement.id = :uniteId")
    Optional<LigneVentePOS> findByVenteAndProduitAndUnite(
            @Param("ventePOSId") Integer ventePOSId,
            @Param("produitId") Integer produitId,
            @Param("uniteId") Integer uniteId);

    void deleteByVentePOSId(Integer ventePOSId);
}
