
package ma.ensaa.jee.util;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

public class JPAUtil {

    private static final EntityManagerFactory emf = createEntityManagerFactory();

    private static EntityManagerFactory createEntityManagerFactory() {
        Map<String, Object> properties = new HashMap<>();

        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

        if (url == null || user == null || password == null) {
            throw new IllegalStateException(
                "Variables DB_URL, DB_USER et DB_PASSWORD non configurees."
            );
        }

        properties.put("jakarta.persistence.jdbc.driver",
                "com.mysql.cj.jdbc.Driver");
        properties.put("jakarta.persistence.jdbc.url", url);
        properties.put("jakarta.persistence.jdbc.user", user);
        properties.put("jakarta.persistence.jdbc.password", password);

        return Persistence.createEntityManagerFactory(
                "restaurantPU", properties);
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

    public static void close() {
        if (emf.isOpen()) {
            emf.close();
        }
    }
}
