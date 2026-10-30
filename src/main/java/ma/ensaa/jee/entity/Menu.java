package ma.ensaa.jee.entity;
import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "menus")
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom du menu est obligatoire.")
    @Column(name = "nom", nullable = false)
    private String nom;

    @Size(max = 1000, message = "La description ne doit pas dépasser 1000 caractères.")
    @Column(name = "description", length = 1000)
    private String description;

    @OneToMany(mappedBy = "menu", fetch = FetchType.LAZY)
    private Set<Plat> plats = new HashSet<>();

    public Menu() {}

    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Plat> getPlats() {
        return plats;
    }

}
