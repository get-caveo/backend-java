package com.caveo.backend.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReceptionLigneDto {

    @NotNull(message = "L'id de la ligne est obligatoire")
    private Integer ligneId;

    @NotNull(message = "La quantité reçue est obligatoire")
    private Integer quantiteRecue;

    private String numeroLot;

    private LocalDate datePeremption;
}
