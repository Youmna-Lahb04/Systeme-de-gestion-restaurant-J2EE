package ma.ensaa.jee.service;

import ma.ensaa.jee.dao.PlatDAO;
import ma.ensaa.jee.entity.Plat;
import ma.ensaa.jee.entity.enums.Role;

import java.math.BigDecimal;
import java.util.List;

public class PlatService {
    private final PlatDAO platDAO;
    private final AuthorizationService authorizationService;
    public PlatService() {
        this.platDAO = new PlatDAO();
        this.authorizationService = new AuthorizationService();
    }
    public Plat ajouterPlat(Plat plat,Long utilisateurConnecteId)
    {
        authorizationService.verifierRole(utilisateurConnecteId, Role.ADMIN);
        validerDonneesPlat(plat);
        platDAO.ajouterPlat(plat);
        return plat;
    };

    public Plat modifierPlat(Plat plat, Long utilisateurConnecteId)
    {
        authorizationService.verifierRole(utilisateurConnecteId, Role.ADMIN);
        if (plat == null || plat.getId() == null) {
            throw new IllegalArgumentException("Le plat et son identifiant sont obligatoires.");
        }
        validerDonneesPlat(plat);
        return platDAO.modifierPlat(plat);
    };

    public void supprimerPlat(Long id, Long utilisateurConnecteId)
    {
        authorizationService.verifierRole(utilisateurConnecteId, Role.ADMIN);
        if (id == null) {
            throw new IllegalArgumentException("L'identifiant du plat est obligatoire.");
        }
        platDAO.supprimerPlat(id);

    };

    public Plat getPlatById(Long id){
        if (id == null) {
            throw new IllegalArgumentException("L'identifiant du plat est obligatoire.");
        }
         return platDAO.trouverPlatId(id);
    };

    public List<Plat> getAllPlats(){
        return platDAO.trouverPlatTous();
    };

    public List<Plat> getPlatsDisponibles(){
        return platDAO.trouverPlatsDisponibles();
    };

    private void validerDonneesPlat(Plat plat) {
        if (plat == null) {
            throw new IllegalArgumentException("Les informations du plat sont obligatoires.");
        }
        if (plat.getNom() == null || plat.getNom().isBlank()) {
            throw new IllegalArgumentException("Le nom du plat est obligatoire.");
        }
        if (plat.getPrix() == null || plat.getPrix().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le prix du plat doit être strictement positif.");
        }
        if (plat.getCategorie() == null) {
            throw new IllegalArgumentException("La catégorie du plat est obligatoire.");
        }
    }
}
