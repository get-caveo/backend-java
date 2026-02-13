package com.caveo.backend.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "commandes_client")
public class CommandeClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String numero;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_commande", nullable = false,
            columnDefinition = "ENUM('EN_ATTENTE', 'CONFIRMEE', 'EN_PREPARATION', 'EXPEDIEE', 'LIVREE', 'ANNULEE')")
    private StatutCommandeClient statutCommande = StatutCommandeClient.EN_ATTENTE;

    @Column(name = "date_commande")
    private LocalDateTime dateCommande;

    @Column(name = "date_expedition")
    private LocalDateTime dateExpedition;

    @Column(name = "date_livraison")
    private LocalDateTime dateLivraison;

    @Column(name = "sous_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal sousTotal = BigDecimal.ZERO;

    @Column(name = "frais_livraison", precision = 10, scale = 2)
    private BigDecimal fraisLivraison = BigDecimal.ZERO;

    @Column(name = "montant_taxes", precision = 10, scale = 2)
    private BigDecimal montantTaxes = BigDecimal.ZERO;

    @Column(name = "montant_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantTotal = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "cree_le", updatable = false)
    private LocalDateTime creeLe;

    @Column(name = "modifie_le")
    private LocalDateTime modifieLe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    @JsonIgnoreProperties({"password", "adresses", "hibernateLazyInitializer", "handler"})
    private Utilisateur client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adresse_livraison_id")
    @JsonIgnoreProperties({"utilisateur", "hibernateLazyInitializer", "handler"})
    private Adresse adresseLivraison;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adresse_facturation_id")
    @JsonIgnoreProperties({"utilisateur", "hibernateLazyInitializer", "handler"})
    private Adresse adresseFacturation;

    @OneToMany(mappedBy = "commandeClient", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("commandeClient")
    private List<LigneCommandeClient> lignes = new ArrayList<>();

    @PreUpdate
    protected void onUpdate() {
        modifieLe = LocalDateTime.now();
    }
}
