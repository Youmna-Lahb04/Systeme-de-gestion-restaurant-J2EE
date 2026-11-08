
package ma.ensaa.jee.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import ma.ensaa.jee.entity.Reservation;
import ma.ensaa.jee.entity.enums.StatutReservation;
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
//ADD partager em avec le service
public void ajouterReservation(
        EntityManager em,
        Reservation reservation) {

    if (em == null || reservation == null) {
        throw new IllegalArgumentException(
                "L'EntityManager et la réservation sont obligatoires."
        );
    }

    em.persist(reservation);
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
//partage de em lors de update
public Reservation trouverParId(
        EntityManager em,
        Long id) {

    if (em == null || id == null) {
        throw new IllegalArgumentException(
                "L'EntityManager et l'identifiant sont obligatoires."
        );
    }

    return em.find(Reservation.class, id);
}
    public void modifierStatut(
            EntityManager em,
            Reservation reservation,
            StatutReservation nouveauStatut) {

        if (em == null || reservation == null ||
                nouveauStatut == null) {

            throw new IllegalArgumentException(
                    "Les paramètres sont obligatoires."
            );
        }

        reservation.setStatutReservation(nouveauStatut);
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
//delete partage de em

    public void supprimerReservation(EntityManager em, Long id) {

        if (em == null || id == null) {
            throw new IllegalArgumentException(
                    "L'EntityManager et l'identifiant sont obligatoires."
            );
        }

        Reservation reservation =
                em.find(Reservation.class, id);

        if (reservation == null) {
            throw new IllegalArgumentException(
                    "La réservation n'existe pas."
            );
        }

        em.remove(reservation);
    }

    // GET-BY-ID
    public Reservation trouverParId(Long id) {

        EntityManager em = emf.createEntityManager();

        try {
            //return em.find(Reservation.class, id);
            return em.createQuery(
                            "SELECT r FROM Reservation r " +
                                    "LEFT JOIN FETCH r.client " +
                                    "LEFT JOIN FETCH r.tableRestaurant " +
                                    "WHERE r.id = :id",
                            Reservation.class
                    )
                    .setParameter("id", id)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

        } finally {
            em.close();
        }
    }

    // GET-ALL
    public List<Reservation> trouverToutes() {

        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                    "SELECT r FROM Reservation r " +
                            "LEFT JOIN FETCH r.client " +
                            "LEFT JOIN FETCH r.tableRestaurant " +
                            "ORDER BY r.id DESC",
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
                                    "LEFT JOIN FETCH r.tableRestaurant " +
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
                                    "LEFT JOIN FETCH r.client " +
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

    // GET-BY-TABLE-ET-DATE et statut en_attente ou connfirmée avec em partagé
    public List<Reservation> trouverParTableEtDate(EntityManager em, Long tableId, LocalDate date) {
        if (em == null || tableId == null || date == null) {
            throw new IllegalArgumentException("Paramètres obligatoires");
        }
        return em.createQuery(
                        "SELECT r FROM Reservation r " +
                                "WHERE r.tableRestaurant.id = :tableId " +
                                "AND r.dateReservation = :date " +
                                "AND r.statutReservation NOT IN (:annulee, :refusee)",
                        Reservation.class
                )
                .setParameter("tableId", tableId)
                .setParameter("date", date)
                .setParameter("annulee", StatutReservation.ANNULEE)
                .setParameter("refusee", StatutReservation.REFUSEE)
                .getResultList();
    }
}
