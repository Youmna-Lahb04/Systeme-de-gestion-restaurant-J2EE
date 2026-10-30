package ma.ensaa.jee.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import ma.ensaa.jee.entity.TableRestaurant;
import ma.ensaa.jee.util.JPAUtil;

import java.util.List;

public class TableDAO {

    //get l'usine de JPAUtil
    private final EntityManagerFactory emf = JPAUtil.getEntityManagerFactory();

    //ADD
    public void ajouterTable(TableRestaurant table) {
        //l'usine cree un entitee manager et
        //entitymanager appelle lobjet entityTransaction
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            //rend l'entity donnee en params persistence
            em.persist(table);
            //permet a hibernate dexecutee la req sql
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();// toujours fermer le entitymanger pour liberer les ressources
        }
    }
    //UPDATE
    public TableRestaurant modifierTable(TableRestaurant table) {
        if (table == null || table.getId() == null) {
            throw new IllegalArgumentException(
                    "La table et son identifiant sont obligatoires."
            );
        }
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            TableRestaurant resultat =
                    em.find(TableRestaurant.class, table.getId());

            if (resultat == null) {
                throw new IllegalArgumentException(
                        "Aucune table trouvee avec cet identifiant."
                );
            }
            resultat.setNumero(table.getNumero());
            resultat.setCapacite(table.getCapacite());
            //merge() permet de recopier lles valeurs de l'objet vers une
            //entity gerere par JPA
//            TableRestaurant resultat = em.merge(table);
            tx.commit();
            return resultat;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
    //DELETE
    public void supprimerTable(Long id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            TableRestaurant table =
                    em.find(TableRestaurant.class, id);
            if (table == null) {
                throw new IllegalArgumentException(
                        "La table n'existe pas."
                );
            }
            Long nombreReservations = em.createQuery(
                            "SELECT COUNT(r) FROM Reservation r " +
                                    "WHERE r.tableRestaurant.id = :id",
                            Long.class
                    )
                    .setParameter("id", id)
                    .getSingleResult();

            if (nombreReservations > 0) {
                throw new IllegalStateException(
                        "Impossible de supprimer cette table : " +
                                "elle possède des réservations."
                );
            }
            em.remove(table);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
    //GET-ALL
    public List<TableRestaurant> trouverToutes() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT t FROM TableRestaurant t ORDER BY t.numero",
                    TableRestaurant.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    //GET-by-ID
    public TableRestaurant trouverParId(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(TableRestaurant.class, id);
        } finally {
            em.close();
        }
    }
    //GET-by-Numero
    public TableRestaurant trouverParNumero(Integer numero) {
        EntityManager em = emf.createEntityManager();
        try {
            List<TableRestaurant> resultats = em.createQuery(
                            "SELECT t FROM TableRestaurant t " +
                                    "WHERE t.numero = :numero",
                            TableRestaurant.class
                    )
                    .setParameter("numero", numero)
                    .getResultList();

            return resultats.isEmpty() ? null : resultats.get(0);
        } finally {
            em.close();
        }
    }



}
