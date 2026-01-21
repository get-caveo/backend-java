package com.caveo.backend.backend.dao;

import com.caveo.backend.backend.model.UniteConditionnement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UniteConditionnementDao extends JpaRepository<UniteConditionnement, Integer> {

    List<UniteConditionnement> findByActifTrueOrderByOrdreTri();
    
    List<UniteConditionnement> findByEstVendableTrueAndActifTrueOrderByOrdreTri();
    
    UniteConditionnement findByEstUniteBaseTrueAndActifTrue();
    
    boolean existsByNomIgnoreCase(String nom);
    
    boolean existsByNomCourtIgnoreCase(String nomCourt);
}
