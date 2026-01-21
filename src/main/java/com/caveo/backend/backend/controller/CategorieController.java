package com.caveo.backend.backend.controller;

import com.caveo.backend.backend.dao.CategorieDao;
import com.caveo.backend.backend.exception.GestionException;
import com.caveo.backend.backend.model.Categorie;
import com.caveo.backend.backend.security.IsEmploye;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
@IsEmploye
public class CategorieController {

    private final CategorieDao categorieDao;

    @GetMapping
    public ResponseEntity<List<Categorie>> getAll() {
        List<Categorie> categories = categorieDao.findByActifTrueOrderByOrdreTri();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categorie> getById(@PathVariable Integer id) {
        Categorie categorie = categorieDao.findById(id)
                .orElseThrow(() -> GestionException.notFound("Catégorie", id));
        return ResponseEntity.ok(categorie);
    }

    @PostMapping
    public ResponseEntity<Categorie> create(@RequestBody @Valid Categorie categorie) {
        if (categorieDao.existsByNomIgnoreCase(categorie.getNom())) {
            throw GestionException.conflict("Une catégorie avec le nom '" + categorie.getNom() + "' existe déjà");
        }

        Categorie saved = categorieDao.save(categorie);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categorie> update(
            @PathVariable Integer id,
            @RequestBody @Valid Categorie categorieEnvoyee) {

        Categorie categorieExistante = categorieDao.findById(id)
                .orElseThrow(() -> GestionException.notFound("Catégorie", id));

        // Vérifier si le nouveau nom existe déjà pour une autre catégorie
        if (!categorieExistante.getNom().equalsIgnoreCase(categorieEnvoyee.getNom())
                && categorieDao.existsByNomIgnoreCase(categorieEnvoyee.getNom())) {
            throw GestionException.conflict("Une catégorie avec le nom '" + categorieEnvoyee.getNom() + "' existe déjà");
        }

        categorieExistante.setNom(categorieEnvoyee.getNom());
        categorieExistante.setDescription(categorieEnvoyee.getDescription());
        categorieExistante.setOrdreTri(categorieEnvoyee.getOrdreTri());
        categorieExistante.setActif(categorieEnvoyee.getActif());

        Categorie updated = categorieDao.save(categorieExistante);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        Categorie categorie = categorieDao.findById(id)
                .orElseThrow(() -> GestionException.notFound("Catégorie", id));

        // Soft delete - désactivation
        categorie.setActif(false);
        categorieDao.save(categorie);

        return ResponseEntity.noContent().build();
    }
}
