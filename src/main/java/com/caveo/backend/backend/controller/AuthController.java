package com.caveo.backend.backend.controller;

import com.caveo.backend.backend.dao.UtilisateurDao;
import com.caveo.backend.backend.dto.AuthResponse;
import com.caveo.backend.backend.dto.LoginRequest;
import com.caveo.backend.backend.dto.RegisterRequest;
import com.caveo.backend.backend.exception.GestionException;
import com.caveo.backend.backend.model.Utilisateur;
import com.caveo.backend.backend.security.Role;
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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    @Value("${jwt.secret}")
    protected String jwtSecret;

    protected final UtilisateurDao utilisateurDao;
    protected final PasswordEncoder passwordEncoder;
    protected final AuthenticationProvider authenticationProvider;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {

        try {
            authenticationProvider.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        } catch (Exception e) {
            throw GestionException.unauthorized("Email ou mot de passe incorrect");
        }

        Utilisateur utilisateurConnecte = utilisateurDao.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> GestionException.unauthorized("Email ou mot de passe incorrect"));
                
        // Vérifie que le compte est actif
        if (!utilisateurConnecte.getActif()) {
            throw GestionException.forbidden("Votre compte a été désactivé. Contactez un administrateur.");
        }

        String jwt = generateToken(utilisateurConnecte);

        AuthResponse response = new AuthResponse(
                jwt,
                utilisateurConnecte.getId(),
                utilisateurConnecte.getEmail(),
                utilisateurConnecte.getPrenom(),
                utilisateurConnecte.getNom(),
                utilisateurConnecte.getRole().name()
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest) {
        
        // Vérifier si l'email existe déjà
        if (utilisateurDao.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw GestionException.conflict("Un compte avec cet email existe déjà");
        }

        // Créer le nouvel utilisateur
        Utilisateur nouvelUtilisateur = new Utilisateur();
        nouvelUtilisateur.setEmail(registerRequest.getEmail());
        nouvelUtilisateur.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        nouvelUtilisateur.setPrenom(registerRequest.getPrenom());
        nouvelUtilisateur.setNom(registerRequest.getNom());
        nouvelUtilisateur.setTelephone(registerRequest.getTelephone());
        nouvelUtilisateur.setRole(Role.CLIENT); // Par défaut, un nouvel utilisateur est un client
        nouvelUtilisateur.setActif(true);

        Utilisateur savedUtilisateur = utilisateurDao.save(nouvelUtilisateur);

        String jwt = generateToken(savedUtilisateur);

        AuthResponse response = new AuthResponse(
                jwt,
                savedUtilisateur.getId(),
                savedUtilisateur.getEmail(),
                savedUtilisateur.getPrenom(),
                savedUtilisateur.getNom(),
                savedUtilisateur.getRole().name()
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw GestionException.unauthorized("Token manquant ou invalide");
        }

        String token = authHeader.substring(7);
        
        try {
            String email = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();

            Utilisateur utilisateur = utilisateurDao.findByEmail(email)
                    .orElseThrow(() -> GestionException.unauthorized("Utilisateur non trouvé"));

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", utilisateur.getId());
            userInfo.put("email", utilisateur.getEmail());
            userInfo.put("prenom", utilisateur.getPrenom());
            userInfo.put("nom", utilisateur.getNom());
            userInfo.put("role", utilisateur.getRole().name());
            userInfo.put("actif", utilisateur.getActif());

            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            throw GestionException.unauthorized("Token invalide");
        }
    }

    private String generateToken(Utilisateur utilisateur) {
        return Jwts.builder()
                .setSubject(utilisateur.getEmail())
                .claim("role", utilisateur.getRole().name())
                .claim("id", utilisateur.getId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24 heures
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
}
