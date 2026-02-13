package com.caveo.backend.backend.dto;

import com.caveo.backend.backend.model.MethodePaiement;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaiementDto {

    @NotNull(message = "La méthode de paiement est obligatoire")
    private MethodePaiement methodePaiement;

    private String referenceTransaction;
    private String detailsPaiement;
}
