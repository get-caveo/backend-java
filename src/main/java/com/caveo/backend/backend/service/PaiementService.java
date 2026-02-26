package com.caveo.backend.backend.service;

import com.caveo.backend.backend.dao.PaiementDao;
import com.caveo.backend.backend.dto.PaiementDto;
import com.caveo.backend.backend.exception.GestionException;
import com.caveo.backend.backend.exception.StockInsuffisantException;
import com.caveo.backend.backend.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaiementService {

    private final PaiementDao paiementDao;
    private final CommandeClientService commandeClientService;
    private final StockService stockService;
    private final CommandeFournisseurService commandeFournisseurService;

    /**
     * Effectue un paiement fictif pour une commande client.
     * Le paiement passe directement en statut COMPLET (simulation).
     */
    @Transactional
    public Paiement payerCommande(Integer commandeClientId, PaiementDto dto) {
        CommandeClient commande = commandeClientService.getCommande(commandeClientId);

        if (commande.getStatutCommande() != StatutCommandeClient.EN_ATTENTE) {
            throw GestionException.badRequest(
                    "Impossible de payer une commande en statut " + commande.getStatutCommande());
        }

        // Vérifier qu'il n'y a pas déjà un paiement
        if (paiementDao.findByCommandeClientId(commandeClientId).isPresent()) {
            throw GestionException.conflict("Un paiement existe déjà pour cette commande");
        }

        // Vérifier la disponibilité du stock avant de procéder au paiement
        List<StockInsuffisantException.ProduitInsuffisant> insuffisants =
                stockService.verifierDisponibilite(commande.getLignes());

        boolean stockInsuffisant = !insuffisants.isEmpty();
        boolean forcePrecommande = Boolean.TRUE.equals(dto.getForcePrecommande());

        if (stockInsuffisant && !forcePrecommande) {
            // Premier essai : informer le client et créer les commandes fournisseur
            List<CommandeFournisseur> commandesFournisseur =
                    commandeFournisseurService.creerCommandesPourDeficit(insuffisants);

            List<String> numeros = commandesFournisseur.stream()
                    .map(CommandeFournisseur::getNumero)
                    .toList();

            log.warn("Stock insuffisant pour commande {}. {} produit(s) en déficit. " +
                            "Commandes fournisseur créées: {}",
                    commande.getNumero(), insuffisants.size(), numeros);

            throw new StockInsuffisantException(
                    "Certains produits ne sont pas disponibles en quantité suffisante. " +
                            "Vous pouvez payer en pré-commande : votre commande sera expédiée dès réception du stock.",
                    insuffisants,
                    numeros
            );
        }

        // Si stock insuffisant mais pré-commande forcée, les commandes fournisseur
        // ont déjà été créées lors du premier appel (sans forcePrecommande)
        if (stockInsuffisant) {
            log.info("Paiement pré-commande forcé pour commande {} ({} produit(s) en déficit)",
                    commande.getNumero(), insuffisants.size());
        }

        // Créer le paiement
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

        if (stockInsuffisant) {
            // Pré-commande : paiement OK mais pas de réservation de stock
            commandeClientService.passerEnPreCommande(commandeClientId);
            log.info("Pré-commande {} pour commande {} ({} €)",
                    saved.getReferenceTransaction(), commande.getNumero(), saved.getMontant());
        } else {
            // Stock OK : confirmer normalement avec réservation de stock
            commandeClientService.confirmerCommande(commandeClientId);
            log.info("Paiement {} pour commande {} ({} €)",
                    saved.getReferenceTransaction(), commande.getNumero(), saved.getMontant());
        }

        return saved;
    }

    public Paiement getPaiementParCommande(Integer commandeClientId) {
        return paiementDao.findByCommandeClientId(commandeClientId)
                .orElseThrow(() -> GestionException.notFound("Paiement", "commande " + commandeClientId));
    }
}
