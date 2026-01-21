package com.caveo.backend.backend.controller;

import com.caveo.backend.backend.exception.GestionException;
import com.caveo.backend.backend.model.MouvementStock;
import com.caveo.backend.backend.model.StockActuel;
import com.caveo.backend.backend.security.AppUserDetails;
import com.caveo.backend.backend.security.IsEmploye;
import com.caveo.backend.backend.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stock")
@IsEmploye
public class StockController {

    private final StockService stockService;

    // ==================== STOCK ACTUEL ====================

    /**
     * Liste complète du stock actuel
     */
    @GetMapping
    public ResponseEntity<List<StockActuel>> getAll() {
        List<StockActuel> stock = stockService.getStockComplet();
        return ResponseEntity.ok(stock);
    }

    /**
     * Produits sous le seuil minimal (alertes)
     */
    @GetMapping("/alertes")
    public ResponseEntity<List<StockActuel>> getAlertes(
            @RequestParam(required = false, defaultValue = "false") Boolean reapproAutoOnly) {
        
        List<StockActuel> alertes;
        if (Boolean.TRUE.equals(reapproAutoOnly)) {
            alertes = stockService.getAlertesAvecReapproAuto();
        } else {
            alertes = stockService.getAlertes();
        }
        return ResponseEntity.ok(alertes);
    }

    // ==================== MOUVEMENTS ====================

    /**
     * Enregistrer un mouvement de stock (entrée, sortie, ajustement)
     * Met à jour automatiquement le stock_actuel
     */
    @PostMapping("/mouvements")
    public ResponseEntity<MouvementStock> enregistrerMouvement(
            @RequestBody @Valid MouvementStock mouvement,
            @AuthenticationPrincipal AppUserDetails user) {

        if (mouvement.getProduit() == null || mouvement.getProduit().getId() == null) {
            throw GestionException.badRequest("Le produit est obligatoire");
        }
        if (mouvement.getUniteConditionnement() == null || mouvement.getUniteConditionnement().getId() == null) {
            throw GestionException.badRequest("L'unité de conditionnement est obligatoire");
        }
        if (mouvement.getQuantite() == null || mouvement.getQuantite() <= 0) {
            throw GestionException.badRequest("La quantité doit être supérieure à 0");
        }

        // Associer l'utilisateur connecté
        mouvement.setUtilisateur(user.getUtilisateur());

        MouvementStock saved = stockService.enregistrerMouvement(mouvement);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    /**
     * Historique des mouvements d'un produit
     */
    @GetMapping("/mouvements/produit/{produitId}")
    public ResponseEntity<List<MouvementStock>> getHistoriqueProduit(@PathVariable Integer produitId) {
        List<MouvementStock> mouvements = stockService.getHistoriqueMouvements(produitId);
        return ResponseEntity.ok(mouvements);
    }

    /**
     * Derniers mouvements (pour tableau de bord)
     */
    @GetMapping("/mouvements/recents")
    public ResponseEntity<List<MouvementStock>> getDerniersMouvements(
            @RequestParam(required = false, defaultValue = "20") Integer limit) {
        List<MouvementStock> mouvements = stockService.getDerniersMouvements(limit);
        return ResponseEntity.ok(mouvements);
    }

    // ==================== RÉSERVATIONS ====================

    /**
     * Réserver du stock (pour commande client)
     */
    @PostMapping("/reserver")
    public ResponseEntity<Void> reserverStock(
            @RequestParam Integer produitId,
            @RequestParam Integer uniteConditionnementId,
            @RequestParam Integer quantite) {

        stockService.reserverStock(produitId, uniteConditionnementId, quantite);
        return ResponseEntity.ok().build();
    }

    /**
     * Libérer du stock réservé
     */
    @PostMapping("/liberer")
    public ResponseEntity<Void> libererStock(
            @RequestParam Integer produitId,
            @RequestParam Integer uniteConditionnementId,
            @RequestParam Integer quantite) {

        stockService.libererStock(produitId, uniteConditionnementId, quantite);
        return ResponseEntity.ok().build();
    }
}
