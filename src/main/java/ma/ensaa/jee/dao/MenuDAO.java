package ma.ensaa.jee.dao;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import ma.ensaa.jee.entity.Menu;
import ma.ensaa.jee.util.JPAUtil;

public class MenuDAO {

    private final EntityManagerFactory emf = JPAUtil.getEntityManagerFactory();

    // ADD : créer le menu général une seule fois
    public void ajouterMenu(Menu menu) {
        if (menu == null) {
            throw new IllegalArgumentException("Le menu est obligatoire.");
        }

        EntityManager em = emf.createEntityManager();
        EntityTransaction et = em.getTransaction();

        try {
            et.begin();
            Long nombreMenus = em.createQuery(
                    "SELECT COUNT(m) FROM Menu m",
                    Long.class
            ).getSingleResult();

            if (nombreMenus > 0) {
                throw new IllegalStateException("Le menu général existe déjà.");
            }
            // Le menu général est créé sans plats.
            menu.getPlats().clear();
            em.persist(menu);
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

    // FIND : retrouver le menu général avec ses plats
    public Menu trouverMenuGeneral() {
        EntityManager em = emf.createEntityManager();

        try {
            List<Menu> menus = em.createQuery(
                    "SELECT DISTINCT m FROM Menu m " + "LEFT JOIN FETCH m.plats ORDER BY m.id",
                    Menu.class
            ).setMaxResults(2).getResultList();

            if (menus.size() > 1) {
                throw new IllegalStateException("Plusieurs menus existent dans la base.");
            }
            return menus.isEmpty() ? null : menus.getFirst();

        } finally {
            em.close();
        }
    }

    // UPDATE : modifier les informations du menu
    public Menu modifierMenu(Menu menu) {
        if (menu == null || menu.getId() == null) {
            throw new IllegalArgumentException("Le menu et son identifiant sont obligatoires.");
        }

        EntityManager em = emf.createEntityManager();
        EntityTransaction et = em.getTransaction();

        try {
            et.begin();
            Menu resultat = em.find(Menu.class, menu.getId());

            if (resultat == null) {
                throw new IllegalArgumentException("Aucun menu trouvé avec cet identifiant.");
            }
            resultat.setNom(menu.getNom());
            resultat.setDescription(menu.getDescription());
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


}

