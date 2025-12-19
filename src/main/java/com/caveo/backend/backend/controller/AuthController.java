package com.caveo.backend.backend.controller;

import com.caveo.backend.backend.dao.UtilisateurDao;
import com.caveo.backend.backend.model.Utilisateur;
import com.caveo.backend.backend.view.UtilisateurView;
import com.fasterxml.jackson.annotation.JsonView;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class AuthController {

    @Value("${jwt.secret}")
    protected String jwtSecret;

    protected final UtilisateurDao utilisateurDao;
    protected final PasswordEncoder passwordEncoder;
    protected final AuthenticationProvider authenticationProvider;

    @PutMapping("/utilisateur/{id}")
    @JsonView(UtilisateurView.class)
    public ResponseEntity<Utilisateur> update(
            @PathVariable int id,
            @RequestBody @Validated(Utilisateur.onMiseAjour.class) Utilisateur utilisateurEnvoye) {

        Optional<Utilisateur> optionalUtilisateur = utilisateurDao.findById(id);

        if(optionalUtilisateur.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Utilisateur utilisateurBaseDeDonnees = optionalUtilisateur.get();

        //setter dans l'objet provenant de la bdd uniquement les propriétés
        //que l'utilisateur à le droit de changer (par le role par exemple)
        utilisateurBaseDeDonnees.setEmail(utilisateurEnvoye.getEmail());

        //Ici on peut gérer le cas ou l'utilisateur utilise un email déja existant
        //note : on peut également gérer une exception globalement via un @ControllerAdvice
        // (voir classe IntercepteurGlobal a la racine)
        //note : actuellement l'erreur est intercepté de 2 manières: avec ce try/catch et
        //avec l'intercepteur global (ce qui n'est pas nécessaire mais pour l'exemple)
        try {
            utilisateurDao.save(utilisateurBaseDeDonnees);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(utilisateurBaseDeDonnees,HttpStatus.OK);
    }

    @PostMapping("/inscription")
    public ResponseEntity<String> signIn(
            @RequestBody @Validated(Utilisateur.onCreation.class) Utilisateur utilisateur) {

        utilisateur.setPassword(passwordEncoder.encode(utilisateur.getPassword()));
        utilisateurDao.save(utilisateur);


        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    /**
     * retourne un JWT
     * @param utilisateur
     * @return
     */
    @PostMapping("/connexion")
    public ResponseEntity<String> login(@RequestBody Utilisateur utilisateur) {

        try {

            authenticationProvider.authenticate(
                    new UsernamePasswordAuthenticationToken(utilisateur.getEmail(), utilisateur.getPassword()));

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String jwt = Jwts
                .builder()
                .setSubject(utilisateur.getEmail())
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

        return new ResponseEntity<>(jwt, HttpStatus.OK);
    }

}
