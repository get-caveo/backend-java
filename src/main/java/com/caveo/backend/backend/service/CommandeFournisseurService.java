package com.caveo.backend.backend.service;

import com.caveo.backend.backend.dao.*;
import com.caveo.backend.backend.dto.LigneCommandeCreateDto;
import com.caveo.backend.backend.dto.ReceptionLigneDto;
import com.caveo.backend.backend.exception.GestionException;
import com.caveo.backend.backend.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommandeFournisseurService {

    private final CommandeFournisseurDao commandeFournisseurDao;
    private final LigneCommandeFournisseurDao ligneCommandeFournisseurDao;
    private final FournisseurDao fournisseurDao;
    private final FournisseurProduitDao fournisseurProduitDao;
    private final ProduitDao produitDao;
    private final UniteConditionnementDao uniteConditionnementDao;
    private final StockActuelDao stockActuelDao;
    private final StockService stockService;

    // ==================== CRUD ====================

    /**
     * Crée une nouvelle commande fournisseur en statut BROUILLON.
     */
    @Transactional
    public CommandeFournisseur creerCommande(Integer fournisseurId, String notes, Utilisateur utilisateur) {
        Fournisseur fournisseur = fournisseurDao.findById(fournisseurId)
                .orElseThrow(() -> GestionException.notFound("Fournisseur", fournisseurId));

        CommandeFournisseur commande = new CommandeFournisseur();
        commande.setNumero(genererNumeroCommande());
        commande.setFournisseur(fournisseur);
        commande.setStatut(StatutCommandeFournisseur.BROUILLON);
        commande.setNotes(notes);
        commande.setCreePar(utilisateur);
        commande.setMontantTotal(BigDecimal.ZERO);

        return commandeFournisseurDao.save(commande);
    }

    /**
     * Récupère une commande avec ses lignes.
     */
    public CommandeFournisseur getCommandeAvecLignes(Integer id) {
        return commandeFournisseurDao.findByIdWithLignes(id)
                .orElseThrow(() -> GestionException.notFound("Commande fournisseur", id));
    }

    /**
     * Liste toutes les commandes.
     */
    public List<CommandeFournisseur> getAllCommandes() {
        return commandeFournisseurDao.findAllOrderByDateDesc();
    }

    /**
     * Liste les commandes par statut.
     */
    public List<CommandeFournisseur> getCommandesByStatut(StatutCommandeFournisseur statut) {
        return commandeFournisseurDao.findByStatutOrderByCreeLe(statut);
    }

    /**
     * Liste les commandes en attente de réception.
     */
    public List<CommandeFournisseur> getCommandesEnAttente() {
        return commandeFournisseurDao.findCommandesEnAttente();
    }

    /**
     * Met à jour une commande (uniquement si BROUILLON).
     */
    @Transactional
    public CommandeFournisseur mettreAJour(Integer id, LocalDateTime dateLivraisonPrevue, String notes) {
        CommandeFournisseur commande = commandeFournisseurDao.findById(id)
                .orElseThrow(() -> GestionException.notFound("Commande fournisseur", id));

        if (commande.getStatut() != StatutCommandeFournisseur.BROUILLON) {
            throw GestionException.badRequest(
                    "Impossible de modifier une commande en statut " + commande.getStatut());
        }

        commande.setDateLivraisonPrevue(dateLivraisonPrevue);
        commande.setNotes(notes);

        return commandeFournisseurDao.save(commande);
    }

    /**
     * Supprime une commande (uniquement si BROUILLON).
     */
    @Transactional
    public void supprimerCommande(Integer id) {
        CommandeFournisseur commande = commandeFournisseurDao.findById(id)
                .orElseThrow(() -> GestionException.notFound("Commande fournisseur", id));

        if (commande.getStatut() != StatutCommandeFournisseur.BROUILLON) {
            throw GestionException.badRequest(
                    "Impossible de supprimer une commande en statut " + commande.getStatut());
        }

        commandeFournisseurDao.delete(commande);
    }

    // ==================== LIGNES ====================

    /**
     * Ajoute une ligne à la commande.
     */
    @Transactional
    public LigneCommandeFournisseur ajouterLigne(Integer commandeId, LigneCommandeCreateDto dto) {
        CommandeFournisseur commande = commandeFournisseurDao.findByIdWithLignes(commandeId)
                .orElseThrow(() -> GestionException.notFound("Commande fournisseur", commandeId));

        if (commande.getStatut() != StatutCommandeFournisseur.BROUILLON) {
            throw GestionException.badRequest(
                    "Impossible d'ajouter des lignes à une commande en statut " + commande.getStatut());
        }

        Produit produit = produitDao.findById(dto.getProduitId())
                .orElseThrow(() -> GestionException.notFound("Produit", dto.getProduitId()));

        UniteConditionnement unite = uniteConditionnementDao.findById(dto.getUniteConditionnementId())
                .orElseThrow(() -> GestionException.notFound("Unité de conditionnement", dto.getUniteConditionnementId()));

        LigneCommandeFournisseur ligne = new LigneCommandeFournisseur();
        ligne.setCommandeFournisseur(commande);
        ligne.setProduit(produit);
        ligne.setUniteConditionnement(unite);
        ligne.setQuantite(dto.getQuantite());
        ligne.setPrixUnitaire(dto.getPrixUnitaire());
        ligne.setPrixTotal(dto.getPrixUnitaire().multiply(BigDecimal.valueOf(dto.getQuantite())));
        ligne.setQuantiteRecue(0);

        commande.getLignes().add(ligne);
        recalculerMontantTotal(commande);

        commandeFournisseurDao.save(commande);
        return ligne;
    }

    /**
     * Supprime une ligne de la commande.
     */
    @Transactional
    public void supprimerLigne(Integer commandeId, Integer ligneId) {
        CommandeFournisseur commande = commandeFournisseurDao.findById(commandeId)
                .orElseThrow(() -> GestionException.notFound("Commande fournisseur", commandeId));

        if (commande.getStatut() != StatutCommandeFournisseur.BROUILLON) {
            throw GestionException.badRequest(
                    "Impossible de supprimer des lignes d'une commande en statut " + commande.getStatut());
        }

        LigneCommandeFournisseur ligne = ligneCommandeFournisseurDao.findById(ligneId)
                .orElseThrow(() -> GestionException.notFound("Ligne commande", ligneId));

        if (!ligne.getCommandeFournisseur().getId().equals(commandeId)) {
            throw GestionException.badRequest("Cette ligne n'appartient pas à cette commande");
        }

        commande.getLignes().remove(ligne);
        ligneCommandeFournisseurDao.delete(ligne);
        recalculerMontantTotal(commande);
        commandeFournisseurDao.save(commande);
    }

    // ==================== flux de travail ====================

    /**
     * Envoie la commande : BROUILLON → ENVOYEE.
     */
    @Transactional
    public CommandeFournisseur envoyerCommande(Integer id) {
        CommandeFournisseur commande = commandeFournisseurDao.findByIdWithLignes(id)
                .orElseThrow(() -> GestionException.notFound("Commande fournisseur", id));

        if (commande.getStatut() != StatutCommandeFournisseur.BROUILLON) {
            throw GestionException.badRequest(
                    "Impossible d'envoyer une commande en statut " + commande.getStatut());
        }

        if (commande.getLignes().isEmpty()) {
            throw GestionException.badRequest("Impossible d'envoyer une commande sans lignes");
        }

        commande.setStatut(StatutCommandeFournisseur.ENVOYEE);
        commande.setDateCommande(LocalDateTime.now());

        log.info("Commande {} envoyée au fournisseur {}", commande.getNumero(), 
                commande.getFournisseur().getNom());

        return commandeFournisseurDao.save(commande);
    }

    /**
     * Confirme la commande : ENVOYEE → CONFIRMEE.
     */
    @Transactional
    public CommandeFournisseur confirmerCommande(Integer id, LocalDateTime dateLivraisonPrevue) {
        CommandeFournisseur commande = commandeFournisseurDao.findById(id)
                .orElseThrow(() -> GestionException.notFound("Commande fournisseur", id));

        if (commande.getStatut() != StatutCommandeFournisseur.ENVOYEE) {
            throw GestionException.badRequest(
                    "Impossible de confirmer une commande en statut " + commande.getStatut());
        }

        commande.setStatut(StatutCommandeFournisseur.CONFIRMEE);
        if (dateLivraisonPrevue != null) {
            commande.setDateLivraisonPrevue(dateLivraisonPrevue);
        }

        log.info("Commande {} confirmée", commande.getNumero());

        return commandeFournisseurDao.save(commande);
    }

    /**
     * Réceptionne des lignes de la commande.
     * Met à jour le stock via des mouvements d'entrée.
     */
    @Transactional
    public CommandeFournisseur receptionner(Integer id, List<ReceptionLigneDto> receptions, Utilisateur utilisateur) {
        CommandeFournisseur commande = commandeFournisseurDao.findByIdWithLignes(id)
                .orElseThrow(() -> GestionException.notFound("Commande fournisseur", id));

        if (commande.getStatut() != StatutCommandeFournisseur.ENVOYEE &&
            commande.getStatut() != StatutCommandeFournisseur.CONFIRMEE &&
            commande.getStatut() != StatutCommandeFournisseur.PARTIELLEMENT_RECUE) {
            throw GestionException.badRequest(
                    "Impossible de réceptionner une commande en statut " + commande.getStatut());
        }

        for (ReceptionLigneDto reception : receptions) {
            LigneCommandeFournisseur ligne = commande.getLignes().stream()
                    .filter(l -> l.getId().equals(reception.getLigneId()))
                    .findFirst()
                    .orElseThrow(() -> GestionException.notFound("Ligne commande", reception.getLigneId()));

            int nouvelleQteRecue = ligne.getQuantiteRecue() + reception.getQuantiteRecue();
            if (nouvelleQteRecue > ligne.getQuantite()) {
                throw GestionException.badRequest(
                        "Quantité reçue (" + nouvelleQteRecue + ") supérieure à la quantité commandée (" +
                        ligne.getQuantite() + ") pour le produit " + ligne.getProduit().getNom());
            }

            // Mettre à jour la ligne
            ligne.setQuantiteRecue(nouvelleQteRecue);
            if (reception.getNumeroLot() != null) {
                ligne.setNumeroLot(reception.getNumeroLot());
            }
            if (reception.getDatePeremption() != null) {
                ligne.setDatePeremption(reception.getDatePeremption());
            }

            // Créer le mouvement de stock (entrée)
            MouvementStock mouvement = new MouvementStock();
            mouvement.setProduit(ligne.getProduit());
            mouvement.setUniteConditionnement(ligne.getUniteConditionnement());
            mouvement.setTypeMouvement(TypeMouvement.ENTREE);
            mouvement.setQuantite(reception.getQuantiteRecue());
            mouvement.setTypeReference(TypeReference.COMMANDE_FOURNISSEUR);
            mouvement.setReferenceId(commande.getId());
            mouvement.setPrixUnitaire(ligne.getPrixUnitaire());
            mouvement.setRaison("Réception commande " + commande.getNumero());
            mouvement.setNumeroLot(reception.getNumeroLot());
            mouvement.setDatePeremption(reception.getDatePeremption());
            mouvement.setUtilisateur(utilisateur);

            stockService.enregistrerMouvement(mouvement);

            log.info("Réception: {} x {} pour commande {}", 
                    reception.getQuantiteRecue(), ligne.getProduit().getNom(), commande.getNumero());
        }

        // Déterminer le nouveau statut
        boolean touteRecue = commande.getLignes().stream()
                .allMatch(l -> l.getQuantiteRecue().equals(l.getQuantite()));

        if (touteRecue) {
            commande.setStatut(StatutCommandeFournisseur.RECUE);
            log.info("Commande {} entièrement reçue", commande.getNumero());
        } else {
            commande.setStatut(StatutCommandeFournisseur.PARTIELLEMENT_RECUE);
        }

        return commandeFournisseurDao.save(commande);
    }

    /**
     * Annule la commande.
     */
    @Transactional
    public CommandeFournisseur annulerCommande(Integer id) {
        CommandeFournisseur commande = commandeFournisseurDao.findById(id)
                .orElseThrow(() -> GestionException.notFound("Commande fournisseur", id));

        if (commande.getStatut() == StatutCommandeFournisseur.RECUE) {
            throw GestionException.badRequest("Impossible d'annuler une commande déjà reçue");
        }

        if (commande.getStatut() == StatutCommandeFournisseur.PARTIELLEMENT_RECUE) {
            throw GestionException.badRequest(
                    "Impossible d'annuler une commande partiellement reçue. " +
                    "Utilisez un avoir ou un retour.");
        }

        commande.setStatut(StatutCommandeFournisseur.ANNULEE);
        log.info("Commande {} annulée", commande.getNumero());

        return commandeFournisseurDao.save(commande);
    }

    // ==================== COMMANDE AUTOMATIQUE ====================

    /**
     * Crée automatiquement une commande fournisseur pour les produits sous le seuil.
     * Appelée depuis StockService quand un produit passe sous le seuil avec reapproAuto=true.
     */
    @Transactional
    public CommandeFournisseur creerCommandeAutomatique(Produit produit, Utilisateur utilisateurSysteme) {
        // Trouver le fournisseur préféré (premier avec le meilleur prix ou délai)
        List<FournisseurProduit> fournisseursProduit = fournisseurProduitDao.findByProduitIdOrderByPrixAsc(produit.getId());
        
        if (fournisseursProduit.isEmpty()) {
            log.warn("Aucun fournisseur trouvé pour le produit {} - commande auto impossible", produit.getNom());
            return null;
        }

        FournisseurProduit fp = fournisseursProduit.get(0);
        Fournisseur fournisseur = fp.getFournisseur();

        // Calculer la quantité à commander (ex: 2x le seuil minimum)
        int quantiteACommander = produit.getSeuilStockMinimal() * 2;

        // Trouver l'unité de base ou la première unité disponible
        UniteConditionnement unite = uniteConditionnementDao.findByEstUniteBaseTrueAndActifTrue();
        if (unite == null) {
            List<UniteConditionnement> unites = uniteConditionnementDao.findByActifTrueOrderByOrdreTri();
            if (unites.isEmpty()) {
                log.error("Aucune unité de conditionnement disponible");
                return null;
            }
            unite = unites.get(0);
        }

        // Créer la commande
        CommandeFournisseur commande = new CommandeFournisseur();
        commande.setNumero(genererNumeroCommande());
        commande.setFournisseur(fournisseur);
        commande.setStatut(StatutCommandeFournisseur.BROUILLON);
        commande.setNotes("Commande automatique - Stock bas pour: " + produit.getNom());
        commande.setCreePar(utilisateurSysteme);

        // Ajouter la ligne
        LigneCommandeFournisseur ligne = new LigneCommandeFournisseur();
        ligne.setCommandeFournisseur(commande);
        ligne.setProduit(produit);
        ligne.setUniteConditionnement(unite);
        ligne.setQuantite(quantiteACommander);
        ligne.setPrixUnitaire(fp.getPrixFournisseur() != null ? fp.getPrixFournisseur() : BigDecimal.ZERO);
        ligne.setPrixTotal(ligne.getPrixUnitaire().multiply(BigDecimal.valueOf(quantiteACommander)));
        ligne.setQuantiteRecue(0);

        commande.getLignes().add(ligne);
        commande.setMontantTotal(ligne.getPrixTotal());

        CommandeFournisseur saved = commandeFournisseurDao.save(commande);

        log.info("Commande automatique {} créée pour {} ({} unités) chez {}", 
                saved.getNumero(), produit.getNom(), quantiteACommander, fournisseur.getNom());

        return saved;
    }

    /**
     * Vérifie tous les produits sous seuil et crée des commandes automatiques GROUPÉES par fournisseur.
     * Une seule commande par fournisseur avec toutes les lignes des produits manquants.
     */
    @Transactional
    public List<CommandeFournisseur> creerCommandesAutomatiquesGroupees(Utilisateur utilisateurSysteme) {
        List<StockActuel> stockSousSeuil = stockActuelDao.findStockSousSeuilAvecReapproAuto();
        
        if (stockSousSeuil.isEmpty()) {
            log.info("Aucun produit sous le seuil avec réappro auto activé");
            return new ArrayList<>();
        }

        // Grouper les produits par fournisseur préféré
        // Map<FournisseurId, List<{Produit, FournisseurProduit}>>
        Map<Integer, List<ProduitACommander>> produitsParFournisseur = new HashMap<>();

        for (StockActuel stock : stockSousSeuil) {
            Produit produit = stock.getProduit();
            
            // Trouver le fournisseur préféré (meilleur prix)
            List<FournisseurProduit> fournisseursProduit = fournisseurProduitDao
                    .findByProduitIdOrderByPrixAsc(produit.getId());
            
            if (fournisseursProduit.isEmpty()) {
                log.warn("Aucun fournisseur trouvé pour le produit {} - ignoré", produit.getNom());
                continue;
            }

            FournisseurProduit fp = fournisseursProduit.get(0);
            Integer fournisseurId = fp.getFournisseur().getId();

            // Ajouter au groupe du fournisseur
            produitsParFournisseur
                    .computeIfAbsent(fournisseurId, k -> new ArrayList<>())
                    .add(new ProduitACommander(produit, fp, stock));
        }

        // Créer une commande par fournisseur
        List<CommandeFournisseur> commandesCrees = new ArrayList<>();

        for (Map.Entry<Integer, List<ProduitACommander>> entry : produitsParFournisseur.entrySet()) {
            List<ProduitACommander> produits = entry.getValue();
            Fournisseur fournisseur = produits.get(0).fournisseurProduit.getFournisseur();

            // Créer la commande
            CommandeFournisseur commande = new CommandeFournisseur();
            commande.setNumero(genererNumeroCommande());
            commande.setFournisseur(fournisseur);
            commande.setStatut(StatutCommandeFournisseur.BROUILLON);
            commande.setCreePar(utilisateurSysteme);

            // Construire la liste des noms pour les notes
            List<String> nomsProduits = new ArrayList<>();

            // Ajouter les lignes
            BigDecimal montantTotal = BigDecimal.ZERO;

            for (ProduitACommander pac : produits) {
                Produit produit = pac.produit;
                FournisseurProduit fp = pac.fournisseurProduit;
                // Utiliser l'unité de conditionnement du stock qui est sous le seuil
                UniteConditionnement unite = pac.stock.getUniteConditionnement();

                // Calculer quantité à commander (2x le seuil minimum)
                int quantiteACommander = produit.getSeuilStockMinimal() * 2;

                LigneCommandeFournisseur ligne = new LigneCommandeFournisseur();
                ligne.setCommandeFournisseur(commande);
                ligne.setProduit(produit);
                ligne.setUniteConditionnement(unite);
                ligne.setQuantite(quantiteACommander);
                ligne.setPrixUnitaire(fp.getPrixFournisseur() != null ? fp.getPrixFournisseur() : BigDecimal.ZERO);
                ligne.setPrixTotal(ligne.getPrixUnitaire().multiply(BigDecimal.valueOf(quantiteACommander)));
                ligne.setQuantiteRecue(0);

                commande.getLignes().add(ligne);
                montantTotal = montantTotal.add(ligne.getPrixTotal());
                nomsProduits.add(produit.getNom());
            }

            commande.setMontantTotal(montantTotal);
            commande.setNotes("Commande automatique - Stock bas pour: " + String.join(", ", nomsProduits));

            CommandeFournisseur saved = commandeFournisseurDao.save(commande);
            commandesCrees.add(saved);

            log.info("Commande automatique {} créée pour {} produits chez {} (total: {} €)", 
                    saved.getNumero(), produits.size(), fournisseur.getNom(), montantTotal);
        }

        return commandesCrees;
    }

    /**
     * Classe interne pour regrouper les infos d'un produit à commander.
     */
    private record ProduitACommander(Produit produit, FournisseurProduit fournisseurProduit, StockActuel stock) {}

    // ==================== UTILITAIRES ====================

    private String genererNumeroCommande() {
        String prefix = "CF-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM")) + "-";
        Integer maxId = commandeFournisseurDao.getMaxId();
        int nextNum = (maxId == null ? 1 : maxId + 1);
        return prefix + String.format("%04d", nextNum);
    }

    private void recalculerMontantTotal(CommandeFournisseur commande) {
        BigDecimal total = commande.getLignes().stream()
                .map(LigneCommandeFournisseur::getPrixTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        commande.setMontantTotal(total);
    }
}
