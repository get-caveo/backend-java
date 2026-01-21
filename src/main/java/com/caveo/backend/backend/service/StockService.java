package com.caveo.backend.backend.service;

import com.caveo.backend.backend.dao.*;
import com.caveo.backend.backend.exception.GestionException;
import com.caveo.backend.backend.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockService {

    private final StockActuelDao stockActuelDao;
    private final MouvementStockDao mouvementStockDao;
    private final ProduitDao produitDao;
    private final UniteConditionnementDao uniteConditionnementDao;

    /**
     * Enregistre un mouvement de stock et met à jour le stock actuel.
     * Vérifie également si le stock passe sous le seuil minimal.
     */
    @Transactional
    public MouvementStock enregistrerMouvement(MouvementStock mouvement) {
        // Valider le produit
        Produit produit = produitDao.findById(mouvement.getProduit().getId())
                .orElseThrow(() -> GestionException.notFound("Produit", mouvement.getProduit().getId()));
        mouvement.setProduit(produit);

        // Valider l'unité de conditionnement
        UniteConditionnement unite = uniteConditionnementDao.findById(mouvement.getUniteConditionnement().getId())
                .orElseThrow(() -> GestionException.notFound("Unité de conditionnement", 
                        mouvement.getUniteConditionnement().getId()));
        mouvement.setUniteConditionnement(unite);

        // Calculer la quantité en unité de base
        mouvement.setQuantiteUniteBase(mouvement.getQuantite() * unite.getQuantiteUniteBase());

        // Récupérer ou créer le stock actuel
        StockActuel stockActuel = stockActuelDao
                .findByProduitIdAndUniteConditionnementId(produit.getId(), unite.getId())
                .orElseGet(() -> {
                    StockActuel newStock = new StockActuel();
                    newStock.setProduit(produit);
                    newStock.setUniteConditionnement(unite);
                    newStock.setQuantite(0);
                    newStock.setQuantiteReservee(0);
                    newStock.setQuantiteDisponible(0);
                    newStock.setQuantiteUniteBase(0);
                    return newStock;
                });

        // Appliquer le mouvement selon le type
        switch (mouvement.getTypeMouvement()) {
            case ENTREE:
                stockActuel.setQuantite(stockActuel.getQuantite() + mouvement.getQuantite());
                break;

            case SORTIE:
                int nouvelleQuantite = stockActuel.getQuantite() - mouvement.getQuantite();
                if (nouvelleQuantite < 0) {
                    throw GestionException.badRequest(
                            "Stock insuffisant. Stock actuel: " + stockActuel.getQuantite() + 
                            ", demandé: " + mouvement.getQuantite());
                }
                stockActuel.setQuantite(nouvelleQuantite);
                break;

            case AJUSTEMENT:
                // La quantité peut être positive ou négative pour un ajustement
                stockActuel.setQuantite(stockActuel.getQuantite() + mouvement.getQuantite());
                if (stockActuel.getQuantite() < 0) {
                    stockActuel.setQuantite(0);
                }
                break;

            case INVENTAIRE:
                // Pour un inventaire, la quantité représente la nouvelle valeur absolue
                stockActuel.setQuantite(mouvement.getQuantite());
                stockActuel.setDernierInventaire(java.time.LocalDateTime.now());
                break;
        }

        // Recalculer les quantités dérivées
        recalculerQuantites(stockActuel);

        // Sauvegarder le stock
        stockActuelDao.save(stockActuel);

        // Sauvegarder le mouvement
        MouvementStock saved = mouvementStockDao.save(mouvement);

        // Vérifier le seuil minimal et logger si nécessaire
        verifierSeuilMinimal(produit, stockActuel);

        return saved;
    }

    /**
     * Vérifie si le stock est sous le seuil minimal et log une alerte.
     * Cette méthode sera étendue pour créer automatiquement des commandes fournisseur.
     */
    private void verifierSeuilMinimal(Produit produit, StockActuel stockActuel) {
        if (produit.getSeuilStockMinimal() != null && 
            stockActuel.getQuantite() <= produit.getSeuilStockMinimal()) {
            
            log.warn("ALERTE STOCK BAS - Produit: {} (ID: {}), Stock actuel: {}, Seuil: {}",
                    produit.getNom(), 
                    produit.getId(), 
                    stockActuel.getQuantite(),
                    produit.getSeuilStockMinimal());

            if (Boolean.TRUE.equals(produit.getReapproAuto())) {
                log.info("Réapprovisionnement automatique activé pour le produit {} - " +
                        "Une commande fournisseur devrait être créée", produit.getNom());
                // TODO: Appeler le service de création de commande fournisseur automatique
                // commandeFournisseurService.creerCommandeAutomatique(produit);
            }
        }
    }

    /**
     * Récupère le stock actuel avec toutes les relations.
     */
    public List<StockActuel> getStockComplet() {
        return stockActuelDao.findAllWithRelations();
    }

    /**
     * Récupère le stock d'un produit spécifique.
     */
    public List<StockActuel> getStockByProduit(Integer produitId) {
        if (!produitDao.existsById(produitId)) {
            throw GestionException.notFound("Produit", produitId);
        }
        return stockActuelDao.findByProduitId(produitId);
    }

    /**
     * Récupère les produits sous le seuil minimal (alertes).
     */
    public List<StockActuel> getAlertes() {
        return stockActuelDao.findStockSousSeuil();
    }

    /**
     * Récupère les produits sous le seuil avec réappro auto activé.
     */
    public List<StockActuel> getAlertesAvecReapproAuto() {
        return stockActuelDao.findStockSousSeuilAvecReapproAuto();
    }

    /**
     * Récupère l'historique des mouvements d'un produit.
     */
    public List<MouvementStock> getHistoriqueMouvements(Integer produitId) {
        if (!produitDao.existsById(produitId)) {
            throw GestionException.notFound("Produit", produitId);
        }
        return mouvementStockDao.findByProduitIdWithRelations(produitId);
    }

    /**
     * Récupère les derniers mouvements (pour tableau de bord).
     */
    public List<MouvementStock> getDerniersMouvements(int limit) {
        List<MouvementStock> mouvements = mouvementStockDao.findRecentWithRelations();
        return mouvements.stream().limit(limit).toList();
    }

    /**
     * Réserve du stock (pour commande client en attente).
     */
    @Transactional
    public void reserverStock(Integer produitId, Integer uniteConditionnementId, Integer quantite) {
        StockActuel stock = stockActuelDao
                .findByProduitIdAndUniteConditionnementId(produitId, uniteConditionnementId)
                .orElseThrow(() -> GestionException.notFound("Stock", 
                        "produit " + produitId + " / conditionnement " + uniteConditionnementId));

        if (stock.getQuantiteDisponible() < quantite) {
            throw GestionException.badRequest(
                    "Stock disponible insuffisant. Disponible: " + stock.getQuantiteDisponible() + 
                    ", demandé: " + quantite);
        }

        stock.setQuantiteReservee(stock.getQuantiteReservee() + quantite);
        recalculerQuantites(stock);
        stockActuelDao.save(stock);
    }

    /**
     * Libère du stock réservé.
     */
    @Transactional
    public void libererStock(Integer produitId, Integer uniteConditionnementId, Integer quantite) {
        StockActuel stock = stockActuelDao
                .findByProduitIdAndUniteConditionnementId(produitId, uniteConditionnementId)
                .orElseThrow(() -> GestionException.notFound("Stock", 
                        "produit " + produitId + " / conditionnement " + uniteConditionnementId));

        int nouvelleReservation = stock.getQuantiteReservee() - quantite;
        stock.setQuantiteReservee(Math.max(0, nouvelleReservation));
        recalculerQuantites(stock);
        stockActuelDao.save(stock);
    }

    // ==================== MÉTHODES UTILITAIRES PRIVÉES ====================

    /**
     * Recalcule les quantités dérivées du stock (disponible et unité de base).
     */
    private void recalculerQuantites(StockActuel stock) {
        // Quantité disponible = quantité totale - quantité réservée
        stock.setQuantiteDisponible(stock.getQuantite() - stock.getQuantiteReservee());

        // Quantité en unité de base
        if (stock.getUniteConditionnement() != null && 
            stock.getUniteConditionnement().getQuantiteUniteBase() != null) {
            stock.setQuantiteUniteBase(
                stock.getQuantite() * stock.getUniteConditionnement().getQuantiteUniteBase());
        }
    }
}
