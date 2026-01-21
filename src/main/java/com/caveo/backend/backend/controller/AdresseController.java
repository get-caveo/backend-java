package com.caveo.backend.backend.controller;

import com.caveo.backend.backend.dao.AdresseDao;
import com.caveo.backend.backend.exception.GestionException;
import com.caveo.backend.backend.model.Adresse;
import com.caveo.backend.backend.security.AppUserDetails;
import com.caveo.backend.backend.security.IsClient;
import com.caveo.backend.backend.view.UtilisateurView;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/adresse")
@IsClient
public class AdresseController {

    private final AdresseDao adresseDao;

    // Récupérer toutes les adresses de l'utilisateur connecté
    @GetMapping("/liste")
    @JsonView(UtilisateurView.class)
    public ResponseEntity<List<Adresse>> getAllByUser(@AuthenticationPrincipal AppUserDetails user) {
        List<Adresse> adresses = adresseDao.findByUtilisateurId(user.getUtilisateur().getId());
        return ResponseEntity.ok(adresses);
    }

    // Récupérer une adresse par ID (uniquement si elle appartient à l'utilisateur connecté)
    @GetMapping("/{id}")
    @JsonView(UtilisateurView.class)
    public ResponseEntity<Adresse> getById(
            @PathVariable int id,
            @AuthenticationPrincipal AppUserDetails user) {

        Adresse adresse = adresseDao.findById(id)
                .orElseThrow(() -> GestionException.notFound("Adresse", id));

        if (!adresse.getUtilisateur().getId().equals(user.getUtilisateur().getId())) {
            throw GestionException.forbidden("Cette adresse ne vous appartient pas");
        }

        return ResponseEntity.ok(adresse);
    }

    // Créer une nouvelle adresse pour l'utilisateur connecté
    @PostMapping
    @JsonView(UtilisateurView.class)
    public ResponseEntity<Adresse> create(
            @RequestBody @Valid Adresse adresse,
            @AuthenticationPrincipal AppUserDetails user) {

        adresse.setUtilisateur(user.getUtilisateur());
        
        // Si c'est la première adresse ou si parDefaut est true, vérifier les autres adresses
        if (adresse.getParDefaut()) {
            resetDefaultAddresses(user.getUtilisateur().getId());
        }

        Adresse savedAdresse = adresseDao.save(adresse);
        return new ResponseEntity<>(savedAdresse, HttpStatus.CREATED);
    }

    // Modifier une adresse existante (uniquement si elle appartient à l'utilisateur connecté)
    @PutMapping("/{id}")
    @JsonView(UtilisateurView.class)
    public ResponseEntity<Adresse> update(
            @PathVariable int id,
            @RequestBody @Valid Adresse adresseEnvoye,
            @AuthenticationPrincipal AppUserDetails user) {

        Adresse adresseBaseDeDonnees = adresseDao.findById(id)
                .orElseThrow(() -> GestionException.notFound("Adresse", id));

        if (!adresseBaseDeDonnees.getUtilisateur().getId().equals(user.getUtilisateur().getId())) {
            throw GestionException.forbidden("Cette adresse ne vous appartient pas");
        }

        adresseBaseDeDonnees.setType(adresseEnvoye.getType());
        adresseBaseDeDonnees.setRue(adresseEnvoye.getRue());
        adresseBaseDeDonnees.setVille(adresseEnvoye.getVille());
        adresseBaseDeDonnees.setCodePostal(adresseEnvoye.getCodePostal());
        adresseBaseDeDonnees.setPays(adresseEnvoye.getPays());

        // Si on définit cette adresse comme par défaut, retirer le défaut des autres
        if (adresseEnvoye.getParDefaut() && !adresseBaseDeDonnees.getParDefaut()) {
            resetDefaultAddresses(user.getUtilisateur().getId());
        }
        adresseBaseDeDonnees.setParDefaut(adresseEnvoye.getParDefaut());

        adresseDao.save(adresseBaseDeDonnees);
        return ResponseEntity.ok(adresseBaseDeDonnees);
    }

    // Supprimer une adresse (uniquement si elle appartient à l'utilisateur connecté)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable int id,
            @AuthenticationPrincipal AppUserDetails user) {

        Adresse adresse = adresseDao.findById(id)
                .orElseThrow(() -> GestionException.notFound("Adresse", id));

        if (!adresse.getUtilisateur().getId().equals(user.getUtilisateur().getId())) {
            throw GestionException.forbidden("Cette adresse ne vous appartient pas");
        }

        adresseDao.delete(adresse);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Méthode utilitaire pour retirer le flag parDefaut des autres adresses
    private void resetDefaultAddresses(Integer utilisateurId) {
        List<Adresse> adresses = adresseDao.findByUtilisateurId(utilisateurId);
        adresses.forEach(a -> a.setParDefaut(false));
        adresseDao.saveAll(adresses);
    }
}
