package com.caveo.backend.backend.model;

import com.caveo.backend.backend.view.UtilisateurView;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "adresses")
public class Adresse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(UtilisateurView.class)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('FACTURATION', 'LIVRAISON', 'ENTREPRISE')")
    @NotNull(message = "Le type d'adresse ne peut pas être vide")
    @JsonView(UtilisateurView.class)
    private TypeAdresse type;

    @Column(nullable = false, length = 255)
    @NotBlank(message = "La rue ne peut pas être vide")
    @JsonView(UtilisateurView.class)
    private String rue;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "La ville ne peut pas être vide")
    @JsonView(UtilisateurView.class)
    private String ville;

    @Column(name = "code_postal", nullable = false, length = 20)
    @NotBlank(message = "Le code postal ne peut pas être vide")
    @JsonView(UtilisateurView.class)
    private String codePostal;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Le pays ne peut pas être vide")
    @JsonView(UtilisateurView.class)
    private String pays;

    @Column(name = "par_defaut", nullable = false)
    @JsonView(UtilisateurView.class)
    private Boolean parDefaut = false;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

}
