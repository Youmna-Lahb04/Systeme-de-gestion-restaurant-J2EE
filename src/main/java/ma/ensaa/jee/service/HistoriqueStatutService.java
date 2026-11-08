package ma.ensaa.jee.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import ma.ensaa.jee.dao.HistoriqueStatutDAO;
import ma.ensaa.jee.dao.UtilisateurDAO;
import ma.ensaa.jee.entity.HistoriqueStatut;
import ma.ensaa.jee.entity.Reservation;
import ma.ensaa.jee.entity.Utilisateur;
import ma.ensaa.jee.entity.enums.StatutReservation;
import ma.ensaa.jee.util.JPAUtil;

import java.util.List;

public class HistoriqueStatutService {

    private final HistoriqueStatutDAO historiqueDAO;
    private final UtilisateurDAO utilisateurDAO;
    private final AuthorizationService authorizationService;

    public HistoriqueStatutService(){
        this.utilisateurDAO = new UtilisateurDAO();
        this.historiqueDAO = new HistoriqueStatutDAO();
        this.authorizationService = new AuthorizationService();
    }

    public void enregistrerChangementStatut(EntityManager em, Reservation reservation, StatutReservation ancienStatut, Long utilisateurConnecteId){
        if (em == null || reservation == null || utilisateurConnecteId == null) {
            throw new IllegalArgumentException("Paramètres obligatoires pour enregistrer l'historique.");
        }
        Utilisateur modificateur = utilisateurDAO.trouverParId(em, utilisateurConnecteId);
        if (modificateur == null) {
            throw new IllegalArgumentException("Utilisateur modificateur introuvable.");
        }
        HistoriqueStatut historique = new HistoriqueStatut();
        historique.setReservation(reservation);
        historique.setAncienStatut(ancienStatut);
        historique.setNouveauStatut(reservation.getStatutReservation());
        historique.setModifiePar(modificateur);
        historiqueDAO.ajouterHistorique(em, historique);

    }

    public void enregistrerChangementStatut(Reservation reservation, Long utilisateurConnecteId) {
        if (reservation == null || reservation.getId() == null || utilisateurConnecteId == null) {
            throw new IllegalArgumentException("Paramètres obligatoires.");
        }
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction et = em.getTransaction();
        try {
            et.begin();

            List<HistoriqueStatut> historiques = historiqueDAO.trouverParReservation(reservation.getId());
            StatutReservation ancienStatut = historiques.isEmpty() ? null : historiques.get(historiques.size() - 1).getNouveauStatut();

            enregistrerChangementStatut(em, reservation, ancienStatut, utilisateurConnecteId);
            et.commit();
        } catch (RuntimeException e) {
            if (et.isActive()) et.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public List<HistoriqueStatut> getHistoriqueReservation(Long reservationId, Long utilisateurConnecteId){
        if (reservationId == null || utilisateurConnecteId == null) {
            throw new IllegalArgumentException("Identifiants obligatoires.");
        }

        authorizationService.verifierProprietaireReservation(utilisateurConnecteId, reservationId);
        return historiqueDAO.trouverParReservation(reservationId);
    }
}
