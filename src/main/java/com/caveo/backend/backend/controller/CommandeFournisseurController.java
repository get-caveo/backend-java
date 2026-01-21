package com.caveo.backend.backend.controller;

import com.caveo.backend.backend.dto.LigneCommandeCreateDto;
import com.caveo.backend.backend.dto.ReceptionDto;
import com.caveo.backend.backend.model.CommandeFournisseur;
import com.caveo.backend.backend.model.LigneCommandeFournisseur;
import com.caveo.backend.backend.model.StatutCommandeFournisseur;
import com.caveo.backend.backend.security.AppUserDetails;
import com.caveo.backend.backend.security.IsEmploye;
import com.caveo.backend.backend.service.CommandeFournisseurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/commandes-fournisseur")
@IsEmploye
public class CommandeFournisseurController {

    private final CommandeFournisseurService commandeFournisseurService;

    // ==================== CRUD ====================

    @GetMapping
    public ResponseEntity<List<CommandeFournisseur>> getAll(
            @RequestParam(required = false) StatutCommandeFournisseur statut) {
        
        List<CommandeFournisseur> commandes;
        if (statut != null) {
            commandes = commandeFournisseurService.getCommandesByStatut(statut);
        } else {
            commandes = commandeFournisseurService.getAllCommandes();
        }
        return ResponseEntity.ok(commandes);
    }

    @GetMapping("/en-attente")
    public ResponseEntity<List<CommandeFournisseur>> getEnAttente() {
        List<CommandeFournisseur> commandes = commandeFournisseurService.getCommandesEnAttente();
        return ResponseEntity.ok(commandes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommandeFournisseur> getById(@PathVariable Integer id) {
        CommandeFournisseur commande = commandeFournisseurService.getCommandeAvecLignes(id);
        return ResponseEntity.ok(commande);
    }

    @PostMapping
    public ResponseEntity<CommandeFournisseur> create(
            @RequestParam Integer fournisseurId,
            @RequestParam(required = false) String notes,
            @AuthenticationPrincipal AppUserDetails user) {
        
        CommandeFournisseur commande = commandeFournisseurService.creerCommande(
                fournisseurId, notes, user.getUtilisateur());
        return new ResponseEntity<>(commande, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommandeFournisseur> update(
            @PathVariable Integer id,
            @RequestParam(required = false) LocalDateTime dateLivraisonPrevue,
            @RequestParam(required = false) String notes) {
        
        CommandeFournisseur commande = commandeFournisseurService.mettreAJour(id, dateLivraisonPrevue, notes);
        return ResponseEntity.ok(commande);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        commandeFournisseurService.supprimerCommande(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== LIGNES ====================

    @PostMapping("/{id}/lignes")
    public ResponseEntity<LigneCommandeFournisseur> addLigne(
            @PathVariable Integer id,
            @RequestBody @Valid LigneCommandeCreateDto dto) {
        
        LigneCommandeFournisseur ligne = commandeFournisseurService.ajouterLigne(id, dto);
        return new ResponseEntity<>(ligne, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}/lignes/{ligneId}")
    public ResponseEntity<Void> deleteLigne(
            @PathVariable Integer id,
            @PathVariable Integer ligneId) {
        
        commandeFournisseurService.supprimerLigne(id, ligneId);
        return ResponseEntity.noContent().build();
    }

    // ==================== WORKFLOW ====================

    /**
     * Envoie la commande au fournisseur.
     * BROUILLON → ENVOYEE
     */
    @PostMapping("/{id}/envoyer")
    public ResponseEntity<CommandeFournisseur> envoyer(@PathVariable Integer id) {
        CommandeFournisseur commande = commandeFournisseurService.envoyerCommande(id);
        return ResponseEntity.ok(commande);
    }

    /**
     * Confirme la commande (le fournisseur a accepté).
     * ENVOYEE → CONFIRMEE
     */
    @PostMapping("/{id}/confirmer")
    public ResponseEntity<CommandeFournisseur> confirmer(
            @PathVariable Integer id,
            @RequestParam(required = false) LocalDateTime dateLivraisonPrevue) {
        
        CommandeFournisseur commande = commandeFournisseurService.confirmerCommande(id, dateLivraisonPrevue);
        return ResponseEntity.ok(commande);
    }

    /**
     * Réceptionne des produits de la commande.
     * Met à jour le stock automatiquement.
     * ENVOYEE/CONFIRMEE/PARTIELLEMENT_RECUE → PARTIELLEMENT_RECUE/RECUE
     */
    @PostMapping("/{id}/reception")
    public ResponseEntity<CommandeFournisseur> reception(
            @PathVariable Integer id,
            @RequestBody @Valid ReceptionDto dto,
            @AuthenticationPrincipal AppUserDetails user) {
        
        CommandeFournisseur commande = commandeFournisseurService.receptionner(
                id, dto.getLignes(), user.getUtilisateur());
        return ResponseEntity.ok(commande);
    }

    /**
     * Annule la commande.
     * * → ANNULEE (sauf si déjà RECUE ou PARTIELLEMENT_RECUE)
     */
    @PostMapping("/{id}/annuler")
    public ResponseEntity<CommandeFournisseur> annuler(@PathVariable Integer id) {
        CommandeFournisseur commande = commandeFournisseurService.annulerCommande(id);
        return ResponseEntity.ok(commande);
    }

    // ==================== COMMANDES AUTOMATIQUES ====================

    /**
     * Déclenche la création de commandes automatiques pour tous les produits sous seuil.
     */
    @PostMapping("/auto/generer")
    public ResponseEntity<List<CommandeFournisseur>> genererCommandesAuto(
            @AuthenticationPrincipal AppUserDetails user) {
        
        List<CommandeFournisseur> commandes = commandeFournisseurService
                .creerCommandesAutomatiquesGroupees(user.getUtilisateur());
        return ResponseEntity.ok(commandes);
    }
}
