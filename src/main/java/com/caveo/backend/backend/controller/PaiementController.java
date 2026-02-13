package com.caveo.backend.backend.controller;

import com.caveo.backend.backend.dto.PaiementDto;
import com.caveo.backend.backend.model.Paiement;
import com.caveo.backend.backend.security.IsClient;
import com.caveo.backend.backend.service.PaiementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/paiements")
@IsClient
public class PaiementController {

    private final PaiementService paiementService;

    /**
     * Effectue un paiement fictif pour une commande.
     */
    @PostMapping("/commande/{commandeId}")
    public ResponseEntity<Paiement> payer(
            @PathVariable Integer commandeId,
            @RequestBody @Valid PaiementDto dto) {
        Paiement paiement = paiementService.payerCommande(commandeId, dto);
        return new ResponseEntity<>(paiement, HttpStatus.CREATED);
    }

    /**
     * Récupère le paiement d'une commande.
     */
    @GetMapping("/commande/{commandeId}")
    public ResponseEntity<Paiement> getParCommande(@PathVariable Integer commandeId) {
        return ResponseEntity.ok(paiementService.getPaiementParCommande(commandeId));
    }
}
