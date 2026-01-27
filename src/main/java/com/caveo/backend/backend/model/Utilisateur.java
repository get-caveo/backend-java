package com.caveo.backend.backend.model;
import com.caveo.backend.backend.security.Role;
import com.caveo.backend.backend.view.UtilisateurView;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "utilisateurs")
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(UtilisateurView.class)
    protected Integer id;

    @Column(nullable = false, unique = true, length = 255)
    @NotBlank(groups = { onCreation.class, onMiseAjour.class }, message = "L'email ne peut pas etre vide")
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}",
            groups = { onCreation.class, onMiseAjour.class })
    @JsonView(UtilisateurView.class)
    protected String email;

    @Column(name = "mot_de_passe", nullable = false, length = 255)
    @NotBlank(groups = { onCreation.class })
    protected String password;

    @Column(nullable = false, length = 100)
    @NotBlank(groups = { onCreation.class, onMiseAjour.class }, message = "Le prenom ne peut pas être vide" )
    @JsonView(UtilisateurView.class)
    protected String prenom;

    @Column(nullable = false, length = 100)
    @NotBlank(groups = { onCreation.class, onMiseAjour.class }, message = "Le nom ne peut pas être vide" )
    @JsonView(UtilisateurView.class)
    protected String nom;

    @Column(length = 20)
    @JsonView(UtilisateurView.class)
    protected String telephone;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('CLIENT', 'ADMIN', 'EMPLOYE')")
    @JsonView(UtilisateurView.class)
    protected Role role;

    @Column(nullable = false)
    @JsonView(UtilisateurView.class)
    protected Boolean actif = true;

    @CreationTimestamp
    @Column(name = "cree_le", updatable = false)
    @JsonView(UtilisateurView.class)
    protected LocalDateTime creeLe;

    @Column(name = "modifie_le")
    @JsonView(UtilisateurView.class)
    protected LocalDateTime modifieLe;

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonView(UtilisateurView.class)
    protected List<Adresse> adresses = new ArrayList<>();

    public interface onCreation {}
    public interface onMiseAjour {}

}
