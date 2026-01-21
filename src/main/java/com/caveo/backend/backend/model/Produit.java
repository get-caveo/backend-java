package com.caveo.backend.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "produits")
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    @NotBlank(message = "Le SKU ne peut pas être vide")
    private String sku;

    @Column(nullable = false, length = 255)
    @NotBlank(message = "Le nom du produit ne peut pas être vide")
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "domaine_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Domaine domaine;

    @Column
    private Integer millesime;

    @Column(name = "degre_alcool", precision = 3, scale = 1)
    private BigDecimal degreAlcool;

    @Column(name = "code_barre", length = 50)
    private String codeBarre;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "notes_degustation", columnDefinition = "TEXT")
    private String notesDegustation;

    @Column(name = "temperature_service", length = 50)
    private String temperatureService;

    @Column(name = "conditions_conservation", columnDefinition = "TEXT")
    private String conditionsConservation;

    @Column(name = "seuil_stock_minimal")
    private Integer seuilStockMinimal = 5;

    @Column(name = "reappro_auto", nullable = false)
    private Boolean reapproAuto = true;

    @Column(nullable = false)
    private Boolean actif = true;

    @CreationTimestamp
    @Column(name = "cree_le", updatable = false)
    private LocalDateTime creeLe;

    @Column(name = "modifie_le")
    private LocalDateTime modifieLe;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categorie_id", nullable = false)
    @NotNull(message = "La catégorie est obligatoire")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Categorie categorie;

    @OneToMany(mappedBy = "produit", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("produit")
    private Set<ConditionnementProduit> conditionnements = new HashSet<>();

    @OneToMany(mappedBy = "produit", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("produit")
    private Set<FournisseurProduit> fournisseurs = new HashSet<>();
}
