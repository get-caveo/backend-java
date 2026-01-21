package com.caveo.backend.backend.controller;

import com.caveo.backend.backend.dao.FournisseurDao;
import com.caveo.backend.backend.dao.FournisseurProduitDao;
import com.caveo.backend.backend.exception.GestionException;
import com.caveo.backend.backend.model.Fournisseur;
import com.caveo.backend.backend.model.FournisseurProduit;
import com.caveo.backend.backend.security.IsEmploye;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fournisseurs")
@IsEmploye
public class FournisseurController {

    private final FournisseurDao fournisseurDao;
    private final FournisseurProduitDao fournisseurProduitDao;

    @GetMapping
    public ResponseEntity<List<Fournisseur>> getAll(
            @RequestParam(required = false) Boolean bio,
            @RequestParam(required = false) Boolean aoc) {

        List<Fournisseur> fournisseurs;

        if (Boolean.TRUE.equals(bio)) {
            fournisseurs = fournisseurDao.findByCertificationBioTrue();
        } else if (Boolean.TRUE.equals(aoc)) {
            fournisseurs = fournisseurDao.findByCertificationAocTrue();
        } else {
            fournisseurs = fournisseurDao.findAll();
        }

        return ResponseEntity.ok(fournisseurs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Fournisseur> getById(@PathVariable Integer id) {
        Fournisseur fournisseur = fournisseurDao.findById(id)
                .orElseThrow(() -> GestionException.notFound("Fournisseur", id));
        return ResponseEntity.ok(fournisseur);
    }

    @PostMapping
    public ResponseEntity<Fournisseur> create(@RequestBody @Valid Fournisseur fournisseur) {
        if (fournisseurDao.existsByNomIgnoreCase(fournisseur.getNom())) {
            throw GestionException.conflict("Un fournisseur avec le nom '" + fournisseur.getNom() + "' existe déjà");
        }

        if (fournisseur.getEmail() != null && !fournisseur.getEmail().isBlank()
                && fournisseurDao.existsByEmail(fournisseur.getEmail())) {
            throw GestionException.conflict("Un fournisseur avec l'email '" + fournisseur.getEmail() + "' existe déjà");
        }

        Fournisseur saved = fournisseurDao.save(fournisseur);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Fournisseur> update(
            @PathVariable Integer id,
            @RequestBody @Valid Fournisseur fournisseurEnvoye) {

        Fournisseur fournisseurExistant = fournisseurDao.findById(id)
                .orElseThrow(() -> GestionException.notFound("Fournisseur", id));

        // Vérifier les doublons de nom
        if (!fournisseurExistant.getNom().equalsIgnoreCase(fournisseurEnvoye.getNom())
                && fournisseurDao.existsByNomIgnoreCase(fournisseurEnvoye.getNom())) {
            throw GestionException.conflict("Un fournisseur avec le nom '" + fournisseurEnvoye.getNom() + "' existe déjà");
        }

        // Vérifier les doublons d'email
        if (fournisseurEnvoye.getEmail() != null && !fournisseurEnvoye.getEmail().isBlank()) {
            String existingEmail = fournisseurExistant.getEmail();
            if ((existingEmail == null || !existingEmail.equalsIgnoreCase(fournisseurEnvoye.getEmail()))
                    && fournisseurDao.existsByEmail(fournisseurEnvoye.getEmail())) {
                throw GestionException.conflict("Un fournisseur avec l'email '" + fournisseurEnvoye.getEmail() + "' existe déjà");
            }
        }

        fournisseurExistant.setNom(fournisseurEnvoye.getNom());
        fournisseurExistant.setPersonneContact(fournisseurEnvoye.getPersonneContact());
        fournisseurExistant.setEmail(fournisseurEnvoye.getEmail());
        fournisseurExistant.setTelephone(fournisseurEnvoye.getTelephone());
        fournisseurExistant.setAdresse(fournisseurEnvoye.getAdresse());
        fournisseurExistant.setConditionsPaiement(fournisseurEnvoye.getConditionsPaiement());
        fournisseurExistant.setCertificationBio(fournisseurEnvoye.getCertificationBio());
        fournisseurExistant.setCertificationAoc(fournisseurEnvoye.getCertificationAoc());
        fournisseurExistant.setCertificationsAutres(fournisseurEnvoye.getCertificationsAutres());

        Fournisseur updated = fournisseurDao.save(fournisseurExistant);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        Fournisseur fournisseur = fournisseurDao.findById(id)
                .orElseThrow(() -> GestionException.notFound("Fournisseur", id));

        // Hard delete per i fournisseurs (non hanno campo actif)
        // Nota: fallirà se ci sono prodotti associati (foreign key)
        fournisseurDao.delete(fournisseur);

        return ResponseEntity.noContent().build();
    }

    // ==================== PRODUITS DU FOURNISSEUR ====================

    @GetMapping("/{id}/produits")
    public ResponseEntity<List<FournisseurProduit>> getProduits(@PathVariable Integer id) {
        if (!fournisseurDao.existsById(id)) {
            throw GestionException.notFound("Fournisseur", id);
        }
        List<FournisseurProduit> produits = fournisseurProduitDao.findByFournisseurId(id);
        return ResponseEntity.ok(produits);
    }
}
