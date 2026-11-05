package ma.ensaa.jee.service;

import ma.ensaa.jee.entity.HistoriqueStatut;
import ma.ensaa.jee.entity.Reservation;

import java.util.List;

public class HistoriqueStatutService {
    public void enregistrerChangementStatut(
            Reservation reservation,
            Long utilisateurConnecteId
    ){};

    public List<HistoriqueStatut> getHistoriqueReservation(
            Long reservationId,
            Long utilisateurConnecteId
    ){
        return null;
    };
}
