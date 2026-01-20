package com.caveo.backend.backend.controller;

import com.caveo.backend.backend.dao.UtilisateurDao;
import com.caveo.backend.backend.exception.GestionException;
import com.caveo.backend.backend.model.Utilisateur;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;



@RestController
@RequiredArgsConstructor
public class AuthController {

    @Value("${jwt.secret}")
    protected String jwtSecret;

    protected final UtilisateurDao utilisateurDao;
    protected final PasswordEncoder passwordEncoder;
    protected final AuthenticationProvider authenticationProvider;


    @PostMapping("/connexion")
    public ResponseEntity<String> login(@RequestBody Utilisateur utilisateur) {

        try {
            authenticationProvider.authenticate(
                    new UsernamePasswordAuthenticationToken(utilisateur.getEmail(), utilisateur.getPassword()));
        } catch (Exception e) {
            throw GestionException.unauthorized("Email ou mot de passe incorrect");
        }

       
        Utilisateur utilisateurConnecte = utilisateurDao.findByEmail(utilisateur.getEmail())
                .orElseThrow(() -> GestionException.unauthorized("Email ou mot de passe incorrect"));
                
         // Vérifie que le compte est actif
        if (!utilisateurConnecte.getActif()) {
            throw GestionException.forbidden("Votre compte a été désactivé. Contactez un administrateur.");
        }

        String jwt = Jwts
                .builder()
                .setSubject(utilisateur.getEmail())
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

        return new ResponseEntity<>(jwt, HttpStatus.OK);
    }

}
