package com.caveo.backend.backend.dto;

import com.caveo.backend.backend.model.ModePaiement;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaiementVentePOSDto {

    @NotNull(message = "Le mode de paiement est obligatoire")
    private ModePaiement modePaiement;

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    private BigDecimal montant;

    private String reference;

    private BigDecimal montantRecu;
}
