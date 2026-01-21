package com.caveo.backend.backend.dao;

import com.caveo.backend.backend.model.Domaine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DomaineDao extends JpaRepository<Domaine, Integer> {

    List<Domaine> findByActifTrueOrderByNom();
    
    List<Domaine> findByRegionIgnoreCaseAndActifTrue(String region);
    
    List<Domaine> findByAppellationIgnoreCaseAndActifTrue(String appellation);
    
    boolean existsByNomIgnoreCase(String nom);
}
