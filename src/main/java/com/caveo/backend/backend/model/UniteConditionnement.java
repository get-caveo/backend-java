package com.caveo.backend.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "unites_conditionnement")
public class UniteConditionnement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Le nom ne peut pas être vide")
    private String nom;

    @Column(name = "nom_court", nullable = false, length = 20)
    @NotBlank(message = "Le nom court ne peut pas être vide")
    private String nomCourt;

    @Column(name = "quantite_unite_base", nullable = false)
    @NotNull(message = "La quantité unité de base ne peut pas être vide")
    private Integer quantiteUniteBase;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "dimensions_cm", length = 100)
    private String dimensionsCm;

    @Column(name = "poids_kg", precision = 6, scale = 2)
    private BigDecimal poidsKg;

    @Column(name = "volume_ml", nullable = false)
    @NotNull(message = "Le volume en ml ne peut pas être vide")
    private Integer volumeMl;

    @Column(name = "est_vendable", nullable = false)
    private Boolean estVendable = true;

    @Column(name = "est_unite_base", nullable = false)
    private Boolean estUniteBase = false;

    @Column(name = "ordre_tri")
    private Integer ordreTri = 0;

    @Column(nullable = false)
    private Boolean actif = true;

    @CreationTimestamp
    @Column(name = "cree_le", updatable = false)
    private LocalDateTime creeLe;
}
