package com.caveo.backend.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "lignes_commande_client")
public class LigneCommandeClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commande_client_id", nullable = false)
    @JsonIgnoreProperties({"lignes", "hibernateLazyInitializer", "handler"})
    private CommandeClient commandeClient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit_id", nullable = false)
    @NotNull(message = "Le produit est obligatoire")
    @JsonIgnoreProperties({"conditionnements", "fournisseurs", "hibernateLazyInitializer", "handler"})
    private Produit produit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unite_conditionnement_id", nullable = false)
    @NotNull(message = "L'unité de conditionnement est obligatoire")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private UniteConditionnement uniteConditionnement;

    @Column(nullable = false)
    @NotNull(message = "La quantité est obligatoire")
    private Integer quantite;

    @Column(name = "prix_unitaire", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Le prix unitaire est obligatoire")
    private BigDecimal prixUnitaire;

    @Column(name = "prix_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal prixTotal;
}
