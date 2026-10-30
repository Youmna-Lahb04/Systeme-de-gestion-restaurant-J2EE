
package ma.ensaa.jee.util;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

public class JPAUtil {

    //une seule usine pour l'application entiere (final == partagee dans l'app)
    private static final EntityManagerFactory emf =
            createEntityManagerFactory();

    //construire et retourner l'usine
    private static EntityManagerFactory createEntityManagerFactory() {

        //transmettre au JPA des donnes supplementaire sur la BD
        Map<String, Object> properties = new HashMap<>();

        //recuperer les var.env du systeme ou sexecute l'app
        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

        if (url == null || user == null || password == null) {
            throw new IllegalStateException(
                    "Variables DB_URL, DB_USER et DB_PASSWORD non configurees."
            );
        }


        //indiquer au JPA quel driver utiliser, addr de la base,nom user et mdp
        properties.put(
                "jakarta.persistence.jdbc.driver",
                "com.mysql.cj.jdbc.Driver"
        );
        properties.put("jakarta.persistence.jdbc.url", url);
        properties.put("jakarta.persistence.jdbc.user", user);
        properties.put("jakarta.persistence.jdbc.password", password);

        return Persistence.createEntityManagerFactory(
                "restaurantPU", properties
        );
    }

    //permet aux classes DAO d'obtenir l'usine, et de creer des entityManagers
    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

    public static void close() {
        if (emf.isOpen()) {
            emf.close(); // toujours fermer l'usine
        }
    }
}
