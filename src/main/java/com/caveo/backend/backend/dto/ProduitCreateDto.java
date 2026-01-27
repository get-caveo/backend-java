package com.caveo.backend.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProduitCreateDto {

    // ========== Données du produit ==========
    
    @NotBlank(message = "Le SKU ne peut pas être vide")
    private String sku;

    @NotBlank(message = "Le nom du produit ne peut pas être vide")
    private String nom;

    private String description;

    @NotNull(message = "La catégorie est obligatoire")
    private Integer categorieId;

    private Integer domaineId;

    private Integer millesime;

    private BigDecimal degreAlcool;

    private String codeBarre;

    private String imageUrl;

    private String notesDegustation;

    private String temperatureService;

    private String conditionsConservation;

    private Integer seuilStockMinimal = 5;

    private Boolean reapproAuto = true;

    // ========== Premier fournisseur obligatoire ==========
    
    @NotNull(message = "Le fournisseur est obligatoire")
    private Integer fournisseurId;

    private BigDecimal prixFournisseur;

    private Integer delaiApproJours = 0;
}
