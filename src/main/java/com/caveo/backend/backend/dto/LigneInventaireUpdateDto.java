package com.caveo.backend.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LigneInventaireUpdateDto {

    @NotNull(message = "La quantité comptée est obligatoire")
    private Integer quantiteComptee;

    private String notes;
}
