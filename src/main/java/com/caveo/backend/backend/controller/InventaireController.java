package com.caveo.backend.backend.controller;

import com.caveo.backend.backend.dto.LigneInventaireUpdateDto;
import com.caveo.backend.backend.model.Inventaire;
import com.caveo.backend.backend.model.LigneInventaire;
import com.caveo.backend.backend.model.StatutInventaire;
import com.caveo.backend.backend.security.AppUserDetails;
import com.caveo.backend.backend.security.IsEmploye;
import com.caveo.backend.backend.service.InventaireService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inventaires")
@IsEmploye
public class InventaireController {

    private final InventaireService inventaireService;

    // ==================== CRUD INVENTAIRE ====================

    @GetMapping
    public ResponseEntity<List<Inventaire>> getAll(
            @RequestParam(required = false) StatutInventaire statut) {
        
        List<Inventaire> inventaires;
        if (statut != null) {
            inventaires = inventaireService.getInventairesByStatut(statut);
        } else {
            inventaires = inventaireService.getAllInventaires();
        }
        return ResponseEntity.ok(inventaires);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inventaire> getById(@PathVariable Integer id) {
        Inventaire inventaire = inventaireService.getInventaireAvecLignes(id);
        return ResponseEntity.ok(inventaire);
    }

    @PostMapping
    public ResponseEntity<Inventaire> create(
            @RequestBody @Valid Inventaire inventaire,
            @AuthenticationPrincipal AppUserDetails user) {
        
        Inventaire saved = inventaireService.creerInventaire(inventaire, user.getUtilisateur());
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Inventaire> update(
            @PathVariable Integer id,
            @RequestBody @Valid Inventaire inventaireEnvoye) {
        
        Inventaire updated = inventaireService.mettreAJour(id, inventaireEnvoye);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        inventaireService.supprimerInventaire(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== WORKFLOW ====================

    /**
     * Démarre l'inventaire : génère les lignes depuis le stock actuel.
     * BROUILLON → EN_COURS
     */
    @PostMapping("/{id}/demarrer")
    public ResponseEntity<Inventaire> demarrer(@PathVariable Integer id) {
        Inventaire inventaire = inventaireService.demarrerInventaire(id);
        return ResponseEntity.ok(inventaire);
    }

    /**
     * Termine l'inventaire : applique les différences au stock.
     * EN_COURS → TERMINE
     */
    @PostMapping("/{id}/terminer")
    public ResponseEntity<Inventaire> terminer(
            @PathVariable Integer id,
            @AuthenticationPrincipal AppUserDetails user) {
        
        Inventaire inventaire = inventaireService.terminerInventaire(id, user.getUtilisateur());
        return ResponseEntity.ok(inventaire);
    }

    /**
     * Annule l'inventaire.
     * * → ANNULE
     */
    @PostMapping("/{id}/annuler")
    public ResponseEntity<Inventaire> annuler(@PathVariable Integer id) {
        Inventaire inventaire = inventaireService.annulerInventaire(id);
        return ResponseEntity.ok(inventaire);
    }

    // ==================== LIGNES ====================

    @GetMapping("/{id}/lignes")
    public ResponseEntity<List<LigneInventaire>> getLignes(@PathVariable Integer id) {
        List<LigneInventaire> lignes = inventaireService.getLignes(id);
        return ResponseEntity.ok(lignes);
    }

    @GetMapping("/{id}/lignes/ecarts")
    public ResponseEntity<List<LigneInventaire>> getLignesAvecEcart(@PathVariable Integer id) {
        List<LigneInventaire> lignes = inventaireService.getLignesAvecEcart(id);
        return ResponseEntity.ok(lignes);
    }

    /**
     * Met à jour une ligne d'inventaire (enregistre le comptage).
     */
    @PutMapping("/{id}/lignes/{ligneId}")
    public ResponseEntity<LigneInventaire> updateLigne(
            @PathVariable Integer id,
            @PathVariable Integer ligneId,
            @RequestBody LigneInventaireUpdateDto dto,
            @AuthenticationPrincipal AppUserDetails user) {
        
        LigneInventaire ligne = inventaireService.mettreAJourLigne(
                id, ligneId, dto.getQuantiteComptee(), dto.getNotes(), user.getUtilisateur());
        return ResponseEntity.ok(ligne);
    }
}
