package com.caveo.backend.backend.controller;

import com.caveo.backend.backend.dao.DomaineDao;
import com.caveo.backend.backend.exception.GestionException;
import com.caveo.backend.backend.model.Domaine;
import com.caveo.backend.backend.security.IsEmploye;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/domaines")
@IsEmploye
public class DomaineController {

    private final DomaineDao domaineDao;

    @GetMapping
    public ResponseEntity<List<Domaine>> getAll(
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String appellation) {

        List<Domaine> domaines;

        if (region != null && !region.isBlank()) {
            domaines = domaineDao.findByRegionIgnoreCaseAndActifTrue(region);
        } else if (appellation != null && !appellation.isBlank()) {
            domaines = domaineDao.findByAppellationIgnoreCaseAndActifTrue(appellation);
        } else {
            domaines = domaineDao.findByActifTrueOrderByNom();
        }

        return ResponseEntity.ok(domaines);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Domaine> getById(@PathVariable Integer id) {
        Domaine domaine = domaineDao.findById(id)
                .orElseThrow(() -> GestionException.notFound("Domaine", id));
        return ResponseEntity.ok(domaine);
    }

    @PostMapping
    public ResponseEntity<Domaine> create(@RequestBody @Valid Domaine domaine) {
        if (domaineDao.existsByNomIgnoreCase(domaine.getNom())) {
            throw GestionException.conflict("Un domaine avec le nom '" + domaine.getNom() + "' existe déjà");
        }

        Domaine saved = domaineDao.save(domaine);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Domaine> update(
            @PathVariable Integer id,
            @RequestBody @Valid Domaine domaineEnvoye) {

        Domaine domaineExistant = domaineDao.findById(id)
                .orElseThrow(() -> GestionException.notFound("Domaine", id));

        // Vérifier si le nouveau nom existe déjà pour un autre domaine
        if (!domaineExistant.getNom().equalsIgnoreCase(domaineEnvoye.getNom())
                && domaineDao.existsByNomIgnoreCase(domaineEnvoye.getNom())) {
            throw GestionException.conflict("Un domaine avec le nom '" + domaineEnvoye.getNom() + "' existe déjà");
        }

        domaineExistant.setNom(domaineEnvoye.getNom());
        domaineExistant.setRegion(domaineEnvoye.getRegion());
        domaineExistant.setAppellation(domaineEnvoye.getAppellation());
        domaineExistant.setSurfaceVignobleHa(domaineEnvoye.getSurfaceVignobleHa());
        domaineExistant.setTypeSol(domaineEnvoye.getTypeSol());
        domaineExistant.setCepages(domaineEnvoye.getCepages());
        domaineExistant.setVigneron(domaineEnvoye.getVigneron());
        domaineExistant.setDescription(domaineEnvoye.getDescription());
        domaineExistant.setSiteWeb(domaineEnvoye.getSiteWeb());
        domaineExistant.setLatitude(domaineEnvoye.getLatitude());
        domaineExistant.setLongitude(domaineEnvoye.getLongitude());
        domaineExistant.setActif(domaineEnvoye.getActif());

        Domaine updated = domaineDao.save(domaineExistant);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        Domaine domaine = domaineDao.findById(id)
                .orElseThrow(() -> GestionException.notFound("Domaine", id));

        // Soft delete - désactivation
        domaine.setActif(false);
        domaineDao.save(domaine);

        return ResponseEntity.noContent().build();
    }
}
