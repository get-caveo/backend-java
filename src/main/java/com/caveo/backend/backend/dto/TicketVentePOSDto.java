package com.caveo.backend.backend.dto;

import com.caveo.backend.backend.model.ModePaiement;
import com.caveo.backend.backend.model.TypeRemise;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketVentePOSDto {

    private String numero;
    private LocalDateTime dateVente;
    private String vendeur;

    private List<LigneTicketDto> lignes;

    private BigDecimal sousTotal;
    private TypeRemise typeRemise;
    private BigDecimal valeurRemise;
    private BigDecimal montantRemise;
    private BigDecimal total;

    private List<PaiementTicketDto> paiements;
    private BigDecimal montantPaye;
    private BigDecimal montantRendu;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LigneTicketDto {
        private String produitNom;
        private String conditionnement;
        private Integer quantite;
        private BigDecimal prixUnitaire;
        private BigDecimal prixTotal;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PaiementTicketDto {
        private ModePaiement modePaiement;
        private BigDecimal montant;
        private String reference;
    }
}
