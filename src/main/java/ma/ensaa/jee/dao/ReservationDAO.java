
package ma.ensaa.jee.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import ma.ensaa.jee.entity.Reservation;
import ma.ensaa.jee.util.JPAUtil;

import java.time.LocalDate;
import java.util.List;

public class ReservationDAO {

    // Récupérer l'usine JPA
    private final EntityManagerFactory emf =
            JPAUtil.getEntityManagerFactory();

    // ADD
    public void ajouterReservation(Reservation reservation) {

        EntityManager em = emf.createEntityManager();
        EntityTransaction et = em.getTransaction();

        try {
            et.begin();

            em.persist(reservation);

            et.commit();

        } catch (RuntimeException e) {
            if (et.isActive()) et.rollback();
            throw e;

        } finally {
            em.close();
        }
    }

    // UPDATE
    public Reservation modifierReservation(Reservation reservation) {

        if (reservation == null || reservation.getId() == null) {
            throw new IllegalArgumentException(
                    "La réservation et son identifiant sont obligatoires."
            );
        }

        EntityManager em = emf.createEntityManager();
        EntityTransaction et = em.getTransaction();

        try {
            et.begin();
            Reservation resultat =
                    em.find(Reservation.class, reservation.getId());

            if (resultat == null) {
                throw new IllegalArgumentException(
                        "Aucune réservation trouvée avec cet identifiant."
                );
            }
            resultat.setDateReservation(
                    reservation.getDateReservation()
            );
            resultat.setHeureDebut(reservation.getHeureDebut());
            resultat.setHeureFin(reservation.getHeureFin());
            resultat.setNombrePersonnes(
                    reservation.getNombrePersonnes()
            );
            resultat.setStatutReservation(
                    reservation.getStatutReservation()
            );
            resultat.setClient(reservation.getClient());
            resultat.setTableRestaurant(
                    reservation.getTableRestaurant()
            );

            et.commit();

            return resultat;

        } catch (RuntimeException e) {
            if (et.isActive()) et.rollback();
            throw e;

        } finally {
            em.close();
        }
    }

    // DELETE
    public void supprimerReservation(Long id) {

        EntityManager em = emf.createEntityManager();
        EntityTransaction et = em.getTransaction();

        try {
            et.begin();

            Reservation reservation =
                    em.find(Reservation.class, id);

            if (reservation == null) {
                throw new IllegalArgumentException(
                        "La réservation n'existe pas."
                );
            }

            em.remove(reservation);

            et.commit();

        } catch (RuntimeException e) {
            if (et.isActive()) et.rollback();
            throw e;

        } finally {
            em.close();
        }
    }

    // GET-BY-ID
    public Reservation trouverParId(Long id) {

        EntityManager em = emf.createEntityManager();

        try {
            return em.find(Reservation.class, id);

        } finally {
            em.close();
        }
    }

    // GET-ALL
    public List<Reservation> trouverToutes() {

        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                    "SELECT r FROM Reservation r ORDER BY r.id DESC",
                    Reservation.class
            ).getResultList();

        } finally {
            em.close();
        }
    }

    // GET-BY-CLIENT
    public List<Reservation> trouverParClient(Long clientId) {

        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                            "SELECT r FROM Reservation r " +
                                    "WHERE r.client.id = :clientId " +
                                    "ORDER BY r.dateReservation DESC",
                            Reservation.class
                    )
                    .setParameter("clientId", clientId)
                    .getResultList();

        } finally {
            em.close();
        }
    }

    // GET-BY-TABLE-ET-DATE
    public List<Reservation> trouverParTableEtDate(
            Long tableId, LocalDate date) {

        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                            "SELECT r FROM Reservation r " +
                                    "WHERE r.tableRestaurant.id = :tableId " +
                                    "AND r.dateReservation = :date " +
                                    "ORDER BY r.heureDebut",
                            Reservation.class
                    )
                    .setParameter("tableId", tableId)
                    .setParameter("date", date)
                    .getResultList();

        } finally {
            em.close();
        }
    }
}
