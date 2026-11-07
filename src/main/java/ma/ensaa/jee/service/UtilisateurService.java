package ma.ensaa.jee.service;

import ma.ensaa.jee.dao.UtilisateurDAO;
import ma.ensaa.jee.entity.Utilisateur;
import ma.ensaa.jee.entity.enums.Role;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

public class UtilisateurService {

    private final UtilisateurDAO utilisateurDAO;
    private final AuthorizationService authorizationService;

    public UtilisateurService() {
        this.utilisateurDAO = new UtilisateurDAO();
        this.authorizationService = new AuthorizationService();
    }

    public Utilisateur ajouterUtilisateur(Utilisateur utilisateur, Long utilisateurConnecteId)
    {
       authorizationService.verifierRole(utilisateurConnecteId, Role.ADMIN);
       if(utilisateur==null){
           throw new IllegalArgumentException("Les informations de l'utilisateur sont obligatoires");
       }
       if(utilisateur.getEmail()==null||utilisateur.getEmail().isBlank()){
           throw new IllegalArgumentException("L'email est obligatoire");
       }
       String email = utilisateur.getEmail();
       utilisateur.setEmail(email);
       Utilisateur utilisateurExistant= utilisateurDAO.rechercherUtilisateurEmail(email);
       if(utilisateurExistant!=null){
           throw new IllegalArgumentException("L'utilisateur avec ce meme email existe deja");
       }
       if(utilisateur.getMotDePasse()==null||utilisateur.getMotDePasse().length()<8){
           throw new IllegalArgumentException("Le mot de passe est obligatoire et doit contenir au moins 8 caracteres");
       }
       String motDePassehash= BCrypt.hashpw(
               utilisateur.getMotDePasse(),BCrypt.gensalt(12)
       );
       utilisateur.setMotDePasse(motDePassehash);
       utilisateur.setRole(Role.RECEPTIONNISTE);
       utilisateur.setActif(true);
       utilisateurDAO.ajouterUtilisateur(utilisateur);
       return utilisateur;
    };

    public Utilisateur modifierUtilisateur(Utilisateur utilisateur, Long utilisateurConnecteId)
    {
        authorizationService.verifierRole(utilisateurConnecteId, Role.ADMIN);
        if(utilisateur==null){
            throw new IllegalArgumentException("Les informations de l'utilisateur sont obligatoires");
        }
        if(utilisateur.getId()==null){
            throw new IllegalArgumentException("L'ID de l'utilisateur est obligatoire");
        }
        Utilisateur utilisateurExistant= utilisateurDAO.rechercherUtilisateurId(utilisateur.getId());
        if(utilisateurExistant==null){
            throw new IllegalArgumentException("Utilisateur n'existe pas");
        }
        if(utilisateur.getNom()==null||utilisateur.getNom().isBlank()){
            throw new IllegalArgumentException("Le nom d'utilisateur est obligatoire");
        }
        if(utilisateur.getPrenom()==null||utilisateur.getPrenom().isBlank()){
            throw new IllegalArgumentException("le prenom est obligatoire");
        }
        if(utilisateur.getEmail()==null||utilisateur.getEmail().isBlank()){
            throw new IllegalArgumentException("le email est obligatoire");
        }
        String email = utilisateur.getEmail();
        Utilisateur utilisateurEmail= utilisateurDAO.rechercherUtilisateurEmail(email);
        if(utilisateurEmail!=null && !utilisateurEmail.getId().equals(utilisateur.getId())){
            throw new IllegalArgumentException("Cet E-mail existe deja");
        }
        utilisateurExistant.setNom(utilisateur.getNom().trim());
        utilisateurExistant.setPrenom(utilisateur.getPrenom().trim());
        utilisateurExistant.setEmail(email);

        utilisateurDAO.modifierUtilisateur(utilisateurExistant);
        return utilisateur;
    };

    public void supprimerUtilisateur(Long id, Long utilisateurConnecteId)
    {
        authorizationService.verifierRole(utilisateurConnecteId, Role.ADMIN);
        if(id==null){
            throw new IllegalArgumentException("L'ID de l'utilisateur sont obligatoires");
        }
        if(id.equals(utilisateurConnecteId)){
            throw new IllegalArgumentException("vous ne pouvez pas supprimer votre compte");
        }
        Utilisateur utilisateur= utilisateurDAO.rechercherUtilisateurId(id);
        if(utilisateur==null){
            throw new IllegalArgumentException("Utilisateur n'existe pas");
        }
        if(utilisateur.getRole()!=Role.RECEPTIONNISTE){
            throw new IllegalArgumentException("Vous ne pouvez supprimer que les comptes des receptionnistes");
        }

        utilisateurDAO.archiverUtilisateur(id);

    };

    public Utilisateur getUtilisateurById(Long id, Long utilisateurConnecteId)
    {
        authorizationService.verifierRole(utilisateurConnecteId, Role.ADMIN);
        if(id==null){
            throw new IllegalArgumentException("L'ID de l'utilisateur sont obligatoires");
        }
        Utilisateur utilisateur= utilisateurDAO.rechercherUtilisateurId(id);
        if(utilisateur==null){
            throw new IllegalArgumentException("Utilisateur n'existe pas");
        }
        return utilisateur;
    };

    public List<Utilisateur> getAllUtilisateurs(Long utilisateurConnecteId)
    {
        authorizationService.verifierRole(utilisateurConnecteId, Role.ADMIN);
        List<Utilisateur> utilisateurs= utilisateurDAO.listeUtilisateursTous();
        return utilisateurs;
    };
}
