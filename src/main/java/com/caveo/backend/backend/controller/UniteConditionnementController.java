package com.caveo.backend.backend.controller;

import com.caveo.backend.backend.dao.UniteConditionnementDao;
import com.caveo.backend.backend.exception.GestionException;
import com.caveo.backend.backend.model.UniteConditionnement;
import com.caveo.backend.backend.security.IsEmploye;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/unites-conditionnement")
@IsEmploye
public class UniteConditionnementController {

    private final UniteConditionnementDao uniteConditionnementDao;

    @GetMapping
    public ResponseEntity<List<UniteConditionnement>> getAll(
            @RequestParam(required = false) Boolean vendableOnly) {

        List<UniteConditionnement> unites;

        if (Boolean.TRUE.equals(vendableOnly)) {
            unites = uniteConditionnementDao.findByEstVendableTrueAndActifTrueOrderByOrdreTri();
        } else {
            unites = uniteConditionnementDao.findByActifTrueOrderByOrdreTri();
        }

        return ResponseEntity.ok(unites);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UniteConditionnement> getById(@PathVariable Integer id) {
        UniteConditionnement unite = uniteConditionnementDao.findById(id)
                .orElseThrow(() -> GestionException.notFound("Unité de conditionnement", id));
        return ResponseEntity.ok(unite);
    }

    @GetMapping("/unite-base")
    public ResponseEntity<UniteConditionnement> getUniteBase() {
        UniteConditionnement uniteBase = uniteConditionnementDao.findByEstUniteBaseTrueAndActifTrue();
        if (uniteBase == null) {
            throw GestionException.notFound("Unité de base", "active");
        }
        return ResponseEntity.ok(uniteBase);
    }

    @PostMapping
    public ResponseEntity<UniteConditionnement> create(@RequestBody @Valid UniteConditionnement unite) {
        if (uniteConditionnementDao.existsByNomIgnoreCase(unite.getNom())) {
            throw GestionException.conflict("Une unité avec le nom '" + unite.getNom() + "' existe déjà");
        }
        if (uniteConditionnementDao.existsByNomCourtIgnoreCase(unite.getNomCourt())) {
            throw GestionException.conflict("Une unité avec le nom court '" + unite.getNomCourt() + "' existe déjà");
        }

        // Si on veut définir cette unité comme unité de base, vérifier qu'il n'y en a pas déjà une
        if (Boolean.TRUE.equals(unite.getEstUniteBase())) {
            UniteConditionnement existingBase = uniteConditionnementDao.findByEstUniteBaseTrueAndActifTrue();
            if (existingBase != null) {
                throw GestionException.conflict("Une unité de base existe déjà : " + existingBase.getNom());
            }
        }

        UniteConditionnement saved = uniteConditionnementDao.save(unite);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UniteConditionnement> update(
            @PathVariable Integer id,
            @RequestBody @Valid UniteConditionnement uniteEnvoyee) {

        UniteConditionnement uniteExistante = uniteConditionnementDao.findById(id)
                .orElseThrow(() -> GestionException.notFound("Unité de conditionnement", id));

        // Vérifier les doublons de nom
        if (!uniteExistante.getNom().equalsIgnoreCase(uniteEnvoyee.getNom())
                && uniteConditionnementDao.existsByNomIgnoreCase(uniteEnvoyee.getNom())) {
            throw GestionException.conflict("Une unité avec le nom '" + uniteEnvoyee.getNom() + "' existe déjà");
        }

        // Vérifier les doublons de nom court
        if (!uniteExistante.getNomCourt().equalsIgnoreCase(uniteEnvoyee.getNomCourt())
                && uniteConditionnementDao.existsByNomCourtIgnoreCase(uniteEnvoyee.getNomCourt())) {
            throw GestionException.conflict("Une unité avec le nom court '" + uniteEnvoyee.getNomCourt() + "' existe déjà");
        }

        // Vérifier l'unité de base
        if (Boolean.TRUE.equals(uniteEnvoyee.getEstUniteBase()) && !Boolean.TRUE.equals(uniteExistante.getEstUniteBase())) {
            UniteConditionnement existingBase = uniteConditionnementDao.findByEstUniteBaseTrueAndActifTrue();
            if (existingBase != null && !existingBase.getId().equals(id)) {
                throw GestionException.conflict("Une unité de base existe déjà : " + existingBase.getNom());
            }
        }

        uniteExistante.setNom(uniteEnvoyee.getNom());
        uniteExistante.setNomCourt(uniteEnvoyee.getNomCourt());
        uniteExistante.setQuantiteUniteBase(uniteEnvoyee.getQuantiteUniteBase());
        uniteExistante.setDescription(uniteEnvoyee.getDescription());
        uniteExistante.setDimensionsCm(uniteEnvoyee.getDimensionsCm());
        uniteExistante.setPoidsKg(uniteEnvoyee.getPoidsKg());
        uniteExistante.setVolumeMl(uniteEnvoyee.getVolumeMl());
        uniteExistante.setEstVendable(uniteEnvoyee.getEstVendable());
        uniteExistante.setEstUniteBase(uniteEnvoyee.getEstUniteBase());
        uniteExistante.setOrdreTri(uniteEnvoyee.getOrdreTri());
        uniteExistante.setActif(uniteEnvoyee.getActif());

        UniteConditionnement updated = uniteConditionnementDao.save(uniteExistante);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        UniteConditionnement unite = uniteConditionnementDao.findById(id)
                .orElseThrow(() -> GestionException.notFound("Unité de conditionnement", id));

        // Soft delete - désactivation
        unite.setActif(false);
        uniteConditionnementDao.save(unite);

        return ResponseEntity.noContent().build();
    }
}
