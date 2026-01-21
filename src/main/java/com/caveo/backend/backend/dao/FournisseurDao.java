package com.caveo.backend.backend.dao;

import com.caveo.backend.backend.model.Fournisseur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FournisseurDao extends JpaRepository<Fournisseur, Integer> {

    List<Fournisseur> findByCertificationBioTrue();
    
    List<Fournisseur> findByCertificationAocTrue();
    
    boolean existsByNomIgnoreCase(String nom);
    
    boolean existsByEmail(String email);
}
