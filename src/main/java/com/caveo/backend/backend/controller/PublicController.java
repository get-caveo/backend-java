package com.caveo.backend.backend.controller;

import com.caveo.backend.backend.dao.CategorieDao;
import com.caveo.backend.backend.dao.DomaineDao;
import com.caveo.backend.backend.dao.ProduitDao;
import com.caveo.backend.backend.exception.GestionException;
import com.caveo.backend.backend.model.Categorie;
import com.caveo.backend.backend.model.Domaine;
import com.caveo.backend.backend.model.Produit;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur public pour le front office - accessible sans authentification
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public")
public class PublicController {

    private final ProduitDao produitDao;
    private final CategorieDao categorieDao;
    private final DomaineDao domaineDao;

    // ==================== PRODUITS PUBLICS ====================

    @GetMapping("/produits")
    public ResponseEntity<List<Produit>> getAllProduits(
            @RequestParam(required = false) Integer categorieId,
            @RequestParam(required = false) Integer domaineId,
            @RequestParam(required = false) Integer millesime,
            @RequestParam(required = false) String search) {

        List<Produit> produits;

        if (categorieId != null) {
            produits = produitDao.findByCategorieWithConditionnements(categorieId);
        } else if (domaineId != null) {
            produits = produitDao.findByDomaineWithConditionnements(domaineId);
        } else if (millesime != null) {
            produits = produitDao.findByMillesimeAndActifTrue(millesime);
        } else if (search != null && !search.isBlank()) {
            produits = produitDao.searchByNomWithConditionnements(search);
        } else {
            produits = produitDao.findAllProduitsWithConditionnements();
        }

        return ResponseEntity.ok(produits);
    }

    @GetMapping("/produits/{id}")
    public ResponseEntity<Produit> getProduitById(@PathVariable Integer id) {
        Produit produit = produitDao.findByIdWithAllRelations(id)
                .orElseThrow(() -> GestionException.notFound("Produit", id));
        return ResponseEntity.ok(produit);
    }

    // ==================== CATEGORIES PUBLIQUES ====================

    @GetMapping("/categories")
    public ResponseEntity<List<Categorie>> getAllCategories() {
        List<Categorie> categories = categorieDao.findByActifTrueOrderByOrdreTri();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<Categorie> getCategorieById(@PathVariable Integer id) {
        Categorie categorie = categorieDao.findById(id)
                .orElseThrow(() -> GestionException.notFound("Catégorie", id));
        return ResponseEntity.ok(categorie);
    }

    // ==================== DOMAINES PUBLICS ====================

    @GetMapping("/domaines")
    public ResponseEntity<List<Domaine>> getAllDomaines() {
        List<Domaine> domaines = domaineDao.findByActifTrueOrderByNom();
        return ResponseEntity.ok(domaines);
    }

    @GetMapping("/domaines/{id}")
    public ResponseEntity<Domaine> getDomaineById(@PathVariable Integer id) {
        Domaine domaine = domaineDao.findById(id)
                .orElseThrow(() -> GestionException.notFound("Domaine", id));
        return ResponseEntity.ok(domaine);
    }
}
