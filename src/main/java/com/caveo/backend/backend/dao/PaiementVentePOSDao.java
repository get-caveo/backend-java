package com.caveo.backend.backend.dao;

import com.caveo.backend.backend.model.PaiementVentePOS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaiementVentePOSDao extends JpaRepository<PaiementVentePOS, Integer> {

    List<PaiementVentePOS> findByVentePOSId(Integer ventePOSId);

    @Query("SELECT COALESCE(SUM(p.montant), 0) FROM PaiementVentePOS p WHERE p.ventePOS.id = :ventePOSId")
    BigDecimal sumMontantByVentePOSId(@Param("ventePOSId") Integer ventePOSId);

    @Query("SELECT p.modePaiement, SUM(p.montant) FROM PaiementVentePOS p " +
            "WHERE p.ventePOS.statut = 'PAYEE' " +
            "AND p.creeLe >= :debutJour " +
            "GROUP BY p.modePaiement")
    List<Object[]> getTotauxParModePaiementJour(@Param("debutJour") LocalDateTime debutJour);

    void deleteByVentePOSId(Integer ventePOSId);
}
