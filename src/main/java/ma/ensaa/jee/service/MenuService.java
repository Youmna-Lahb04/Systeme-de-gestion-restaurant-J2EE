package ma.ensaa.jee.service;

import ma.ensaa.jee.dao.MenuDAO;
import ma.ensaa.jee.entity.Menu;
import ma.ensaa.jee.entity.enums.Role;

import java.util.List;

public class MenuService {
    private final MenuDAO menuDAO;
    private final AuthorizationService authorizationService;
    public MenuService() {
        this.menuDAO=new MenuDAO();
        this.authorizationService=new AuthorizationService();
    }
    public Menu ajouterMenu(Menu menu,Long utilisateurConnecteId)
    {
        authorizationService.verifierRole(utilisateurConnecteId, Role.ADMIN);
        if(menu==null){
            throw new IllegalArgumentException("Les informations du menu sont obligatoires");
        }
        if (menu.getNom() == null || menu.getNom().isBlank()) {
            throw new IllegalArgumentException("Le nom du menu est obligatoire.");
        }
        menuDAO.ajouterMenu(menu);
        return menu;
    };

    public Menu modifierMenu(Menu menu, Long utilisateurConnecteId)
    {
        authorizationService.verifierRole(utilisateurConnecteId, Role.ADMIN);
        if (menu == null || menu.getId() == null) {
            throw new IllegalArgumentException("Le menu et son identifiant sont obligatoires.");
        }
        if (menu.getNom() == null || menu.getNom().isBlank()) {
            throw new IllegalArgumentException("Le nom du menu est obligatoire.");
        }

        return menuDAO.modifierMenu(menu);
    };

    public Menu getMenuGeneral(){
        return menuDAO.trouverMenuGeneral();
    };

}
