package com.caveo.backend.backend.service;

import com.caveo.backend.backend.dao.*;
import com.caveo.backend.backend.dto.NotificationEvent;
import com.caveo.backend.backend.exception.GestionException;
import com.caveo.backend.backend.model.*;
import com.caveo.backend.backend.security.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommandeClientService {

    private final CommandeClientDao commandeClientDao;
    private final PanierDao panierDao;
    private final ConditionnementProduitDao conditionnementProduitDao;
    private final AdresseDao adresseDao;
    private final StockService stockService;
    private final ApplicationEventPublisher eventPublisher;

    // ==================== MACHINE À ÉTATS ====================

    private void transitionner(CommandeClient commande, StatutCommandeClient cible) {
        if (!commande.getStatutCommande().peutTransitionnerVers(cible)) {
            throw GestionException.badRequest(
                    "Transition impossible : " + commande.getStatutCommande() + " → " + cible);
        }
        commande.setStatutCommande(cible);
    }

    // ==================== CRÉATION ====================

    /**
     * Crée une commande à partir du panier du client.
     */
    @Transactional
    public CommandeClient creerDepuisPanier(Integer clientId, Integer adresseLivraisonId, Integer adresseFacturationId, String notes) {
        Panier panier = panierDao.findByClientIdWithDetails(clientId)
                .orElseThrow(() -> GestionException.badRequest("Aucun panier trouvé"));

        if (panier.getLignes().isEmpty()) {
            throw GestionException.badRequest("Le panier est vide");
        }

        CommandeClient commande = new CommandeClient();
        commande.setNumero(genererNumeroCommande());
        commande.setClient(panier.getClient());
        commande.setStatutCommande(StatutCommandeClient.EN_ATTENTE);
        commande.setDateCommande(LocalDateTime.now());
        commande.setNotes(notes);

        if (adresseLivraisonId != null) {
            Adresse adresseLivraison = adresseDao.findById(adresseLivraisonId)
                    .orElseThrow(() -> GestionException.notFound("Adresse", adresseLivraisonId));
            commande.setAdresseLivraison(adresseLivraison);
        }

        if (adresseFacturationId != null) {
            Adresse adresseFacturation = adresseDao.findById(adresseFacturationId)
                    .orElseThrow(() -> GestionException.notFound("Adresse", adresseFacturationId));
            commande.setAdresseFacturation(adresseFacturation);
        }

        BigDecimal sousTotal = BigDecimal.ZERO;

        for (LignePanier lignePanier : panier.getLignes()) {
            // Récupérer le prix depuis ConditionnementProduit
            ConditionnementProduit cp = conditionnementProduitDao
                    .findByProduitIdAndUniteConditionnementId(
                            lignePanier.getProduit().getId(),
                            lignePanier.getUniteConditionnement().getId())
                    .orElseThrow(() -> GestionException.badRequest(
                            "Conditionnement introuvable pour le produit " + lignePanier.getProduit().getNom()));

            BigDecimal prixUnitaire = cp.getPrixUnitaire();
            BigDecimal prixTotal = prixUnitaire.multiply(BigDecimal.valueOf(lignePanier.getQuantite()));

            LigneCommandeClient ligneCommande = new LigneCommandeClient();
            ligneCommande.setCommandeClient(commande);
            ligneCommande.setProduit(lignePanier.getProduit());
            ligneCommande.setUniteConditionnement(lignePanier.getUniteConditionnement());
            ligneCommande.setQuantite(lignePanier.getQuantite());
            ligneCommande.setPrixUnitaire(prixUnitaire);
            ligneCommande.setPrixTotal(prixTotal);

            commande.getLignes().add(ligneCommande);
            sousTotal = sousTotal.add(prixTotal);
        }

        commande.setSousTotal(sousTotal);
        commande.setMontantTotal(sousTotal.add(commande.getFraisLivraison()).add(commande.getMontantTaxes()));

        CommandeClient saved = commandeClientDao.save(commande);

        // Vider le panier
        panier.getLignes().clear();
        panierDao.save(panier);

        // Publier un événement de notification pour le backoffice
        eventPublisher.publishEvent(new NotificationEvent(
                TypeNotification.COMMANDE_RECUE,
                "Nouvelle commande " + saved.getNumero(),
                "Commande de " + saved.getMontantTotal() + " € reçue",
                "COMMANDE_CLIENT",
                saved.getId(),
                null
        ));

        log.info("Commande {} créée pour le client {}", saved.getNumero(), clientId);
        return saved;
    }

    public CommandeClient getCommande(Integer id) {
        return commandeClientDao.findByIdWithDetails(id)
                .orElseThrow(() -> GestionException.notFound("Commande client", id));
    }

    /**
     * Détail d'une commande avec vérification de propriété pour les clients.
     */
    public CommandeClient getCommande(Integer id, Integer clientId, Role role) {
        CommandeClient commande = getCommande(id);
        verifierProprietaire(commande, clientId, role);
        return commande;
    }

    /**
     * Vérifie que le client connecté est bien le propriétaire de la commande.
     * Les employés et admins peuvent accéder à toutes les commandes.
     */
    private void verifierProprietaire(CommandeClient commande, Integer clientId, Role role) {
        if (role == Role.CLIENT && !commande.getClient().getId().equals(clientId)) {
            throw GestionException.forbidden("Vous ne pouvez accéder qu'à vos propres commandes");
        }
    }

    public List<CommandeClient> getCommandesClient(Integer clientId) {
        return commandeClientDao.findByClientIdOrderByCreeLeDesc(clientId);
    }

    public List<CommandeClient> getAllCommandes() {
        return commandeClientDao.findAllOrderByDateDesc();
    }

    public List<CommandeClient> getCommandesByStatut(StatutCommandeClient statut) {
        return commandeClientDao.findByStatutCommandeOrderByCreeLeDesc(statut);
    }

    /**
     * Confirme la commande : EN_ATTENTE → CONFIRMEE.
     * Réserve le stock pour chaque ligne.
     */
    @Transactional
    public CommandeClient confirmerCommande(Integer id) {
        CommandeClient commande = commandeClientDao.findByIdWithDetails(id)
                .orElseThrow(() -> GestionException.notFound("Commande client", id));

        transitionner(commande, StatutCommandeClient.CONFIRMEE);

        for (LigneCommandeClient ligne : commande.getLignes()) {
            stockService.reserverStock(
                    ligne.getProduit().getId(),
                    ligne.getUniteConditionnement().getId(),
                    ligne.getQuantite());
        }

        return commandeClientDao.save(commande);
    }

    /**
     * CONFIRMEE → EN_PREPARATION.
     */
    @Transactional
    public CommandeClient preparerCommande(Integer id) {
        CommandeClient commande = commandeClientDao.findById(id)
                .orElseThrow(() -> GestionException.notFound("Commande client", id));

        transitionner(commande, StatutCommandeClient.EN_PREPARATION);

        return commandeClientDao.save(commande);
    }

    /**
     * EN_PREPARATION → EXPEDIEE. Mouvements de sortie de stock.
     */
    @Transactional
    public CommandeClient expedierCommande(Integer id, Utilisateur utilisateur) {
        CommandeClient commande = commandeClientDao.findByIdWithDetails(id)
                .orElseThrow(() -> GestionException.notFound("Commande client", id));

        transitionner(commande, StatutCommandeClient.EXPEDIEE);

        for (LigneCommandeClient ligne : commande.getLignes()) {
            stockService.libererStock(
                    ligne.getProduit().getId(),
                    ligne.getUniteConditionnement().getId(),
                    ligne.getQuantite());

            MouvementStock mouvement = new MouvementStock();
            mouvement.setProduit(ligne.getProduit());
            mouvement.setUniteConditionnement(ligne.getUniteConditionnement());
            mouvement.setTypeMouvement(TypeMouvement.SORTIE);
            mouvement.setQuantite(ligne.getQuantite());
            mouvement.setTypeReference(TypeReference.COMMANDE_CLIENT);
            mouvement.setReferenceId(commande.getId());
            mouvement.setPrixUnitaire(ligne.getPrixUnitaire());
            mouvement.setRaison("Expédition commande " + commande.getNumero());
            mouvement.setUtilisateur(utilisateur);

            stockService.enregistrerMouvement(mouvement);
        }

        commande.setDateExpedition(LocalDateTime.now());

        // Notification
        eventPublisher.publishEvent(new NotificationEvent(
                TypeNotification.COMMANDE_EXPEDIEE,
                "Commande " + commande.getNumero() + " expédiée",
                "La commande a été expédiée",
                "COMMANDE_CLIENT",
                commande.getId(),
                commande.getClient().getId()
        ));

        log.info("Commande {} expédiée", commande.getNumero());
        return commandeClientDao.save(commande);
    }

    /**
     * EXPEDIEE → LIVREE.
     */
    @Transactional
    public CommandeClient livrerCommande(Integer id) {
        CommandeClient commande = commandeClientDao.findById(id)
                .orElseThrow(() -> GestionException.notFound("Commande client", id));

        transitionner(commande, StatutCommandeClient.LIVREE);

        commande.setDateLivraison(LocalDateTime.now());
        return commandeClientDao.save(commande);
    }

    /**
     * EN_ATTENTE|CONFIRMEE|EN_PREPARATION → ANNULEE. Libère le stock réservé si nécessaire.
     */
    @Transactional
    public CommandeClient annulerCommande(Integer id, Integer clientId, Role role) {
        CommandeClient commande = commandeClientDao.findByIdWithDetails(id)
                .orElseThrow(() -> GestionException.notFound("Commande client", id));

        verifierProprietaire(commande, clientId, role);

        // Libérer le stock réservé avant de transitionner
        if (commande.getStatutCommande() == StatutCommandeClient.CONFIRMEE ||
            commande.getStatutCommande() == StatutCommandeClient.EN_PREPARATION) {
            for (LigneCommandeClient ligne : commande.getLignes()) {
                stockService.libererStock(
                        ligne.getProduit().getId(),
                        ligne.getUniteConditionnement().getId(),
                        ligne.getQuantite());
            }
        }

        transitionner(commande, StatutCommandeClient.ANNULEE);

        log.info("Commande {} annulée", commande.getNumero());
        return commandeClientDao.save(commande);
    }

    private String genererNumeroCommande() {
        String prefix = "CC-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM")) + "-";
        Integer maxId = commandeClientDao.findAll().stream()
                .map(CommandeClient::getId)
                .max(Integer::compareTo)
                .orElse(0);
        return prefix + String.format("%04d", maxId + 1);
    }
}
