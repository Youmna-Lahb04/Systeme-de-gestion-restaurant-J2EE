package ma.ensaa.jee.service;

import ma.ensaa.jee.entity.Plat;

import java.util.List;

public class PlatService {

    public Plat ajouterPlat(
            Plat plat,
            Long utilisateurConnecteId
    ){
        return null;
    };

    public Plat modifierPlat(
            Plat plat,
            Long utilisateurConnecteId
    ){
        return null;
    };

    public void supprimerPlat(
            Long id,
            Long utilisateurConnecteId
    ){};

    public Plat getPlatById(Long id){
        return null;
    };

    public List<Plat> getAllPlats(){
        return null;
    };

    public List<Plat> getPlatsDisponibles(String nom){
        return null;
    };
}
