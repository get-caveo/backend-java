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
@Table(name = "stock_actuel", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"produit_id", "unite_conditionnement_id"})
})
public class StockActuel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer quantite = 0;

    @Column(name = "quantite_reservee", nullable = false)
    private Integer quantiteReservee = 0;

    @Column(name = "quantite_disponible", nullable = false)
    private Integer quantiteDisponible = 0;

    @Column(name = "quantite_unite_base", nullable = false)
    private Integer quantiteUniteBase = 0;

    @Column(name = "dernier_inventaire")
    private LocalDateTime dernierInventaire;

    @Column(name = "modifie_le")
    private LocalDateTime modifieLe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit_id", nullable = false)
    @JsonIgnoreProperties({"conditionnements", "fournisseurs", "hibernateLazyInitializer", "handler"})
    private Produit produit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unite_conditionnement_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private UniteConditionnement uniteConditionnement;
}
