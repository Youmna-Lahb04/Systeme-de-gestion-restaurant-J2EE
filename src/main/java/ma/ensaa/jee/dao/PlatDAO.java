package ma.ensaa.jee.dao;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import ma.ensaa.jee.entity.Menu;
import ma.ensaa.jee.entity.Plat;
import ma.ensaa.jee.util.JPAUtil;

public class PlatDAO {

    private final EntityManagerFactory emf = JPAUtil.getEntityManagerFactory();

    // ADD : enregistrer un plat et l'associer au menu général
    public void ajouterPlat(Plat plat) {
        if (plat == null) {
            throw new IllegalArgumentException("Le plat est obligatoire.");
        }

        EntityManager em = emf.createEntityManager();
        EntityTransaction et = em.getTransaction();

        try {
            et.begin();
            // Chercher le menu général existant.
            List<Menu> menus = em.createQuery(
                    "SELECT m FROM Menu m ORDER BY m.id",
                    Menu.class
            ).setMaxResults(2).getResultList();
            if (menus.isEmpty()) {
                throw new IllegalStateException("Créez d'abord le menu général.");
            }

            if (menus.size() > 1) {
                throw new IllegalStateException("Plusieurs menus existent dans la base.");
            }
            // Récupérer le menu général
            Menu menuGeneral = menus.get(0);

            // Associer le plat au menu général
            plat.setMenu(menuGeneral);

            em.persist(plat);
            et.commit();

        } catch (RuntimeException e) {
            if (et.isActive()) {
                et.rollback();
            }
            throw e;

        } finally {
            em.close();
        }
    }

    // UPDATE : modifier les informations d'un plat
    public Plat modifierPlat(Plat plat) {
        if (plat == null || plat.getId() == null) {
            throw new IllegalArgumentException("Le plat et son identifiant sont obligatoires.");
        }

        EntityManager em = emf.createEntityManager();
        EntityTransaction et = em.getTransaction();

        try {
            et.begin();
            Plat resultat = em.find(Plat.class, plat.getId());
            if (resultat == null) {
                throw new IllegalArgumentException("Aucun plat trouvé avec cet identifiant.");
            }

            resultat.setNom(plat.getNom());
            resultat.setDescription(plat.getDescription());
            resultat.setPrix(plat.getPrix());
            resultat.setCategorie(plat.getCategorie());
            resultat.setDisponible(plat.isDisponible());

            et.commit();
            return resultat;

        } catch (RuntimeException e) {
            if (et.isActive()) {
                et.rollback();
            }
            throw e;

        } finally {
            em.close();
        }
    }

    // DELETE : supprimer un plat
    public void supprimerPlat(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'identifiant du plat est obligatoire.");
        }

        EntityManager em = emf.createEntityManager();
        EntityTransaction et = em.getTransaction();

        try {
            et.begin();
            Plat plat = em.find(Plat.class, id);

            if (plat == null) {
                throw new IllegalArgumentException("Aucun plat trouvé avec cet identifiant.");
            }

            // Retirer le plat de la collection en mémoire.
            if (plat.getMenu() != null) {
                plat.getMenu().getPlats().remove(plat);
            }
            em.remove(plat);
            et.commit();

        } catch (RuntimeException e) {
            if (et.isActive()) {
                et.rollback();
            }
            throw e;

        } finally {
            em.close();
        }
    }

    // GET-BY-ID
    public Plat trouverPlatId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'identifiant du plat est obligatoire.");
        }
        EntityManager em = emf.createEntityManager();

        try {
            return em.find(Plat.class, id);

        } finally {
            em.close();
        }
    }

    // GET-ALL
    public List<Plat> trouverPlatTous() {
        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                    "SELECT p FROM Plat p ORDER BY p.id DESC",
                    Plat.class
            ).getResultList();

        } finally {
            em.close();
        }
    }

    // GET-AVAILABLE
    public List<Plat> trouverPlatsDisponibles() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT p FROM Plat p " + "WHERE p.disponible = true ORDER BY p.nom",
                    Plat.class
            ).getResultList();

        } finally {
            em.close();
        }
    }
}
