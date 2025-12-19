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

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(UtilisateurView.class)
    protected Integer id;

    @Column(nullable = false, unique = true, length = 100)
    @NotBlank(groups = { onCreation.class, onMiseAjour.class }, message = "L'email ne peut pas etre vide")
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}",
            groups = { onCreation.class, onMiseAjour.class })
    @JsonView(UtilisateurView.class)
    protected String email;

    @Column(nullable = false)
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
    protected Role role;

    public interface onCreation {}
    public interface onMiseAjour {}

}
