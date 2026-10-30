package ma.ensaa.jee.dao;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import ma.ensaa.jee.entity.Utilisateur;
import ma.ensaa.jee.util.JPAUtil;

public class UtilisateurDAO {

    // Récupérer l'usine JPA
    private final EntityManagerFactory emf = JPAUtil.getEntityManagerFactory();

    // ADD
    public void ajouterUtilisateur(Utilisateur utilisateur) {
        if (utilisateur == null) {
            throw new IllegalArgumentException("L'utilisateur est obligatoire.");
        }
        EntityManager em = emf.createEntityManager();
        EntityTransaction et = em.getTransaction();

        try {
            et.begin();
            em.persist(utilisateur);
            et.commit();

        } catch (RuntimeException e) {
            if (et.isActive()) et.rollback();
            throw e;

        } finally {
            em.close();
        }
    }

    // GET-BY-ID
    public Utilisateur rechercherUtilisateurId(Long id) {
        EntityManager em = emf.createEntityManager();

        try {
            return em.find(Utilisateur.class, id);

        } finally {
            em.close();
        }
    }

    // GET-BY-EMAIL
    public Utilisateur rechercherUtilisateurEmail(String email) {
        EntityManager em = emf.createEntityManager();

        try {
            List<Utilisateur> resultats = em.createQuery(
                    "SELECT u FROM Utilisateur u " + "WHERE u.email = :email",
                    Utilisateur.class
            ).setParameter("email", email).getResultList();
            return resultats.isEmpty() ? null : resultats.get(0);

        } finally {
            em.close();
        }
    }

    // GET-ALL
    public List<Utilisateur> listeUtilisateursTous() {
        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                    "SELECT u FROM Utilisateur u ORDER BY u.id DESC",
                    Utilisateur.class
            ).getResultList();

        } finally {
            em.close();
        }
    }

    // GET-ACTIFS
    public List<Utilisateur> listeUtilisateursActifs() {
        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                    "SELECT u FROM Utilisateur u " + "WHERE u.actif = true ORDER BY u.id DESC",
                    Utilisateur.class
            ).getResultList();

        } finally {
            em.close();
        }
    }

    // GET-ARCHIVES
    public List<Utilisateur> listeUtilisateursArchives() {
        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                    "SELECT u FROM Utilisateur u " + "WHERE u.actif = false ORDER BY u.id DESC",
                    Utilisateur.class
            ).getResultList();

        } finally {
            em.close();
        }
    }

    // UPDATE
    public Utilisateur modifierUtilisateur(Utilisateur utilisateur) {
        if (utilisateur == null || utilisateur.getId() == null) {
            throw new IllegalArgumentException("L'identifiant et l'utilisateur sont obligatoires.");
        }
        EntityManager em = emf.createEntityManager();
        EntityTransaction et = em.getTransaction();

        try {
            et.begin();
            Utilisateur existant = em.find(Utilisateur.class, utilisateur.getId());
            if (existant == null) {
                throw new IllegalArgumentException("Utilisateur introuvable : " + utilisateur.getId());
            }
            existant.setNom(utilisateur.getNom());
            existant.setPrenom(utilisateur.getPrenom());
            existant.setEmail(utilisateur.getEmail());
            et.commit();
            return existant;

        } catch (RuntimeException e) {
            if (et.isActive()) et.rollback();
            throw e;

        } finally {
            em.close();
        }
    }

    // ARCHIVER
    public void archiverUtilisateur(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("L'identifiant de l'utilisateur est obligatoire.");
        }
        EntityManager em = emf.createEntityManager();
        EntityTransaction et = em.getTransaction();

        try {
            et.begin();
            Utilisateur utilisateur = em.find(Utilisateur.class, id);

            if (utilisateur == null) {
                throw new IllegalArgumentException("Utilisateur introuvable : " + id);
            }
            utilisateur.setActif(false);
            et.commit();

        } catch (RuntimeException e) {
            if (et.isActive()) et.rollback();
            throw e;

        } finally {
            em.close();
        }
    }
}
