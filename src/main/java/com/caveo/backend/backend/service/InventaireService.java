package com.caveo.backend.backend.service;

import com.caveo.backend.backend.dao.*;
import com.caveo.backend.backend.exception.GestionException;
import com.caveo.backend.backend.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventaireService {

    private final InventaireDao inventaireDao;
    private final LigneInventaireDao ligneInventaireDao;
    private final StockActuelDao stockActuelDao;
    private final StockService stockService;

    /**
     * Crée un nouvel inventaire en statut BROUILLON.
     */
    @Transactional
    public Inventaire creerInventaire(Inventaire inventaire, Utilisateur utilisateur) {
        inventaire.setStatut(StatutInventaire.BROUILLON);
        inventaire.setUtilisateur(utilisateur);
        return inventaireDao.save(inventaire);
    }

    /**
     * Récupère un inventaire avec ses lignes.
     */
    public Inventaire getInventaireAvecLignes(Integer id) {
        return inventaireDao.findByIdWithLignes(id)
                .orElseThrow(() -> GestionException.notFound("Inventaire", id));
    }

    /**
     * Liste tous les inventaires.
     */
    public List<Inventaire> getAllInventaires() {
        return inventaireDao.findAllOrderByDateDesc();
    }

    /**
     * Liste les inventaires par statut.
     */
    public List<Inventaire> getInventairesByStatut(StatutInventaire statut) {
        return inventaireDao.findByStatutOrderByCreeLe(statut);
    }

    /**
     * Met à jour les métadonnées d'un inventaire (uniquement si BROUILLON).
     */
    @Transactional
    public Inventaire mettreAJour(Integer id, Inventaire inventaireEnvoye) {
        Inventaire inventaire = inventaireDao.findById(id)
                .orElseThrow(() -> GestionException.notFound("Inventaire", id));

        if (inventaire.getStatut() != StatutInventaire.BROUILLON) {
            throw GestionException.badRequest(
                    "Impossible de modifier un inventaire en statut " + inventaire.getStatut());
        }

        inventaire.setNom(inventaireEnvoye.getNom());
        inventaire.setNotes(inventaireEnvoye.getNotes());

        return inventaireDao.save(inventaire);
    }

    /**
     * Supprime un inventaire (uniquement si BROUILLON).
     */
    @Transactional
    public void supprimerInventaire(Integer id) {
        Inventaire inventaire = inventaireDao.findById(id)
                .orElseThrow(() -> GestionException.notFound("Inventaire", id));

        if (inventaire.getStatut() != StatutInventaire.BROUILLON) {
            throw GestionException.badRequest(
                    "Impossible de supprimer un inventaire en statut " + inventaire.getStatut());
        }

        inventaireDao.delete(inventaire);
    }

    // ==================== WORKFLOW ====================

    /**
     * Démarre l'inventaire : BROUILLON → EN_COURS.
     * Génère automatiquement les lignes à partir du stock actuel.
     */
    @Transactional
    public Inventaire demarrerInventaire(Integer id) {
        Inventaire inventaire = inventaireDao.findById(id)
                .orElseThrow(() -> GestionException.notFound("Inventaire", id));

        if (inventaire.getStatut() != StatutInventaire.BROUILLON) {
            throw GestionException.badRequest(
                    "Impossible de démarrer un inventaire en statut " + inventaire.getStatut());
        }

        // Vérifier qu'il n'y a pas déjà un inventaire en cours
        if (inventaireDao.existsInventaireEnCours()) {
            throw GestionException.conflict("Un inventaire est déjà en cours");
        }

        // Générer les lignes à partir du stock actuel
        List<StockActuel> stockList = stockActuelDao.findAllWithRelations();
        
        for (StockActuel stock : stockList) {
            LigneInventaire ligne = new LigneInventaire();
            ligne.setInventaire(inventaire);
            ligne.setProduit(stock.getProduit());
            ligne.setUniteConditionnement(stock.getUniteConditionnement());
            ligne.setQuantiteAttendue(stock.getQuantite());
            ligne.setStatut(StatutLigneInventaire.EN_ATTENTE);
            inventaire.getLignes().add(ligne);
        }

        // Mettre à jour le statut
        inventaire.setStatut(StatutInventaire.EN_COURS);
        inventaire.setDateDebut(LocalDateTime.now());

        log.info("Inventaire {} démarré avec {} lignes", inventaire.getNom(), stockList.size());

        return inventaireDao.save(inventaire);
    }

    /**
     * Termine l'inventaire : EN_COURS → TERMINE.
     * Applique les différences au stock via des mouvements d'inventaire.
     */
    @Transactional
    public Inventaire terminerInventaire(Integer id, Utilisateur utilisateur) {
        Inventaire inventaire = inventaireDao.findByIdWithLignes(id)
                .orElseThrow(() -> GestionException.notFound("Inventaire", id));

        if (inventaire.getStatut() != StatutInventaire.EN_COURS) {
            throw GestionException.badRequest(
                    "Impossible de terminer un inventaire en statut " + inventaire.getStatut());
        }

        // Vérifier que toutes les lignes sont comptées ou validées
        long lignesEnAttente = ligneInventaireDao.countByInventaireIdAndStatut(
                id, StatutLigneInventaire.EN_ATTENTE);
        
        if (lignesEnAttente > 0) {
            throw GestionException.badRequest(
                    "Impossible de terminer : " + lignesEnAttente + " ligne(s) non comptée(s)");
        }

        // Appliquer les différences au stock
        for (LigneInventaire ligne : inventaire.getLignes()) {
            if (ligne.getQuantiteComptee() != null && 
                !ligne.getQuantiteComptee().equals(ligne.getQuantiteAttendue())) {
                
                // Créer un mouvement d'inventaire
                MouvementStock mouvement = new MouvementStock();
                mouvement.setProduit(ligne.getProduit());
                mouvement.setUniteConditionnement(ligne.getUniteConditionnement());
                mouvement.setTypeMouvement(TypeMouvement.INVENTAIRE);
                mouvement.setQuantite(ligne.getQuantiteComptee());
                mouvement.setTypeReference(TypeReference.INVENTAIRE);
                mouvement.setReferenceId(inventaire.getId());
                mouvement.setRaison("Inventaire: " + inventaire.getNom() + 
                        " - Écart: " + ligne.getDifference());
                mouvement.setUtilisateur(utilisateur);

                stockService.enregistrerMouvement(mouvement);

                log.info("Écart corrigé pour produit {} : {} → {} (diff: {})",
                        ligne.getProduit().getNom(),
                        ligne.getQuantiteAttendue(),
                        ligne.getQuantiteComptee(),
                        ligne.getDifference());
            }

            // Valider la ligne
            ligne.setStatut(StatutLigneInventaire.VALIDEE);
        }

        // Mettre à jour le statut
        inventaire.setStatut(StatutInventaire.TERMINE);
        inventaire.setDateFin(LocalDateTime.now());

        log.info("Inventaire {} terminé", inventaire.getNom());

        return inventaireDao.save(inventaire);
    }

    /**
     * Annule l'inventaire : * → ANNULE.
     */
    @Transactional
    public Inventaire annulerInventaire(Integer id) {
        Inventaire inventaire = inventaireDao.findById(id)
                .orElseThrow(() -> GestionException.notFound("Inventaire", id));

        if (inventaire.getStatut() == StatutInventaire.TERMINE) {
            throw GestionException.badRequest("Impossible d'annuler un inventaire terminé");
        }

        inventaire.setStatut(StatutInventaire.ANNULE);
        inventaire.setDateFin(LocalDateTime.now());

        log.info("Inventaire {} annulé", inventaire.getNom());

        return inventaireDao.save(inventaire);
    }

    // ==================== LIGNES ====================

    /**
     * Récupère les lignes d'un inventaire.
     */
    public List<LigneInventaire> getLignes(Integer inventaireId) {
        if (!inventaireDao.existsById(inventaireId)) {
            throw GestionException.notFound("Inventaire", inventaireId);
        }
        return ligneInventaireDao.findByInventaireIdWithRelations(inventaireId);
    }

    /**
     * Met à jour une ligne d'inventaire (enregistre le comptage).
     */
    @Transactional
    public LigneInventaire mettreAJourLigne(Integer inventaireId, Integer ligneId, 
                                             Integer quantiteComptee, String notes,
                                             Utilisateur utilisateur) {
        Inventaire inventaire = inventaireDao.findById(inventaireId)
                .orElseThrow(() -> GestionException.notFound("Inventaire", inventaireId));

        if (inventaire.getStatut() != StatutInventaire.EN_COURS) {
            throw GestionException.badRequest(
                    "Impossible de modifier les lignes d'un inventaire en statut " + inventaire.getStatut());
        }

        LigneInventaire ligne = ligneInventaireDao.findById(ligneId)
                .orElseThrow(() -> GestionException.notFound("Ligne inventaire", ligneId));

        if (!ligne.getInventaire().getId().equals(inventaireId)) {
            throw GestionException.badRequest("Cette ligne n'appartient pas à cet inventaire");
        }

        ligne.setQuantiteComptee(quantiteComptee);
        ligne.setDifference(quantiteComptee - ligne.getQuantiteAttendue());
        ligne.setStatut(StatutLigneInventaire.COMPTEE);
        ligne.setCompteLe(LocalDateTime.now());
        ligne.setComptePar(utilisateur);
        ligne.setNotes(notes);

        return ligneInventaireDao.save(ligne);
    }

    /**
     * Récupère les lignes avec écart (différence != 0).
     */
    public List<LigneInventaire> getLignesAvecEcart(Integer inventaireId) {
        if (!inventaireDao.existsById(inventaireId)) {
            throw GestionException.notFound("Inventaire", inventaireId);
        }
        return ligneInventaireDao.findLignesAvecEcart(inventaireId);
    }
}
