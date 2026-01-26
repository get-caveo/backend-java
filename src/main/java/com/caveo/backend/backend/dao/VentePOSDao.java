package com.caveo.backend.backend.dao;

import com.caveo.backend.backend.model.StatutVentePOS;
import com.caveo.backend.backend.model.VentePOS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VentePOSDao extends JpaRepository<VentePOS, Integer> {

    List<VentePOS> findByStatutOrderByCreeLe(StatutVentePOS statut);

    List<VentePOS> findByUtilisateurIdOrderByCreeLe(Integer utilisateurId);

    @Query("SELECT v FROM VentePOS v ORDER BY v.creeLe DESC")
    List<VentePOS> findAllOrderByDateDesc();

    @Query("SELECT v FROM VentePOS v WHERE v.dateVente BETWEEN :debut AND :fin ORDER BY v.dateVente DESC")
    List<VentePOS> findByDateVenteBetween(@Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);

    @Query("SELECT DISTINCT v FROM VentePOS v " +
            "LEFT JOIN FETCH v.lignes l " +
            "LEFT JOIN FETCH l.produit " +
            "LEFT JOIN FETCH l.uniteConditionnement " +
            "LEFT JOIN FETCH v.paiements " +
            "LEFT JOIN FETCH v.utilisateur " +
            "WHERE v.id = :id")
    Optional<VentePOS> findByIdWithDetails(@Param("id") Integer id);

    @Query("SELECT v FROM VentePOS v " +
            "WHERE v.statut = 'BROUILLON' " +
            "AND v.utilisateur.id = :utilisateurId " +
            "ORDER BY v.creeLe DESC")
    List<VentePOS> findVentesBrouillonByUtilisateur(@Param("utilisateurId") Integer utilisateurId);

    @Query("SELECT v FROM VentePOS v " +
            "WHERE v.statut = 'PAYEE' " +
            "AND v.dateVente >= :debutJour " +
            "ORDER BY v.dateVente DESC")
    List<VentePOS> findVentesPayeesAujourdHui(@Param("debutJour") LocalDateTime debutJour);

    @Query("SELECT MAX(v.id) FROM VentePOS v")
    Integer getMaxId();

    Optional<VentePOS> findByNumero(String numero);

    boolean existsByNumero(String numero);

    @Query("SELECT COALESCE(SUM(v.montantTotal), 0) FROM VentePOS v " +
            "WHERE v.statut = 'PAYEE' AND v.dateVente >= :debutJour")
    java.math.BigDecimal getTotalVentesJour(@Param("debutJour") LocalDateTime debutJour);

    @Query("SELECT COUNT(v) FROM VentePOS v " +
            "WHERE v.statut = 'PAYEE' AND v.dateVente >= :debutJour")
    Long getNombreVentesJour(@Param("debutJour") LocalDateTime debutJour);
}
