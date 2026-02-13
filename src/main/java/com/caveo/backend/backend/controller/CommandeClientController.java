package com.caveo.backend.backend.controller;

import com.caveo.backend.backend.model.CommandeClient;
import com.caveo.backend.backend.model.StatutCommandeClient;
import com.caveo.backend.backend.security.AppUserDetails;
import com.caveo.backend.backend.security.IsClient;
import com.caveo.backend.backend.security.IsEmploye;
import com.caveo.backend.backend.service.CommandeClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/commandes-client")
public class CommandeClientController {

    private final CommandeClientService commandeClientService;

    // ==================== CLIENT ====================

    /**
     * Crée une commande à partir du panier du client connecté.
     */
    @PostMapping
    @IsClient
    public ResponseEntity<CommandeClient> creerCommande(
            @AuthenticationPrincipal AppUserDetails user,
            @RequestParam(required = false) Integer adresseLivraisonId,
            @RequestParam(required = false) Integer adresseFacturationId,
            @RequestParam(required = false) String notes) {

        CommandeClient commande = commandeClientService.creerDepuisPanier(
                user.getUtilisateur().getId(), adresseLivraisonId, adresseFacturationId, notes);
        return new ResponseEntity<>(commande, HttpStatus.CREATED);
    }

    /**
     * Liste les commandes du client connecté.
     */
    @GetMapping("/mes-commandes")
    @IsClient
    public ResponseEntity<List<CommandeClient>> getMesCommandes(
            @AuthenticationPrincipal AppUserDetails user) {
        return ResponseEntity.ok(commandeClientService.getCommandesClient(user.getUtilisateur().getId()));
    }

    // ==================== BACKOFFICE ====================

    /**
     * Liste toutes les commandes (backoffice).
     */
    @GetMapping
    @IsEmploye
    public ResponseEntity<List<CommandeClient>> getAll(
            @RequestParam(required = false) StatutCommandeClient statut) {

        List<CommandeClient> commandes;
        if (statut != null) {
            commandes = commandeClientService.getCommandesByStatut(statut);
        } else {
            commandes = commandeClientService.getAllCommandes();
        }
        return ResponseEntity.ok(commandes);
    }

    /**
     * Détail d'une commande.
     */
    @GetMapping("/{id}")
    @IsClient
    public ResponseEntity<CommandeClient> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(commandeClientService.getCommande(id));
    }

    /**
     * Confirme la commande : EN_ATTENTE → CONFIRMEE.
     */
    @PostMapping("/{id}/confirmer")
    @IsEmploye
    public ResponseEntity<CommandeClient> confirmer(@PathVariable Integer id) {
        return ResponseEntity.ok(commandeClientService.confirmerCommande(id));
    }

    /**
     * Prépare la commande : CONFIRMEE → EN_PREPARATION.
     */
    @PostMapping("/{id}/preparer")
    @IsEmploye
    public ResponseEntity<CommandeClient> preparer(@PathVariable Integer id) {
        return ResponseEntity.ok(commandeClientService.preparerCommande(id));
    }

    /**
     * Expédie la commande : EN_PREPARATION → EXPEDIEE.
     */
    @PostMapping("/{id}/expedier")
    @IsEmploye
    public ResponseEntity<CommandeClient> expedier(
            @PathVariable Integer id,
            @AuthenticationPrincipal AppUserDetails user) {
        return ResponseEntity.ok(commandeClientService.expedierCommande(id, user.getUtilisateur()));
    }

    /**
     * Marque la commande comme livrée : EXPEDIEE → LIVREE.
     */
    @PostMapping("/{id}/livrer")
    @IsEmploye
    public ResponseEntity<CommandeClient> livrer(@PathVariable Integer id) {
        return ResponseEntity.ok(commandeClientService.livrerCommande(id));
    }

    /**
     * Annule la commande.
     */
    @PostMapping("/{id}/annuler")
    @IsClient
    public ResponseEntity<CommandeClient> annuler(@PathVariable Integer id) {
        return ResponseEntity.ok(commandeClientService.annulerCommande(id));
    }
}
