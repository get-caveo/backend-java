package com.caveo.backend.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "paiements")
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commande_client_id", nullable = false, unique = true)
    @JsonIgnoreProperties({"lignes", "hibernateLazyInitializer", "handler"})
    private CommandeClient commandeClient;

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Le montant est obligatoire")
    private BigDecimal montant;

    @Enumerated(EnumType.STRING)
    @Column(name = "methode_paiement", nullable = false,
            columnDefinition = "ENUM('CARTE', 'PAYPAL', 'VIREMENT')")
    @NotNull(message = "La méthode de paiement est obligatoire")
    private MethodePaiement methodePaiement;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_paiement", nullable = false,
            columnDefinition = "ENUM('EN_ATTENTE', 'COMPLET', 'ECHEC')")
    private StatutPaiement statutPaiement = StatutPaiement.EN_ATTENTE;

    @Column(name = "reference_transaction", length = 100)
    private String referenceTransaction;

    @Column(name = "details_paiement", length = 255)
    private String detailsPaiement;

    @Column(name = "paye_le")
    private LocalDateTime payeLe;
}
