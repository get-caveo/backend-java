package com.caveo.backend.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "paiements_vente_pos")
public class PaiementVentePOS {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vente_pos_id", nullable = false)
    @JsonIgnoreProperties({"lignes", "paiements", "hibernateLazyInitializer", "handler"})
    private VentePOS ventePOS;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode_paiement", nullable = false, columnDefinition = "ENUM('ESPECES', 'CARTE', 'CHEQUE', 'AVOIR')")
    @NotNull(message = "Le mode de paiement est obligatoire")
    private ModePaiement modePaiement;

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Le montant est obligatoire")
    private BigDecimal montant;

    @Column(length = 100)
    private String reference;

    @Column(name = "montant_rendu", precision = 10, scale = 2)
    private BigDecimal montantRendu;

    @CreationTimestamp
    @Column(name = "cree_le", updatable = false)
    private LocalDateTime creeLe;
}
