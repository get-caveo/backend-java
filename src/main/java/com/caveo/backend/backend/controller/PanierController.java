package com.caveo.backend.backend.controller;

import com.caveo.backend.backend.dto.LignePanierDto;
import com.caveo.backend.backend.model.Panier;
import com.caveo.backend.backend.security.AppUserDetails;
import com.caveo.backend.backend.security.IsClient;
import com.caveo.backend.backend.service.PanierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/panier")
@IsClient
public class PanierController {

    private final PanierService panierService;

    @GetMapping
    public ResponseEntity<Panier> getPanier(@AuthenticationPrincipal AppUserDetails user) {
        return ResponseEntity.ok(panierService.getPanier(user.getUtilisateur().getId()));
    }

    @PostMapping("/articles")
    public ResponseEntity<Panier> ajouterArticle(
            @AuthenticationPrincipal AppUserDetails user,
            @RequestBody @Valid LignePanierDto dto) {
        Panier panier = panierService.ajouterArticle(user.getUtilisateur().getId(), dto);
        return ResponseEntity.ok(panier);
    }

    @PutMapping("/articles/{ligneId}")
    public ResponseEntity<Panier> modifierQuantite(
            @AuthenticationPrincipal AppUserDetails user,
            @PathVariable Integer ligneId,
            @RequestParam Integer quantite) {
        Panier panier = panierService.modifierQuantite(user.getUtilisateur().getId(), ligneId, quantite);
        return ResponseEntity.ok(panier);
    }

    @DeleteMapping("/articles/{ligneId}")
    public ResponseEntity<Panier> supprimerArticle(
            @AuthenticationPrincipal AppUserDetails user,
            @PathVariable Integer ligneId) {
        Panier panier = panierService.supprimerArticle(user.getUtilisateur().getId(), ligneId);
        return ResponseEntity.ok(panier);
    }

    @DeleteMapping
    public ResponseEntity<Void> viderPanier(@AuthenticationPrincipal AppUserDetails user) {
        panierService.viderPanier(user.getUtilisateur().getId());
        return ResponseEntity.noContent().build();
    }
}
