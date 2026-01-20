package com.caveo.backend.backend.controller;

import com.caveo.backend.backend.dao.UtilisateurDao;
import com.caveo.backend.backend.exception.GestionException;
import com.caveo.backend.backend.model.Utilisateur;
import com.caveo.backend.backend.security.AppUserDetails;
import com.caveo.backend.backend.security.IsAdmin;
import com.caveo.backend.backend.security.IsClient;
import com.caveo.backend.backend.security.Role;
import com.caveo.backend.backend.service.UtilisateurService;
import com.caveo.backend.backend.view.UtilisateurView;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/utilisateur")
@IsClient
public class UtilisateurController {

    private final UtilisateurDao utilisateurDao;
    private final UtilisateurService utilisateurService;
    private final PasswordEncoder passwordEncoder;

    // Récupérer la liste de tous les clients (ADMIN uniquement)
    @GetMapping("/liste-client")
    @IsAdmin
    @JsonView(UtilisateurView.class)
    public ResponseEntity<List<Utilisateur>> getAllClients() {
        List<Utilisateur> clients = utilisateurDao.findByRole(Role.CLIENT);
        return ResponseEntity.ok(clients);
    }

    // Récupérer la liste de tous les employés (ADMIN uniquement)
    @GetMapping("/liste-employe")
    @IsAdmin
    @JsonView(UtilisateurView.class)
    public ResponseEntity<List<Utilisateur>> getAllEmployes() {
        List<Utilisateur> employes = utilisateurDao.findByRole(Role.EMPLOYE);
        return ResponseEntity.ok(employes);
    }

    // Récupérer un utilisateur par ID (uniquement son propre profil)
    @GetMapping("/{id}")
    @JsonView(UtilisateurView.class)
    public ResponseEntity<Utilisateur> getById(
            @PathVariable int id,
            @AuthenticationPrincipal AppUserDetails user) {

        Utilisateur utilisateur = utilisateurDao.findById(id)
                .orElseThrow(() -> GestionException.notFound("Utilisateur", id));

        if (!user.getUtilisateur().getId().equals(id)) {
            throw GestionException.forbidden("Vous ne pouvez consulter que votre propre profil");
        }

        return ResponseEntity.ok(utilisateur);
    }

    // Création d'un utilisateur EMPLOYE par un ADMIN
    @PostMapping("/employe")
    @IsAdmin
    @JsonView(UtilisateurView.class)
    public ResponseEntity<Utilisateur> createUtilisateur(
            @RequestBody @Validated(Utilisateur.onCreation.class) Utilisateur utilisateur) {

        utilisateurService.validateEmailUniqueRegistration(utilisateur.getEmail());
        utilisateur.setPassword(passwordEncoder.encode(utilisateur.getPassword()));
        utilisateur.setRole(Role.EMPLOYE);
        utilisateur.setActif(true);

        Utilisateur savedUtilisateur = utilisateurDao.save(utilisateur);

        return new ResponseEntity<>(savedUtilisateur, HttpStatus.CREATED);
    }

 
    // Chaque utilisateur peut modifier uniquement son propre profil (informations personnelles)
    // Un ADMIN peut activer/désactiver un compte et modifier le rôle (EMPLOYE <-> ADMIN)
    @PutMapping("/{id}")
    @JsonView(UtilisateurView.class)
    public ResponseEntity<Utilisateur> update(
            @PathVariable int id,
            @RequestBody @Validated(Utilisateur.onMiseAjour.class) Utilisateur utilisateurEnvoye,
            @AuthenticationPrincipal AppUserDetails user) {

        Utilisateur utilisateurBaseDeDonnees = utilisateurDao.findById(id)
                .orElseThrow(() -> GestionException.notFound("Utilisateur", id));

        boolean isOwner = user.getUtilisateur().getId().equals(id);
        boolean isAdmin = user.getUtilisateur().getRole().equals(Role.ADMIN);

        // Modification des informations personnelles : uniquement par le propriétaire du profil
        if (utilisateurEnvoye.getEmail() != null || utilisateurEnvoye.getNom() != null ||
            utilisateurEnvoye.getPrenom() != null || utilisateurEnvoye.getTelephone() != null) {
            
            if (!isOwner) {
                throw GestionException.forbidden("Vous ne pouvez modifier que votre propre profil");
            }

            // Vérifie que la nouvelle email n'est pas déjà utilisée par un autre utilisateur
            if (utilisateurEnvoye.getEmail() != null) {
                utilisateurService.validateEmailUnique(utilisateurEnvoye.getEmail(), id);
                utilisateurBaseDeDonnees.setEmail(utilisateurEnvoye.getEmail());
            }
            
            if (utilisateurEnvoye.getNom() != null) {
                utilisateurBaseDeDonnees.setNom(utilisateurEnvoye.getNom());
            }
            if (utilisateurEnvoye.getPrenom() != null) {
                utilisateurBaseDeDonnees.setPrenom(utilisateurEnvoye.getPrenom());
            }
            if (utilisateurEnvoye.getTelephone() != null) {
                utilisateurBaseDeDonnees.setTelephone(utilisateurEnvoye.getTelephone());
            }
        }

        // Seul un ADMIN peut modifier le rôle (EMPLOYE <-> ADMIN )
        if (utilisateurEnvoye.getRole() != null) {
            if (!isAdmin) {
                throw GestionException.forbidden("Seul un administrateur peut modifier le rôle");
            }
            utilisateurBaseDeDonnees.setRole(utilisateurEnvoye.getRole());
        }
        // Seul un ADMIN peut activer/désactiver un compte
        if (utilisateurEnvoye.getActif() != null) {
            if (!isAdmin) {
                throw GestionException.forbidden("Seul un administrateur peut activer/désactiver un compte");
            }
            utilisateurBaseDeDonnees.setActif(utilisateurEnvoye.getActif());
        }

        // Met à jour la date de modification
        utilisateurBaseDeDonnees.setModifieLe(LocalDateTime.now());

        utilisateurDao.save(utilisateurBaseDeDonnees);

        return new ResponseEntity<>(utilisateurBaseDeDonnees, HttpStatus.OK);
    }



    // Chaque utilisateur peut supprimer son propre compte
    // Un ADMIN peut supprimer n'importe quel compte
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable int id,
            @AuthenticationPrincipal AppUserDetails user) {

        Utilisateur utilisateurBaseDeDonnees = utilisateurDao.findById(id)
                .orElseThrow(() -> GestionException.notFound("Utilisateur", id));

        boolean isOwner = user.getUtilisateur().getId().equals(id);
        boolean isAdmin = user.getUtilisateur().getRole().equals(Role.ADMIN);

        if (!isOwner && !isAdmin) {
            throw GestionException.forbidden("Vous ne pouvez supprimer que votre propre compte");
        }

        utilisateurDao.delete(utilisateurBaseDeDonnees);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}