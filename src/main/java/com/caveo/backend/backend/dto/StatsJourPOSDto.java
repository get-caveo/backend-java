package com.caveo.backend.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatsJourPOSDto {

    private Long nombreVentes;
    private BigDecimal totalVentes;
    private BigDecimal moyenneParVente;
    private Map<String, BigDecimal> totauxParModePaiement;
}
