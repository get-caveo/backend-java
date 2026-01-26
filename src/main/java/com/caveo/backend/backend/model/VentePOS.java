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
@Table(name = "ventes_pos")
public class VentePOS {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String numero;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('BROUILLON', 'PAYEE', 'ANNULEE')")
    private StatutVentePOS statut = StatutVentePOS.BROUILLON;

    @Column(name = "date_vente")
    private LocalDateTime dateVente;

    @Column(name = "montant_sous_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantSousTotal = BigDecimal.ZERO;

    @Column(name = "montant_remise", precision = 10, scale = 2)
    private BigDecimal montantRemise = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_remise", columnDefinition = "ENUM('POURCENTAGE', 'MONTANT')")
    private TypeRemise typeRemise;

    @Column(name = "valeur_remise", precision = 10, scale = 2)
    private BigDecimal valeurRemise;

    @Column(name = "montant_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantTotal = BigDecimal.ZERO;

    @Column(name = "montant_paye", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantPaye = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "cree_le", updatable = false)
    private LocalDateTime creeLe;

    @Column(name = "modifie_le")
    private LocalDateTime modifieLe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    @JsonIgnoreProperties({"password", "adresses", "hibernateLazyInitializer", "handler"})
    private Utilisateur utilisateur;

    @OneToMany(mappedBy = "ventePOS", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("ventePOS")
    private List<LigneVentePOS> lignes = new ArrayList<>();

    @OneToMany(mappedBy = "ventePOS", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("ventePOS")
    private List<PaiementVentePOS> paiements = new ArrayList<>();

    @PreUpdate
    protected void onUpdate() {
        modifieLe = LocalDateTime.now();
    }
}
