package com.caveo.backend.backend.service;

import com.caveo.backend.backend.dao.UtilisateurDao;
import com.caveo.backend.backend.exception.GestionException;
import com.caveo.backend.backend.model.Utilisateur;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UtilisateurService {
    private final UtilisateurDao utilisateurDao;

    //Vérifie si une email existe déjà dans la base de données (pour la création)
    public void validateEmailUniqueRegistration(String email) {
        Optional<Utilisateur> existingUser = utilisateurDao.findByEmail(email);
        if (existingUser.isPresent()) {
            throw GestionException.emailExists(email);
        }
    }

    //Vérifie si une email existe déjà dans la base de données en excluant l'utilisateur courant (pour la mise à jour)
    public void validateEmailUnique(String email, Integer excludeUserId) {
        Optional<Utilisateur> existingUser = utilisateurDao.findByEmail(email);
        if (existingUser.isPresent() && !existingUser.get().getId().equals(excludeUserId)) {
            throw GestionException.emailExists(email);
        }
    }

}
