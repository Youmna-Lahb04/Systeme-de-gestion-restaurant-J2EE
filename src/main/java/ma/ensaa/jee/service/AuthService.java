package ma.ensaa.jee.service;

import ma.ensaa.jee.dao.UtilisateurDAO;
import ma.ensaa.jee.entity.Utilisateur;
import ma.ensaa.jee.entity.enums.Role;
import org.mindrot.jbcrypt.BCrypt;
public class AuthService {
    private final UtilisateurDAO utilisateurDAO;
    public AuthService() {
        this.utilisateurDAO = new UtilisateurDAO();
    }
    //return user authentifie
    public Utilisateur authentifier(String email, String motDePasse){
        if (email == null || email.isBlank()
                || motDePasse == null || motDePasse.isBlank()) {
            throw new IllegalArgumentException(
                    "L'email et le mot de passe sont obligatoires."
            );
        }
        Utilisateur utilisateur =
                utilisateurDAO.rechercherUtilisateurEmail(email);
        if (utilisateur == null) {
            throw new IllegalArgumentException(
                    "Email ou mot de passe incorrect."
            );
        }
        if (!utilisateur.isActif()) {
            throw new IllegalArgumentException(
                    "Ce compte est désactivé."
            );
        }
        //check if mdp correspond au hash stocke
        boolean motDePasseCorrect = BCrypt.checkpw(
                motDePasse,
                utilisateur.getMotDePasse()
        );
        if (!motDePasseCorrect) {
            throw new IllegalArgumentException(
                    "Email ou mot de passe incorrect."
            );
        }
    return utilisateur;
    }


    public Utilisateur inscrireClient(
            Utilisateur utilisateur
    ){
        if (utilisateur == null) {
            throw new IllegalArgumentException(
                    "Les informations de l'utilisateur sont obligatoires."
            );
        }


        if (utilisateur.getEmail() == null
                || utilisateur.getEmail().isBlank()) {
            throw new IllegalArgumentException(
                    "L'email est obligatoire."
            );
        }
        String email= utilisateur.getEmail();
        utilisateur.setEmail(email);
        Utilisateur utilisateurExistant =
                utilisateurDAO.rechercherUtilisateurEmail(email);

        if (utilisateurExistant != null) {
            throw new IllegalArgumentException(
                    "Cette adresse email est déjà utilisée."
            );
        }
        String motDePasse = utilisateur.getMotDePasse();
        if (motDePasse == null || motDePasse.length() < 8) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins 8 caractères.");
        }
        String motDePasseHash = BCrypt.hashpw(
                motDePasse,
                BCrypt.gensalt(12)
        );

        utilisateur.setMotDePasse(motDePasseHash);
        utilisateur.setRole(Role.CLIENT);
        utilisateur.setActif(true);
        utilisateurDAO.ajouterUtilisateur(utilisateur);
    return utilisateur;
    }
}
