package ma.ensaa.jee.controller.admin;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ma.ensaa.jee.entity.Menu;
import ma.ensaa.jee.entity.Plat;
import ma.ensaa.jee.entity.Utilisateur;
import ma.ensaa.jee.entity.enums.Categorie;
import ma.ensaa.jee.entity.enums.Role;
import ma.ensaa.jee.service.MenuService;
import ma.ensaa.jee.service.PlatService;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet(urlPatterns = {"/admin/menu", "/admin/menus"})
public class MenuAdminServlet extends HttpServlet {
    private MenuService menuService;
    private PlatService platService;
    @Override
    public void init() {
        this.menuService = new MenuService();
        this.platService = new PlatService();
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Utilisateur userConnecte = (session != null) ? (Utilisateur) session.getAttribute("user") : null;
        if (userConnecte == null || userConnecte.getRole() != Role.ADMIN) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        try {
            Menu menuGeneral = menuService.getMenuGeneral();
            List<Plat> plats = platService.getAllPlats();
            request.setAttribute("menuGeneral", menuGeneral);
            request.setAttribute("plats", plats);
            request.setAttribute("categories", Categorie.values());
            request.getRequestDispatcher("/WEB-INF/views/admin/menu.jsp").forward(request, response);
        } catch (Exception e) {
            session.setAttribute("erreur", "Erreur lors du chargement du menu : " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/personnel");
        }
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        Utilisateur userConnecte = (session != null) ? (Utilisateur) session.getAttribute("user") : null;
        if (userConnecte == null || userConnecte.getRole() != Role.ADMIN) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }
        try {
            switch (action) {
                case "creerMenu": {
                    String nom = request.getParameter("nom");
                    String description = request.getParameter("description");
                    Menu m = new Menu();
                    m.setNom(nom);
                    m.setDescription(description);
                    menuService.ajouterMenu(m, userConnecte.getId());
                    session.setAttribute("succes", "Le menu général a été créé avec succès.");
                    break;
                }
                case "modifierMenu": {
                    String idStr = request.getParameter("id");
                    String nom = request.getParameter("nom");
                    String description = request.getParameter("description");
                    Menu m = new Menu();
                    m.setId(Long.parseLong(idStr));
                    m.setNom(nom);
                    m.setDescription(description);
                    menuService.modifierMenu(m, userConnecte.getId());
                    session.setAttribute("succes", "Le menu général a été mis à jour.");
                    break;
                }
                case "ajouterPlat": {
                    String nom = request.getParameter("nom");
                    String description = request.getParameter("description");
                    String prixStr = request.getParameter("prix");
                    String categorieStr = request.getParameter("categorie");
                    String disponibleStr = request.getParameter("disponible");
                    Plat plat = new Plat();
                    plat.setNom(nom);
                    plat.setDescription(description);
                    plat.setPrix(new BigDecimal(prixStr));
                    plat.setCategorie(Categorie.valueOf(categorieStr));
                    plat.setDisponible("on".equals(disponibleStr) || "true".equalsIgnoreCase(disponibleStr));
                    platService.ajouterPlat(plat, userConnecte.getId());
                    session.setAttribute("succes", "Le plat « " + nom + " » a été ajouté à la carte.");
                    break;
                }
                case "modifierPlat": {
                    String idStr = request.getParameter("id");
                    String nom = request.getParameter("nom");
                    String description = request.getParameter("description");
                    String prixStr = request.getParameter("prix");
                    String categorieStr = request.getParameter("categorie");
                    String disponibleStr = request.getParameter("disponible");
                    Plat plat = new Plat();
                    plat.setId(Long.parseLong(idStr));
                    plat.setNom(nom);
                    plat.setDescription(description);
                    plat.setPrix(new BigDecimal(prixStr));
                    plat.setCategorie(Categorie.valueOf(categorieStr));
                    plat.setDisponible("on".equals(disponibleStr) || "true".equalsIgnoreCase(disponibleStr));
                    platService.modifierPlat(plat, userConnecte.getId());
                    session.setAttribute("succes", "Le plat « " + nom + " » a été modifié avec succès.");
                    break;
                }
                case "supprimerPlat": {
                    String idStr = request.getParameter("id");
                    platService.supprimerPlat(Long.parseLong(idStr), userConnecte.getId());
                    session.setAttribute("succes", "Le plat a été retiré de la carte.");
                    break;
                }
                case "toggleDisponibilite": {
                    String idStr = request.getParameter("id");
                    Plat plat = platService.getPlatById(Long.parseLong(idStr));
                    if (plat != null) {
                        plat.setDisponible(!plat.isDisponible());
                        platService.modifierPlat(plat, userConnecte.getId());
                        session.setAttribute("succes", "Disponibilité du plat « " + plat.getNom() + " » mise à jour.");
                    }
                    break;
                }
                default:
                    throw new IllegalArgumentException("Action non prise en charge : " + action);
            }
        } catch (Exception e) {
            session.setAttribute("erreur", e.getMessage());
        }
        response.sendRedirect(request.getContextPath() + "/admin/menu");
    }


}
