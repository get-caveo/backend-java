package com.caveo.backend.backend.controller;

import com.caveo.backend.backend.dto.*;
import com.caveo.backend.backend.model.ConditionnementProduit;
import com.caveo.backend.backend.model.LigneVentePOS;
import com.caveo.backend.backend.model.PaiementVentePOS;
import com.caveo.backend.backend.model.VentePOS;
import com.caveo.backend.backend.security.AppUserDetails;
import com.caveo.backend.backend.security.IsEmploye;
import com.caveo.backend.backend.service.VentePOSService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pos")
@IsEmploye
public class VentePOSController {

    private final VentePOSService ventePOSService;

    // ==================== GESTION DES VENTES ====================

    @PostMapping("/ventes")
    public ResponseEntity<VentePOS> creerVente(@AuthenticationPrincipal AppUserDetails user) {
        VentePOS vente = ventePOSService.creerVente(user.getUtilisateur());
        return new ResponseEntity<>(vente, HttpStatus.CREATED);
    }

    @GetMapping("/ventes/{id}")
    public ResponseEntity<VentePOS> getVente(@PathVariable Integer id) {
        VentePOS vente = ventePOSService.getVente(id);
        return ResponseEntity.ok(vente);
    }

    @GetMapping("/ventes/jour")
    public ResponseEntity<List<VentePOS>> getVentesJour() {
        List<VentePOS> ventes = ventePOSService.getVentesJour();
        return ResponseEntity.ok(ventes);
    }

    @GetMapping("/ventes/brouillon")
    public ResponseEntity<List<VentePOS>> getVentesBrouillon(@AuthenticationPrincipal AppUserDetails user) {
        List<VentePOS> ventes = ventePOSService.getVentesBrouillon(user.getUtilisateur().getId());
        return ResponseEntity.ok(ventes);
    }

    @DeleteMapping("/ventes/{id}")
    public ResponseEntity<Void> annulerVente(@PathVariable Integer id) {
        ventePOSService.annulerVente(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== GESTION DES LIGNES ====================

    @PostMapping("/ventes/{id}/lignes")
    public ResponseEntity<LigneVentePOS> ajouterLigne(
            @PathVariable Integer id,
            @RequestBody @Valid LigneVentePOSDto dto) {
        LigneVentePOS ligne = ventePOSService.ajouterLigne(id, dto);
        return new ResponseEntity<>(ligne, HttpStatus.CREATED);
    }

    @PutMapping("/ventes/{venteId}/lignes/{ligneId}")
    public ResponseEntity<LigneVentePOS> modifierQuantite(
            @PathVariable Integer venteId,
            @PathVariable Integer ligneId,
            @RequestParam Integer quantite) {
        LigneVentePOS ligne = ventePOSService.modifierQuantiteLigne(venteId, ligneId, quantite);
        return ResponseEntity.ok(ligne);
    }

    @DeleteMapping("/ventes/{venteId}/lignes/{ligneId}")
    public ResponseEntity<Void> supprimerLigne(
            @PathVariable Integer venteId,
            @PathVariable Integer ligneId) {
        ventePOSService.supprimerLigne(venteId, ligneId);
        return ResponseEntity.noContent().build();
    }

    // ==================== GESTION DES REMISES ====================

    @PostMapping("/ventes/{id}/remise")
    public ResponseEntity<VentePOS> appliquerRemise(
            @PathVariable Integer id,
            @RequestBody @Valid RemiseDto dto) {
        VentePOS vente = ventePOSService.appliquerRemise(id, dto);
        return ResponseEntity.ok(vente);
    }

    @DeleteMapping("/ventes/{id}/remise")
    public ResponseEntity<VentePOS> supprimerRemise(@PathVariable Integer id) {
        VentePOS vente = ventePOSService.supprimerRemise(id);
        return ResponseEntity.ok(vente);
    }

    // ==================== GESTION DES PAIEMENTS ====================

    @PostMapping("/ventes/{id}/paiements")
    public ResponseEntity<PaiementVentePOS> enregistrerPaiement(
            @PathVariable Integer id,
            @RequestBody @Valid PaiementVentePOSDto dto) {
        PaiementVentePOS paiement = ventePOSService.enregistrerPaiement(id, dto);
        return new ResponseEntity<>(paiement, HttpStatus.CREATED);
    }

    @PostMapping("/ventes/{id}/finaliser")
    public ResponseEntity<VentePOS> finaliserVente(@PathVariable Integer id) {
        VentePOS vente = ventePOSService.finaliserVente(id);
        return ResponseEntity.ok(vente);
    }

    // ==================== TICKET DE CAISSE ====================

    @GetMapping("/ventes/{id}/ticket")
    public ResponseEntity<TicketVentePOSDto> getTicket(@PathVariable Integer id) {
        TicketVentePOSDto ticket = ventePOSService.genererTicket(id);
        return ResponseEntity.ok(ticket);
    }

    // ==================== STATISTIQUES ====================

    @GetMapping("/stats/jour")
    public ResponseEntity<StatsJourPOSDto> getStatsJour() {
        StatsJourPOSDto stats = ventePOSService.getStatsJour();
        return ResponseEntity.ok(stats);
    }

    // ==================== RECHERCHE CODE-BARRES ====================

    @GetMapping("/recherche/code-barre/{codeBarre}")
    public ResponseEntity<ConditionnementProduit> rechercherParCodeBarre(@PathVariable String codeBarre) {
        ConditionnementProduit cp = ventePOSService.rechercherParCodeBarre(codeBarre);
        return ResponseEntity.ok(cp);
    }
}
