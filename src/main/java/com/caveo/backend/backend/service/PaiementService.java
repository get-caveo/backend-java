package com.caveo.backend.backend.service;

import com.caveo.backend.backend.dao.PaiementDao;
import com.caveo.backend.backend.dto.PaiementDto;
import com.caveo.backend.backend.exception.GestionException;
import com.caveo.backend.backend.model.*;
import com.caveo.backend.backend.security.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaiementService {

    private final PaiementDao paiementDao;
    private final CommandeClientService commandeClientService;

    /**
     * Effectue un paiement fictif pour une commande client.
     * Le paiement passe directement en statut COMPLET (simulation).
     */
    @Transactional
    public Paiement payerCommande(Integer commandeClientId, PaiementDto dto, Integer clientId, Role role) {
        // Vérifier que la commande appartient au client connecté
        CommandeClient commande = commandeClientService.getCommande(commandeClientId, clientId, role);

        if (commande.getStatutCommande() != StatutCommandeClient.EN_ATTENTE) {
            throw GestionException.badRequest(
                    "Impossible de payer une commande en statut " + commande.getStatutCommande());
        }

        // Vérifier qu'il n'y a pas déjà un paiement
        if (paiementDao.findByCommandeClientId(commandeClientId).isPresent()) {
            throw GestionException.conflict("Un paiement existe déjà pour cette commande");
        }

        Paiement paiement = new Paiement();
        paiement.setCommandeClient(commande);
        paiement.setMontant(commande.getMontantTotal());
        paiement.setMethodePaiement(dto.getMethodePaiement());
        paiement.setReferenceTransaction(
                dto.getReferenceTransaction() != null
                        ? dto.getReferenceTransaction()
                        : "FICTIF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        paiement.setDetailsPaiement(dto.getDetailsPaiement());

        // Paiement fictif → directement COMPLET
        paiement.setStatutPaiement(StatutPaiement.COMPLET);
        paiement.setPayeLe(LocalDateTime.now());

        Paiement saved = paiementDao.save(paiement);

        // Confirmer automatiquement la commande après paiement réussi
        commandeClientService.confirmerCommande(commandeClientId);

        log.info("Paiement fictif {} pour commande {} ({} €, {})",
                saved.getReferenceTransaction(),
                commande.getNumero(),
                saved.getMontant(),
                saved.getMethodePaiement());

        return saved;
    }

    public Paiement getPaiementParCommande(Integer commandeClientId, Integer clientId, Role role) {
        // Vérifier que la commande appartient au client connecté
        commandeClientService.getCommande(commandeClientId, clientId, role);
        return paiementDao.findByCommandeClientId(commandeClientId)
                .orElseThrow(() -> GestionException.notFound("Paiement", "commande " + commandeClientId));
    }
}
