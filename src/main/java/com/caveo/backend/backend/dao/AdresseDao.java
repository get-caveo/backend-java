package com.caveo.backend.backend.dao;

import com.caveo.backend.backend.model.Adresse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdresseDao extends JpaRepository<Adresse, Integer> {
    
    List<Adresse> findByUtilisateurId(Integer utilisateurId);
    List<Adresse> findByUtilisateurIdAndParDefautTrue(Integer utilisateurId);
}
