package ma.ensaa.jee.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
@Entity
@Table(name="tables")
public class TableRestaurant {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "Le numéro de table est obligatoire")
    @Positive
    @Column(nullable = false, unique = true)
    private Integer numero;
    @NotNull(message = "La capacité est obligatoire")
    @Positive(message = "La capacité doit être un nombre positif")
    @Column (nullable = false)
    private Integer capacite;

    public TableRestaurant() {}

    public Long getId() {
        return id;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Integer getCapacite() {
        return capacite;
    }
    public void setCapacite(Integer capacite) {
        this.capacite = capacite;
    }
}
