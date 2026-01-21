package com.caveo.backend.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "domaines")
public class Domaine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255)
    @NotBlank(message = "Le nom du domaine ne peut pas être vide")
    private String nom;

    @Column(length = 100)
    private String region;

    @Column(length = 100)
    private String appellation;

    @Column(name = "surface_vignoble_ha", precision = 8, scale = 2)
    private BigDecimal surfaceVignobleHa;

    @Column(name = "type_sol", length = 255)
    private String typeSol;

    @Column(columnDefinition = "TEXT")
    private String cepages;

    @Column(length = 255)
    private String vigneron;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "site_web", length = 255)
    private String siteWeb;

    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(nullable = false)
    private Boolean actif = true;

    @CreationTimestamp
    @Column(name = "cree_le", updatable = false)
    private LocalDateTime creeLe;
}
