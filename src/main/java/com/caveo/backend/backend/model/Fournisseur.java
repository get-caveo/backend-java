package com.caveo.backend.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "fournisseurs")
public class Fournisseur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255)
    @NotBlank(message = "Le nom du fournisseur ne peut pas être vide")
    private String nom;

    @Column(name = "personne_contact", length = 100)
    private String personneContact;

    @Column(length = 255)
    @Email(message = "L'email doit être valide")
    private String email;

    @Column(length = 20)
    private String telephone;

    @Column(length = 255)
    private String adresse;

    @Column(name = "conditions_paiement", length = 100)
    private String conditionsPaiement;

    @Column(name = "certification_bio", nullable = false)
    private Boolean certificationBio = false;

    @Column(name = "certification_aoc", nullable = false)
    private Boolean certificationAoc = false;

    @Column(name = "certifications_autres", columnDefinition = "TEXT")
    private String certificationsAutres;
}
