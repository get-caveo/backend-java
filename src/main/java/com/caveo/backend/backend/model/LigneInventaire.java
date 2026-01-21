package com.caveo.backend.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "lignes_inventaire")
public class LigneInventaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "quantite_attendue", nullable = false)
    private Integer quantiteAttendue;

    @Column(name = "quantite_comptee")
    private Integer quantiteComptee;

    @Column
    private Integer difference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('EN_ATTENTE', 'COMPTEE', 'VALIDEE')")
    private StatutLigneInventaire statut = StatutLigneInventaire.EN_ATTENTE;

    @Column(length = 255)
    private String notes;

    @Column(name = "compte_le")
    private LocalDateTime compteLe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventaire_id", nullable = false)
    @JsonIgnoreProperties({"lignes", "hibernateLazyInitializer", "handler"})
    private Inventaire inventaire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit_id", nullable = false)
    @JsonIgnoreProperties({"conditionnements", "fournisseurs", "hibernateLazyInitializer", "handler"})
    private Produit produit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unite_conditionnement_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private UniteConditionnement uniteConditionnement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compte_par")
    @JsonIgnoreProperties({"password", "adresses", "hibernateLazyInitializer", "handler"})
    private Utilisateur comptePar;
}
