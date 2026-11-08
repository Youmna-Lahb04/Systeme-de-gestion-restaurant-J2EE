package ma.ensaa.jee.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import ma.ensaa.jee.dao.ReservationDAO;
import ma.ensaa.jee.dao.TableDAO;
import ma.ensaa.jee.dao.UtilisateurDAO;
import ma.ensaa.jee.entity.TableRestaurant;
import ma.ensaa.jee.entity.Utilisateur;
import ma.ensaa.jee.entity.enums.Role;
import ma.ensaa.jee.entity.enums.StatutReservation;
import ma.ensaa.jee.util.JPAUtil;
import ma.ensaa.jee.entity.Reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ReservationService {

    private final ReservationDAO reservationDao;
    private final TableDAO tableDAO;
    private final UtilisateurDAO utilisateurDAO;
    private final HistoriqueStatutService historiqueStatutService;
    private final AuthorizationService authorizationService;

    public ReservationService(){
        this.reservationDao = new ReservationDAO();
        this.tableDAO = new TableDAO();
        this.utilisateurDAO = new UtilisateurDAO();
        this.historiqueStatutService = new HistoriqueStatutService();
        this.authorizationService = new AuthorizationService();
    }

    public Reservation creerReservation(Reservation reservation, Long utilisateurConnecteId){
        //verifier les params
        if(reservation == null || utilisateurConnecteId == null){
            throw new IllegalArgumentException("Paramètres obligatoires");
        }
        //verifier les infos de la reser
        validerDonneesReservation(reservation);
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction et = em.getTransaction();
        try{
            et.begin();
            //recuperer le client et verifier s'il est actif
            Utilisateur client = utilisateurDAO.trouverParId(em, utilisateurConnecteId);
            if (client == null || !client.isActif()) {
                throw new IllegalArgumentException("Utilisateur inexistant ou inactif.");
            }
            //recuperer la table et verifier sa capacite et sa dsponibilite
            TableRestaurant table = tableDAO.trouverParId(em, reservation.getTableRestaurant().getId());
            if (table == null) {
                throw new IllegalArgumentException("La table sélectionnée n'existe pas.");
            }
            if (table.getCapacite() < reservation.getNombrePersonnes()) {
                throw new IllegalArgumentException("La capacité de la table (" + table.getCapacite() + " personnes) est insuffisante pour " + reservation.getNombrePersonnes() + " personnes.");
            }
            verifierDisponibiliteTable(em, table.getId(), reservation.getDateReservation(), reservation.getHeureDebut(), reservation.getHeureFin(), null);
            //initialiser la reservation et l'ajouter
            reservation.setClient(client);
            reservation.setTableRestaurant(table);
            reservation.setStatutReservation(StatutReservation.EN_ATTENTE);
            reservationDao.ajouterReservation(em, reservation);

            //cote historique
            historiqueStatutService.enregistrerChangementStatut(em, reservation, null, utilisateurConnecteId);
            et.commit();
            return reservation;
        } catch (RuntimeException e) {
            if (et.isActive()) et.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    //methode prive pour verifier les infos des champs,la date et les heures,nbr perso
    private void validerDonneesReservation(Reservation reservation) {
        if (reservation.getDateReservation() == null || reservation.getHeureDebut() == null
                || reservation.getHeureFin() == null || reservation.getNombrePersonnes() == null
                || reservation.getTableRestaurant() == null || reservation.getTableRestaurant().getId() == null) {
            throw new IllegalArgumentException("Informations de réservation incomplètes.");
        }
        if (reservation.getDateReservation().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La date de réservation ne peut pas être dans le passé.");
        }
        if (reservation.getDateReservation().isEqual(LocalDate.now()) && reservation.getHeureDebut().isBefore(LocalTime.now())) {
            throw new IllegalArgumentException("L'heure de début de la réservation est déjà passée.");
        }
        if (!reservation.getHeureDebut().isBefore(reservation.getHeureFin())) {
            throw new IllegalArgumentException("L'heure de début doit être antérieure à l'heure de fin.");
        }
        if (reservation.getNombrePersonnes() <= 0) {
            throw new IllegalArgumentException("Le nombre de personnes doit être supérieur à 0.");
        }
    }

    //verifiee le creneau horaire de la reservation
    //reservationIdAExclure pour préciser quelle cas si creation il reçoit null
    //si modification on ignore la reservation elle meme pour ne pas empecher les modifs
    private void verifierDisponibiliteTable(EntityManager em, Long tableId, LocalDate date, LocalTime debut, LocalTime fin, Long reservationIdAExclure) {
        List<Reservation> existantes = reservationDao.trouverParTableEtDate(em, tableId, date);
        for (Reservation r : existantes) {
            // Ignorer la réservation en cours de modification
            if (reservationIdAExclure != null && r.getId().equals(reservationIdAExclure)) {
                continue;
            }
            // Condition de chevauchement des heurs
            boolean chevauchement = debut.isBefore(r.getHeureFin()) && fin.isAfter(r.getHeureDebut());
            if (chevauchement) {
                throw new IllegalStateException("La table est déjà réservée sur ce créneau horaire (" + r.getHeureDebut() + " - " + r.getHeureFin() + ").");
            }
        }
    }

    public Reservation modifierReservation(Reservation reservation, Long utilisateurConnecteId){
        if (reservation == null || reservation.getId() == null) {
            throw new IllegalArgumentException("La réservation et son identifiant sont obligatoires.");
        }
        //verifier le propritaire de la reservation et la validite infos
        authorizationService.verifierProprietaireReservation(utilisateurConnecteId, reservation.getId());
        validerDonneesReservation(reservation);

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction et = em.getTransaction();
        try {
            et.begin();
            //recuperer la reser
            Reservation existante = reservationDao.trouverParId(em, reservation.getId());
            if (existante == null) {
                throw new IllegalArgumentException("Réservation introuvable.");
            }
            if (existante.getStatutReservation() != StatutReservation.EN_ATTENTE) {
                throw new IllegalStateException("Seule une réservation en attente peut être modifiée.");
            }
            //recuperer la table
            TableRestaurant table = tableDAO.trouverParId(em, reservation.getTableRestaurant().getId());
            if (table == null) {
                throw new IllegalArgumentException("La table sélectionnée n'existe pas.");
            }
            if (table.getCapacite() < reservation.getNombrePersonnes()) {
                throw new IllegalArgumentException("Capacité de la table insuffisante.");
            }
            // Vérifier chevauchement en exclue cette réservation(sonId : existante.getId())
            verifierDisponibiliteTable(em, table.getId(), reservation.getDateReservation(), reservation.getHeureDebut(), reservation.getHeureFin(), existante.getId());

            existante.setDateReservation(reservation.getDateReservation());
            existante.setHeureDebut(reservation.getHeureDebut());
            existante.setHeureFin(reservation.getHeureFin());
            existante.setNombrePersonnes(reservation.getNombrePersonnes());
            existante.setTableRestaurant(table);
            et.commit();
            return existante;
        } catch (RuntimeException e) {
            if (et.isActive()) et.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void annulerReservation(Long reservationId, Long utilisateurConnecteId){
        authorizationService.verifierProprietaireReservation(utilisateurConnecteId, reservationId);
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction et = em.getTransaction();
        try {
            et.begin();
            Reservation reservation = reservationDao.trouverParId(em, reservationId);
            if (reservation == null) {
                throw new IllegalArgumentException("Réservation introuvable.");
            }
            //verifier que la reservation n'est pas annulle, termine,refusee
            if (reservation.getStatutReservation() == StatutReservation.ANNULEE ||
                    reservation.getStatutReservation() == StatutReservation.REFUSEE ||
                    reservation.getStatutReservation() == StatutReservation.TERMINEE) {
                throw new IllegalStateException("Impossible d'annuler une réservation déjà " + reservation.getStatutReservation());
            }

            StatutReservation ancienStatut = reservation.getStatutReservation();
            reservationDao.modifierStatut(em, reservation, StatutReservation.ANNULEE);
            //cote historique
            historiqueStatutService.enregistrerChangementStatut(em, reservation, ancienStatut, utilisateurConnecteId);
            et.commit();
        } catch (RuntimeException e) {
            if (et.isActive()) et.rollback();
            throw e;
        } finally {
            em.close();
        }

    }

    public Reservation getReservationById(Long reservationId, Long utilisateurConnecteId){
        authorizationService.verifierProprietaireReservation(utilisateurConnecteId, reservationId);
        return reservationDao.trouverParId(reservationId);
    }

    public List<Reservation> getMesReservations(Long utilisateurConnecteId){
        authorizationService.verifierUtilisateurConnecte(utilisateurConnecteId);
        return reservationDao.trouverParClient(utilisateurConnecteId);
    }

    public List<Reservation> getAllReservations(Long utilisateurConnecteId){
        authorizationService.verifierRole(utilisateurConnecteId, Role.ADMIN, Role.RECEPTIONNISTE);
        return reservationDao.trouverToutes();
    }

    public void confirmerReservation(Long reservationId, Long utilisateurConnecteId){
        authorizationService.verifierRole(utilisateurConnecteId, Role.ADMIN, Role.RECEPTIONNISTE);
        changerStatutReservation(reservationId, utilisateurConnecteId, StatutReservation.CONFIRMEE, StatutReservation.EN_ATTENTE);
    }

    public void refuserReservation(Long reservationId, Long utilisateurConnecteId){
        authorizationService.verifierRole(utilisateurConnecteId, Role.ADMIN, Role.RECEPTIONNISTE);
        changerStatutReservation(reservationId, utilisateurConnecteId, StatutReservation.REFUSEE, StatutReservation.EN_ATTENTE);
    }

    //methode privee de changement de statut
    private void changerStatutReservation(Long reservationId, Long utilisateurId, StatutReservation nouveauStatut, StatutReservation statutRequis) {
        if (reservationId == null) {
            throw new IllegalArgumentException("L'identifiant de la réservation est obligatoire.");
        }
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction et = em.getTransaction();
        try {
            et.begin();
            Reservation reservation = reservationDao.trouverParId(em, reservationId);
            if (reservation == null) {
                throw new IllegalArgumentException("Réservation introuvable.");
            }
            //verifier le statut actuelle si il donne le droit de mdodifier le statut
            if (statutRequis != null && reservation.getStatutReservation() != statutRequis) {
                throw new IllegalStateException("L'opération est impossible. La réservation doit être au statut : " + statutRequis);
            }

            StatutReservation ancienStatut = reservation.getStatutReservation();
            reservationDao.modifierStatut(em, reservation, nouveauStatut);
            //cote historique
            historiqueStatutService.enregistrerChangementStatut(em, reservation, ancienStatut, utilisateurId);
            et.commit();
        } catch (RuntimeException e) {
            if (et.isActive()) et.rollback();
            throw e;
        } finally {
            em.close();
        }
    }


}
