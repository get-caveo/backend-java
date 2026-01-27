package com.caveo.backend.backend.controller;

import com.caveo.backend.backend.dao.*;
import com.caveo.backend.backend.dto.ProduitCreateDto;
import com.caveo.backend.backend.exception.GestionException;
import com.caveo.backend.backend.model.*;
import com.caveo.backend.backend.security.IsEmploye;
import com.caveo.backend.backend.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/produits")
@IsEmploye
public class ProduitController {

    private final ProduitDao produitDao;
    private final CategorieDao categorieDao;
    private final DomaineDao domaineDao;
    private final ConditionnementProduitDao conditionnementProduitDao;
    private final FournisseurProduitDao fournisseurProduitDao;
    private final UniteConditionnementDao uniteConditionnementDao;
    private final FournisseurDao fournisseurDao;
    private final StockService stockService;

    // ==================== PRODUITS ====================

    @GetMapping
    public ResponseEntity<List<Produit>> getAll(
            @RequestParam(required = false) Integer categorieId,
            @RequestParam(required = false) Integer domaineId,
            @RequestParam(required = false) Integer millesime,
            @RequestParam(required = false) String search) {

        List<Produit> produits;

        if (categorieId != null) {
            produits = produitDao.findByCategorieIdAndActifTrue(categorieId);
        } else if (domaineId != null) {
            produits = produitDao.findByDomaineIdAndActifTrue(domaineId);
        } else if (millesime != null) {
            produits = produitDao.findByMillesimeAndActifTrue(millesime);
        } else if (search != null && !search.isBlank()) {
            produits = produitDao.searchByNom(search);
        } else {
            produits = produitDao.findByActifTrueOrderByNom();
        }

        return ResponseEntity.ok(produits);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produit> getById(@PathVariable Integer id) {
        // Récupère le produit avec toutes ses relations (vue agrégée)
        Produit produit = produitDao.findByIdWithAllRelations(id)
                .orElseThrow(() -> GestionException.notFound("Produit", id));
        return ResponseEntity.ok(produit);
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<Produit> getBySku(@PathVariable String sku) {
        Produit produit = produitDao.findBySku(sku)
                .orElseThrow(() -> GestionException.notFound("Produit", sku));
        return ResponseEntity.ok(produit);
    }

    @GetMapping("/code-barre/{codeBarre}")
    public ResponseEntity<Produit> getByCodeBarre(@PathVariable String codeBarre) {
        Produit produit = produitDao.findByCodeBarre(codeBarre)
                .orElseThrow(() -> GestionException.notFound("Produit avec code barre", codeBarre));
        return ResponseEntity.ok(produit);
    }

    @PostMapping
    public ResponseEntity<Produit> create(@RequestBody @Valid ProduitCreateDto dto) {
        // Vérifier SKU unique
        if (produitDao.existsBySkuIgnoreCase(dto.getSku())) {
            throw GestionException.conflict("Un produit avec le SKU '" + dto.getSku() + "' existe déjà");
        }

        // Vérifier code barre unique si fourni
        if (dto.getCodeBarre() != null && !dto.getCodeBarre().isBlank()
                && produitDao.existsByCodeBarre(dto.getCodeBarre())) {
            throw GestionException.conflict("Un produit avec le code barre '" + dto.getCodeBarre() + "' existe déjà");
        }

        // Vérifier que la catégorie existe
        Categorie categorie = categorieDao.findById(dto.getCategorieId())
                .orElseThrow(() -> GestionException.notFound("Catégorie", dto.getCategorieId()));

        // Vérifier que le fournisseur existe
        Fournisseur fournisseur = fournisseurDao.findById(dto.getFournisseurId())
                .orElseThrow(() -> GestionException.notFound("Fournisseur", dto.getFournisseurId()));

        // Vérifier que le domaine existe si fourni
        Domaine domaine = null;
        if (dto.getDomaineId() != null) {
            domaine = domaineDao.findById(dto.getDomaineId())
                    .orElseThrow(() -> GestionException.notFound("Domaine", dto.getDomaineId()));
        }

        // Créer le produit
        Produit produit = new Produit();
        produit.setSku(dto.getSku());
        produit.setNom(dto.getNom());
        produit.setDescription(dto.getDescription());
        produit.setCategorie(categorie);
        produit.setDomaine(domaine);
        produit.setMillesime(dto.getMillesime());
        produit.setDegreAlcool(dto.getDegreAlcool());
        produit.setCodeBarre(dto.getCodeBarre());
        produit.setImageUrl(dto.getImageUrl());
        produit.setNotesDegustation(dto.getNotesDegustation());
        produit.setTemperatureService(dto.getTemperatureService());
        produit.setConditionsConservation(dto.getConditionsConservation());
        produit.setSeuilStockMinimal(dto.getSeuilStockMinimal() != null ? dto.getSeuilStockMinimal() : 5);
        produit.setReapproAuto(dto.getReapproAuto() != null ? dto.getReapproAuto() : true);
        produit.setActif(true);

        Produit savedProduit = produitDao.save(produit);

        // Créer la relation avec le fournisseur obligatoire
        FournisseurProduit fournisseurProduit = new FournisseurProduit();
        fournisseurProduit.setProduit(savedProduit);
        fournisseurProduit.setFournisseur(fournisseur);
        fournisseurProduit.setPrixFournisseur(dto.getPrixFournisseur());
        fournisseurProduit.setDelaiApproJours(dto.getDelaiApproJours() != null ? dto.getDelaiApproJours() : 0);
        fournisseurProduitDao.save(fournisseurProduit);

        // Recharger le produit avec ses relations
        Produit produitComplet = produitDao.findByIdWithAllRelations(savedProduit.getId())
                .orElse(savedProduit);

        return new ResponseEntity<>(produitComplet, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Produit> update(
            @PathVariable Integer id,
            @RequestBody @Valid Produit produitEnvoye) {

        Produit produitExistant = produitDao.findById(id)
                .orElseThrow(() -> GestionException.notFound("Produit", id));

        // Vérifier SKU unique si modifié
        if (!produitExistant.getSku().equalsIgnoreCase(produitEnvoye.getSku())
                && produitDao.existsBySkuIgnoreCase(produitEnvoye.getSku())) {
            throw GestionException.conflict("Un produit avec le SKU '" + produitEnvoye.getSku() + "' existe déjà");
        }

        // Vérifier code barre unique si modifié
        if (produitEnvoye.getCodeBarre() != null && !produitEnvoye.getCodeBarre().isBlank()) {
            String existingCodeBarre = produitExistant.getCodeBarre();
            if ((existingCodeBarre == null || !existingCodeBarre.equals(produitEnvoye.getCodeBarre()))
                    && produitDao.existsByCodeBarre(produitEnvoye.getCodeBarre())) {
                throw GestionException.conflict("Un produit avec le code barre '" + produitEnvoye.getCodeBarre() + "' existe déjà");
            }
        }

        // Vérifier catégorie
        if (produitEnvoye.getCategorie() != null && produitEnvoye.getCategorie().getId() != null) {
            Categorie categorie = categorieDao.findById(produitEnvoye.getCategorie().getId())
                    .orElseThrow(() -> GestionException.notFound("Catégorie", produitEnvoye.getCategorie().getId()));
            produitExistant.setCategorie(categorie);
        }

        // Vérifier domaine
        if (produitEnvoye.getDomaine() != null && produitEnvoye.getDomaine().getId() != null) {
            Domaine domaine = domaineDao.findById(produitEnvoye.getDomaine().getId())
                    .orElseThrow(() -> GestionException.notFound("Domaine", produitEnvoye.getDomaine().getId()));
            produitExistant.setDomaine(domaine);
        } else {
            produitExistant.setDomaine(null);
        }

        // Mettre à jour les champs
        produitExistant.setSku(produitEnvoye.getSku());
        produitExistant.setNom(produitEnvoye.getNom());
        produitExistant.setDescription(produitEnvoye.getDescription());
        produitExistant.setMillesime(produitEnvoye.getMillesime());
        produitExistant.setDegreAlcool(produitEnvoye.getDegreAlcool());
        produitExistant.setCodeBarre(produitEnvoye.getCodeBarre());
        produitExistant.setImageUrl(produitEnvoye.getImageUrl());
        produitExistant.setNotesDegustation(produitEnvoye.getNotesDegustation());
        produitExistant.setTemperatureService(produitEnvoye.getTemperatureService());
        produitExistant.setConditionsConservation(produitEnvoye.getConditionsConservation());
        produitExistant.setSeuilStockMinimal(produitEnvoye.getSeuilStockMinimal());
        produitExistant.setReapproAuto(produitEnvoye.getReapproAuto());
        produitExistant.setActif(produitEnvoye.getActif());

        Produit updated = produitDao.save(produitExistant);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        Produit produit = produitDao.findById(id)
                .orElseThrow(() -> GestionException.notFound("Produit", id));

        // Soft delete
        produit.setActif(false);
        produitDao.save(produit);

        return ResponseEntity.noContent().build();
    }

    // ==================== CONDITIONNEMENTS DU PRODUIT ====================

    @GetMapping("/{id}/conditionnements")
    public ResponseEntity<List<ConditionnementProduit>> getConditionnements(@PathVariable Integer id) {
        if (!produitDao.existsById(id)) {
            throw GestionException.notFound("Produit", id);
        }
        List<ConditionnementProduit> conditionnements = conditionnementProduitDao.findByProduitId(id);
        return ResponseEntity.ok(conditionnements);
    }

    @PostMapping("/{id}/conditionnements")
    public ResponseEntity<ConditionnementProduit> addConditionnement(
            @PathVariable Integer id,
            @RequestBody @Valid ConditionnementProduit conditionnement) {

        Produit produit = produitDao.findById(id)
                .orElseThrow(() -> GestionException.notFound("Produit", id));

        // Vérifier que l'unité de conditionnement existe
        UniteConditionnement unite = uniteConditionnementDao.findById(conditionnement.getUniteConditionnement().getId())
                .orElseThrow(() -> GestionException.notFound("Unité de conditionnement",
                        conditionnement.getUniteConditionnement().getId()));

        // Vérifier qu'il n'existe pas déjà
        if (conditionnementProduitDao.existsByProduitIdAndUniteConditionnementId(id, unite.getId())) {
            throw GestionException.conflict("Ce conditionnement existe déjà pour ce produit");
        }

        conditionnement.setProduit(produit);
        conditionnement.setUniteConditionnement(unite);

        ConditionnementProduit saved = conditionnementProduitDao.save(conditionnement);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/conditionnements/{condId}")
    public ResponseEntity<ConditionnementProduit> updateConditionnement(
            @PathVariable Integer id,
            @PathVariable Integer condId,
            @RequestBody @Valid ConditionnementProduit conditionnementEnvoye) {

        if (!produitDao.existsById(id)) {
            throw GestionException.notFound("Produit", id);
        }

        ConditionnementProduit conditionnementExistant = conditionnementProduitDao.findById(condId)
                .orElseThrow(() -> GestionException.notFound("Conditionnement", condId));

        // Vérifier que le conditionnement appartient au produit
        if (!conditionnementExistant.getProduit().getId().equals(id)) {
            throw GestionException.badRequest("Ce conditionnement n'appartient pas à ce produit");
        }

        conditionnementExistant.setPrixUnitaire(conditionnementEnvoye.getPrixUnitaire());
        conditionnementExistant.setDisponible(conditionnementEnvoye.getDisponible());

        ConditionnementProduit updated = conditionnementProduitDao.save(conditionnementExistant);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}/conditionnements/{condId}")
    public ResponseEntity<Void> deleteConditionnement(
            @PathVariable Integer id,
            @PathVariable Integer condId) {

        if (!produitDao.existsById(id)) {
            throw GestionException.notFound("Produit", id);
        }

        ConditionnementProduit conditionnement = conditionnementProduitDao.findById(condId)
                .orElseThrow(() -> GestionException.notFound("Conditionnement", condId));

        if (!conditionnement.getProduit().getId().equals(id)) {
            throw GestionException.badRequest("Ce conditionnement n'appartient pas à ce produit");
        }

        conditionnementProduitDao.delete(conditionnement);
        return ResponseEntity.noContent().build();
    }

    // ==================== FOURNISSEURS DU PRODUIT ====================

    @GetMapping("/{id}/fournisseurs")
    public ResponseEntity<List<FournisseurProduit>> getFournisseurs(@PathVariable Integer id) {
        if (!produitDao.existsById(id)) {
            throw GestionException.notFound("Produit", id);
        }
        List<FournisseurProduit> fournisseurs = fournisseurProduitDao.findByProduitId(id);
        return ResponseEntity.ok(fournisseurs);
    }

    @PostMapping("/{id}/fournisseurs")
    public ResponseEntity<FournisseurProduit> addFournisseur(
            @PathVariable Integer id,
            @RequestBody FournisseurProduit fournisseurProduit) {

        Produit produit = produitDao.findById(id)
                .orElseThrow(() -> GestionException.notFound("Produit", id));

        // Vérifier que le fournisseur existe
        Fournisseur fournisseur = fournisseurDao.findById(fournisseurProduit.getFournisseur().getId())
                .orElseThrow(() -> GestionException.notFound("Fournisseur",
                        fournisseurProduit.getFournisseur().getId()));

        // Vérifier qu'il n'existe pas déjà
        if (fournisseurProduitDao.existsByProduitIdAndFournisseurId(id, fournisseur.getId())) {
            throw GestionException.conflict("Ce fournisseur est déjà associé à ce produit");
        }

        fournisseurProduit.setProduit(produit);
        fournisseurProduit.setFournisseur(fournisseur);

        FournisseurProduit saved = fournisseurProduitDao.save(fournisseurProduit);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/fournisseurs/{fournId}")
    public ResponseEntity<FournisseurProduit> updateFournisseurProduit(
            @PathVariable Integer id,
            @PathVariable Integer fournId,
            @RequestBody FournisseurProduit fournisseurProduitEnvoye) {

        if (!produitDao.existsById(id)) {
            throw GestionException.notFound("Produit", id);
        }

        // fournId est l'ID de la relation FournisseurProduit
        FournisseurProduit fpExistant = fournisseurProduitDao.findById(fournId)
                .orElseThrow(() -> GestionException.notFound("Association fournisseur-produit", fournId));

        if (!fpExistant.getProduit().getId().equals(id)) {
            throw GestionException.badRequest("Cette association n'appartient pas à ce produit");
        }

        fpExistant.setPrixFournisseur(fournisseurProduitEnvoye.getPrixFournisseur());
        fpExistant.setDelaiApproJours(fournisseurProduitEnvoye.getDelaiApproJours());

        FournisseurProduit updated = fournisseurProduitDao.save(fpExistant);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}/fournisseurs/{fournId}")
    public ResponseEntity<Void> removeFournisseur(
            @PathVariable Integer id,
            @PathVariable Integer fournId) {

        if (!produitDao.existsById(id)) {
            throw GestionException.notFound("Produit", id);
        }

        FournisseurProduit fp = fournisseurProduitDao.findById(fournId)
                .orElseThrow(() -> GestionException.notFound("Association fournisseur-produit", fournId));

        if (!fp.getProduit().getId().equals(id)) {
            throw GestionException.badRequest("Cette association n'appartient pas à ce produit");
        }

        fournisseurProduitDao.delete(fp);
        return ResponseEntity.noContent().build();
    }

    // ==================== STOCK DU PRODUIT ====================

    @GetMapping("/{id}/stock")
    public ResponseEntity<List<StockActuel>> getStockProduit(@PathVariable Integer id) {
        List<StockActuel> stock = stockService.getStockByProduit(id);
        return ResponseEntity.ok(stock);
    }

    @GetMapping("/{id}/mouvements")
    public ResponseEntity<List<MouvementStock>> getMouvementsProduit(@PathVariable Integer id) {
        List<MouvementStock> mouvements = stockService.getHistoriqueMouvements(id);
        return ResponseEntity.ok(mouvements);
    }
}
