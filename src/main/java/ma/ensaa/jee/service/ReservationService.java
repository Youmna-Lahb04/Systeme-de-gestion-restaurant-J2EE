package ma.ensaa.jee.service;

import ma.ensaa.jee.entity.Reservation;

import java.util.List;

public class ReservationService {
    public Reservation creerReservation(
            Reservation reservation,
            Long utilisateurConnecteId
    ){
        return null;
    };

    public Reservation modifierReservation(
            Reservation reservation,
            Long utilisateurConnecteId
    ){
        return null;
    };

    public void annulerReservation(
            Long reservationId,
            Long utilisateurConnecteId
    ){};

    public Reservation getReservationById(
            Long reservationId,
            Long utilisateurConnecteId
    ){
        return null;
    };

    public List<Reservation> getMesReservations(
            Long utilisateurConnecteId
    ){
        return null;
    };

    public List<Reservation> getAllReservations(
            Long utilisateurConnecteId
    ){
        return null;
    };

    public void confirmerReservation(
            Long reservationId,
            Long utilisateurConnecteId
    ){};

    public void refuserReservation(
            Long reservationId,
            Long utilisateurConnecteId
    ){};
}
