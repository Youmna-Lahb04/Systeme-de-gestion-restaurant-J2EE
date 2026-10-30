package ma.ensaa.jee.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import ma.ensaa.jee.entity.enums.StatutReservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name="reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "date_reservation",nullable = false)
    private LocalDate dateReservation;

    @NotNull
    @Column(name = "heure_debut",nullable = false)
    private LocalTime heureDebut;

    @NotNull
    @Column(name = "heure_fin",nullable = false)
    private LocalTime heureFin;

    @NotNull
    @Positive
    @Column(name = "nombre_personnes",nullable = false)
    private Integer nombrePersonnes;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "statut_reservation",nullable = false)
    private StatutReservation statutReservation;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="client_id", nullable = false)
    private Utilisateur client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="table_id", nullable = false)
    private TableRestaurant tableRestaurant;

    @NotNull
    @Column(name = "date_creation",nullable = false, updatable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();


    public Reservation() {
    }
    @PrePersist
    protected void onCreate() {
        if (dateCreation == null) {
            dateCreation = LocalDateTime.now();
        }
    }
    public Long getId() {
        return id;
    }

    public LocalDate getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(LocalDate dateReservation) {
        this.dateReservation = dateReservation;
    }

    public LocalTime getHeureDebut() {
        return heureDebut;
    }

    public void setHeureDebut(LocalTime heureDebut) {
        this.heureDebut = heureDebut;
    }

    public LocalTime getHeureFin() {
        return heureFin;
    }

    public void setHeureFin(LocalTime heureFin) {
        this.heureFin = heureFin;
    }

    public Integer getNombrePersonnes() {
        return nombrePersonnes;
    }

    public void setNombrePersonnes(Integer nombrePersonnes) {
        this.nombrePersonnes = nombrePersonnes;
    }

    public StatutReservation getStatutReservation() {
        return statutReservation;
    }

    public void setStatutReservation(StatutReservation statut) {
        this.statutReservation = statut;
    }

    public Utilisateur getClient() {
        return client;
    }

    public void setClient(Utilisateur client) {
        this.client = client;
    }

    public TableRestaurant getTableRestaurant() {
        return tableRestaurant;
    }

    public void setTableRestaurant(TableRestaurant tableRestaurant) {
        this.tableRestaurant = tableRestaurant;
    }
    public LocalDateTime getDateCreation() {
        return dateCreation;
    }




}
