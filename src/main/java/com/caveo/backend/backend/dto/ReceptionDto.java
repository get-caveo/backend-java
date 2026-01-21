package com.caveo.backend.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReceptionDto {

    @NotEmpty(message = "Au moins une ligne doit être réceptionnée")
    @Valid
    private List<ReceptionLigneDto> lignes;
}
