package ma.ensaa.jee.service;

import ma.ensaa.jee.entity.Utilisateur;

import java.util.List;

public class UtilisateurService {
    public Utilisateur ajouterUtilisateur(
            Utilisateur utilisateur,
            Long utilisateurConnecteId
    ){
        return null;
    };

    public Utilisateur modifierUtilisateur(
            Utilisateur utilisateur,
            Long utilisateurConnecteId
    ){
        return null;
    };

    public void supprimerUtilisateur(
            Long id,
            Long utilisateurConnecteId
    ){};

    public Utilisateur getUtilisateurById(
            Long id,
            Long utilisateurConnecteId
    ){
        return null;
    };

    public List<Utilisateur> getAllUtilisateurs(
            Long utilisateurConnecteId
    ){
        return null;
    };
}
