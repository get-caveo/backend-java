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
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "mouvements_stock")
public class MouvementStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_mouvement", nullable = false, columnDefinition = "ENUM('ENTREE', 'SORTIE', 'AJUSTEMENT', 'INVENTAIRE')")
    @NotNull(message = "Le type de mouvement est obligatoire")
    private TypeMouvement typeMouvement;

    @Column(nullable = false)
    @NotNull(message = "La quantité est obligatoire")
    private Integer quantite;

    @Column(name = "quantite_unite_base", nullable = false)
    private Integer quantiteUniteBase;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_reference", nullable = false, columnDefinition = "ENUM('COMMANDE_FOURNISSEUR', 'COMMANDE_CLIENT', 'INVENTAIRE', 'MANUEL')")
    @NotNull(message = "Le type de référence est obligatoire")
    private TypeReference typeReference;

    @Column(name = "reference_id")
    private Integer referenceId;

    @Column(name = "prix_unitaire", precision = 10, scale = 2)
    private BigDecimal prixUnitaire;

    @Column(length = 255)
    private String raison;

    @Column(name = "numero_lot", length = 100)
    private String numeroLot;

    @Column(name = "date_peremption")
    private LocalDate datePeremption;

    @CreationTimestamp
    @Column(name = "cree_le", updatable = false)
    private LocalDateTime creeLe;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    @JsonIgnoreProperties({"password", "adresses", "hibernateLazyInitializer", "handler"})
    private Utilisateur utilisateur;


}
