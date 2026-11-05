package ma.ensaa.jee.service;

import ma.ensaa.jee.dao.ReservationDAO;
import ma.ensaa.jee.dao.UtilisateurDAO;
import ma.ensaa.jee.entity.Reservation;
import ma.ensaa.jee.entity.Utilisateur;
import ma.ensaa.jee.entity.enums.Role;
import ma.ensaa.jee.exception.AccesRefuseException;

public class AuthorizationService {

    private final UtilisateurDAO utilisateurDAO;
    private final ReservationDAO reservationDAO;
    public AuthorizationService() {
        this.utilisateurDAO = new UtilisateurDAO();
        this.reservationDAO = new ReservationDAO();
    }
    public Utilisateur chargerUtilisateurActif(Long utilisateurId){
        if (utilisateurId == null) {
            throw new AccesRefuseException(
                    "Vous devez être connecté pour effectuer cette opération."
            );
        }
        Utilisateur utilisateur =
                utilisateurDAO.rechercherUtilisateurId(utilisateurId);
        if (utilisateur == null) {
            throw new AccesRefuseException(
                    "Utilisateur introuvable."
            );
        }
        if (!utilisateur.isActif()) {
            throw new AccesRefuseException(
                    "Votre compte est désactivé."
            );
        }

        return utilisateur;
    }

    //verifier si user possede un role puis le recuperer
    public boolean aRole(Long utilisateurId, Role role) {

        if (role == null) {
            throw new IllegalArgumentException(
                    "Le rôle à vérifier est obligatoire."
            );
        }

        Utilisateur utilisateur = chargerUtilisateurActif(utilisateurId);

        return utilisateur.getRole() == role;
    }

    public boolean aUnDesRoles(Long utilisateurId, Role... roles) {

        if (roles == null || roles.length == 0) {
            throw new IllegalArgumentException(
                    "Au moins un rôle doit être fourni."
            );
        }

        Utilisateur utilisateur = chargerUtilisateurActif(utilisateurId);

        for (Role role : roles) {
            if (role != null && utilisateur.getRole() == role) {
                return true;
            }
        }

        return false;
    }
    public boolean estAdmin(Long utilisateurId) {

        return aRole(utilisateurId, Role.ADMIN);
    }
    public void verifierUtilisateurConnecte(Long utilisateurId) {

        chargerUtilisateurActif(utilisateurId);
    }

    public void verifierRole(Long utilisateurId, Role... roles) {

        if (!aUnDesRoles(utilisateurId, roles)) {
            throw new AccesRefuseException(
                    "Vous n'avez pas les permissions nécessaires pour cette opération."
            );
        }
    }

    public void verifierProprietaireReservation(
            Long utilisateurId,
            Long reservationId
    ) {

        // Vérifier que l'utilisateur existe et que son compte est actif
        Utilisateur utilisateur = chargerUtilisateurActif(utilisateurId);

        if (reservationId == null) {
            throw new IllegalArgumentException(
                    "L'identifiant de la réservation est obligatoire."
            );
        }
        Reservation reservation =
                reservationDAO.trouverParId(reservationId);

        if (reservation == null) {
            throw new IllegalArgumentException(
                    "Réservation introuvable."
            );
        }
        if (utilisateur.getRole() == Role.ADMIN
                || utilisateur.getRole() == Role.RECEPTIONNISTE) {
            return;
        }
        if (utilisateur.getRole() == Role.CLIENT
                && reservation.getClient() != null
                && utilisateurId.equals(reservation.getClient().getId())) {
            return;
        }

        throw new AccesRefuseException(
                "Vous ne pouvez pas accéder à cette réservation."
        );
    }



    }
