package com.caveo.backend.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LignePanierDto {

    @NotNull(message = "Le produit est obligatoire")
    private Integer produitId;

    @NotNull(message = "L'unité de conditionnement est obligatoire")
    private Integer uniteConditionnementId;

    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 1, message = "La quantité doit être supérieure à 0")
    private Integer quantite;
}
