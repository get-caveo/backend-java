package com.caveo.backend.backend.dto;

import com.caveo.backend.backend.model.TypeRemise;
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
public class RemiseDto {

    @NotNull(message = "Le type de remise est obligatoire")
    private TypeRemise typeRemise;

    @NotNull(message = "La valeur de remise est obligatoire")
    @Positive(message = "La valeur de remise doit être positive")
    private BigDecimal valeur;
}
