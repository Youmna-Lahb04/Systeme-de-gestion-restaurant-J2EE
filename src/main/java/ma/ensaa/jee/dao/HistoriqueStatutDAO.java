
package ma.ensaa.jee.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import ma.ensaa.jee.entity.HistoriqueStatut;
import ma.ensaa.jee.util.JPAUtil;

import java.util.List;

public class HistoriqueStatutDAO {

    // Récupérer l'usine JPA
    private final EntityManagerFactory emf =
            JPAUtil.getEntityManagerFactory();

    // ADD
    // Utilise l'EntityManager fourni par le Service.
    // La transaction est gérée par le Service.
    public void ajouterHistorique(
            EntityManager em,
            HistoriqueStatut historique) {

        if (em == null || historique == null) {
            throw new IllegalArgumentException(
                    "L'EntityManager et l'historique sont obligatoires."
            );
        }

        em.persist(historique);
    }

    // GET-ALL
    public List<HistoriqueStatut> trouverToutes() {

        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                    "SELECT h FROM HistoriqueStatut h " +
                            "ORDER BY h.dateModification DESC, h.id DESC",
                    HistoriqueStatut.class
            ).getResultList();

        } finally {
            em.close();
        }
    }

    // GET-BY-ID
    public HistoriqueStatut trouverParId(Long id) {

        EntityManager em = emf.createEntityManager();

        try {
            return em.find(HistoriqueStatut.class, id);

        } finally {
            em.close();
        }
    }

    // GET-BY-RESERVATION
    public List<HistoriqueStatut> trouverParReservation(
            Long reservationId) {

        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                            "SELECT h FROM HistoriqueStatut h " +
                                    "WHERE h.reservation.id = :reservationId " +
                                    "ORDER BY h.dateModification ASC, h.id ASC",
                            HistoriqueStatut.class
                    )
                    .setParameter("reservationId", reservationId)
                    .getResultList();

        } finally {
            em.close();
        }
    }
}
