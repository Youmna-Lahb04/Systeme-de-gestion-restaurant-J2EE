package ma.ensaa.jee.test;
import org.mindrot.jbcrypt.BCrypt;

public class GenererPassword {

    public static void main(String[] args) {

        // Mot de passe initial de l'administrateur
        String password = "Admin1234";

        // Génération du hash BCrypt
        String passwordHash = BCrypt.hashpw(
                password,
                BCrypt.gensalt(12)
        );

        // Affichage du hash
        System.out.println("Mot de passe : " + password);
        System.out.println("Hash BCrypt : " + passwordHash);
    }
}
