package ma.ensaa.jee.test;

import jakarta.persistence.EntityManager;
import ma.ensaa.jee.util.JPAUtil;

public class TestConnexion1 {

    public static void main(String[] args) {

        EntityManager em = null;

        try {

            // 2. Créer EntityManager
            em = JPAUtil.getEntityManager();

            // 3. Tester la connexion
            em.getTransaction().begin();

            Object resultat = em.createNativeQuery("SELECT 1")
                    .getSingleResult();

            em.getTransaction().commit();

            System.out.println("Résultat SQL : " + resultat);
            System.out.println("Connexion MySQL réussie !");

        } catch (Exception e) {

            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            e.printStackTrace();

        } finally {

            if (em != null && em.isOpen()) {
                em.close();
            }

            JPAUtil.close();
        }
    }
}