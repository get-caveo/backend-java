package com.caveo.backend.backend.service;

import com.caveo.backend.backend.dao.*;
import com.caveo.backend.backend.dto.LignePanierDto;
import com.caveo.backend.backend.exception.GestionException;
import com.caveo.backend.backend.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PanierService {

    private final PanierDao panierDao;
    private final LignePanierDao lignePanierDao;
    private final ProduitDao produitDao;
    private final UniteConditionnementDao uniteConditionnementDao;
    private final ConditionnementProduitDao conditionnementProduitDao;

    /**
     * Récupère le panier du client, ou en crée un nouveau.
     */
    public Panier getPanier(Integer clientId) {
        Panier panier = panierDao.findByClientIdWithDetails(clientId)
                .orElseGet(() -> {
                    Panier p = new Panier();
                    Utilisateur client = new Utilisateur();
                    client.setId(clientId);
                    p.setClient(client);
                    return panierDao.save(p);
                });
        enrichirPrix(panier);
        return panier;
    }

    /**
     * Ajoute un produit au panier. Si le même produit+conditionnement existe déjà, fusionne les quantités.
     */
    @Transactional
    public Panier ajouterArticle(Integer clientId, LignePanierDto dto) {
        Panier panier = getPanierOuCreer(clientId);

        Produit produit = produitDao.findById(dto.getProduitId())
                .orElseThrow(() -> GestionException.notFound("Produit", dto.getProduitId()));

        UniteConditionnement unite = uniteConditionnementDao.findById(dto.getUniteConditionnementId())
                .orElseThrow(() -> GestionException.notFound("Unité de conditionnement", dto.getUniteConditionnementId()));

        // Vérifier que le conditionnement existe pour ce produit
        conditionnementProduitDao.findFirstByProduitIdAndUniteConditionnementId(produit.getId(), unite.getId())
                .orElseThrow(() -> GestionException.badRequest(
                        "Ce conditionnement n'est pas disponible pour ce produit"));

        // Vérifier si l'article existe déjà dans le panier
        var ligneExistante = lignePanierDao.findByPanierAndProduitAndUnite(
                panier.getId(), produit.getId(), unite.getId());

        if (ligneExistante.isPresent()) {
            LignePanier ligne = ligneExistante.get();
            ligne.setQuantite(ligne.getQuantite() + dto.getQuantite());
            lignePanierDao.save(ligne);
        } else {
            LignePanier ligne = new LignePanier();
            ligne.setPanier(panier);
            ligne.setProduit(produit);
            ligne.setUniteConditionnement(unite);
            ligne.setQuantite(dto.getQuantite());
            panier.getLignes().add(ligne);
        }

        panierDao.save(panier);
        Panier result = panierDao.findByClientIdWithDetails(clientId).orElse(panier);
        enrichirPrix(result);
        return result;
    }

    /**
     * Met à jour la quantité d'une ligne du panier.
     */
    @Transactional
    public Panier modifierQuantite(Integer clientId, Integer ligneId, Integer quantite) {
        Panier panier = getPanierOuCreer(clientId);

        LignePanier ligne = lignePanierDao.findById(ligneId)
                .orElseThrow(() -> GestionException.notFound("Ligne panier", ligneId));

        if (!ligne.getPanier().getId().equals(panier.getId())) {
            throw GestionException.badRequest("Cette ligne n'appartient pas à votre panier");
        }

        if (quantite <= 0) {
            panier.getLignes().remove(ligne);
            lignePanierDao.delete(ligne);
        } else {
            ligne.setQuantite(quantite);
            lignePanierDao.save(ligne);
        }

        Panier result = panierDao.findByClientIdWithDetails(clientId).orElse(panier);
        enrichirPrix(result);
        return result;
    }

    /**
     * Supprime une ligne du panier.
     */
    @Transactional
    public Panier supprimerArticle(Integer clientId, Integer ligneId) {
        Panier panier = getPanierOuCreer(clientId);

        LignePanier ligne = lignePanierDao.findById(ligneId)
                .orElseThrow(() -> GestionException.notFound("Ligne panier", ligneId));

        if (!ligne.getPanier().getId().equals(panier.getId())) {
            throw GestionException.badRequest("Cette ligne n'appartient pas à votre panier");
        }

        panier.getLignes().remove(ligne);
        lignePanierDao.delete(ligne);

        Panier result = panierDao.findByClientIdWithDetails(clientId).orElse(panier);
        enrichirPrix(result);
        return result;
    }

    /**
     * Vide entièrement le panier.
     */
    @Transactional
    public void viderPanier(Integer clientId) {
        Panier panier = panierDao.findByClientId(clientId).orElse(null);
        if (panier != null) {
            panier.getLignes().clear();
            panierDao.save(panier);
        }
    }

    /**
     * Enrichit chaque ligne du panier avec le prix unitaire issu de ConditionnementProduit.
     */
    private void enrichirPrix(Panier panier) {
        if (panier == null || panier.getLignes() == null) return;
        for (LignePanier ligne : panier.getLignes()) {
            conditionnementProduitDao.findFirstByProduitIdAndUniteConditionnementId(
                    ligne.getProduit().getId(),
                    ligne.getUniteConditionnement().getId()
            ).ifPresent(cp -> ligne.setPrixUnitaire(cp.getPrixUnitaire()));
        }
    }

    private Panier getPanierOuCreer(Integer clientId) {
        return panierDao.findByClientId(clientId)
                .orElseGet(() -> {
                    Panier panier = new Panier();
                    Utilisateur client = new Utilisateur();
                    client.setId(clientId);
                    panier.setClient(client);
                    return panierDao.save(panier);
                });
    }
}
