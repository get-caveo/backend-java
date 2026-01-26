package com.caveo.backend.backend.dto;

import jakarta.validation.constraints.Min;
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
public class LigneVentePOSDto {

    @NotNull(message = "Le produit est obligatoire")
    private Integer produitId;

    @NotNull(message = "L'unité de conditionnement est obligatoire")
    private Integer uniteConditionnementId;

    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 1, message = "La quantité doit être au moins 1")
    private Integer quantite;

    private BigDecimal prixUnitaire;
}
