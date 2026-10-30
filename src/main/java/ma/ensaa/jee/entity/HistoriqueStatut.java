package ma.ensaa.jee.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import ma.ensaa.jee.entity.enums.StatutReservation;

import java.time.LocalDateTime;

@Entity
@Table(name = "historique_statuts")
public class HistoriqueStatut {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(name = "ancien_statut")
    private StatutReservation ancienStatut;
    @NotNull(message = "Le nouveau statut est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "nouveau_statut", nullable = false)
    private StatutReservation nouveauStatut;
    @NotNull
    @Column(name = "date_modification", nullable = false, updatable = false)
    private LocalDateTime dateModification;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modifie_par_id")
    private Utilisateur modifiePar;
    public HistoriqueStatut() {
    }
    @PrePersist
    protected void onCreate() {
        if (dateModification == null) {
            dateModification = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public StatutReservation getAncienStatut() {
        return ancienStatut;
    }

    public StatutReservation getNouveauStatut() {
        return nouveauStatut;
    }

    public LocalDateTime getDateModification() {
        return dateModification;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public Utilisateur getModifiePar() {
        return modifiePar;
    }

    // Setters
    public void setAncienStatut(StatutReservation ancienStatut) {
        this.ancienStatut = ancienStatut;
    }

    public void setNouveauStatut(StatutReservation nouveauStatut) {
        this.nouveauStatut = nouveauStatut;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public void setModifiePar(Utilisateur modifiePar) {
        this.modifiePar = modifiePar;
    }
}
