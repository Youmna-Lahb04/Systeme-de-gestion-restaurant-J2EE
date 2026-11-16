package ma.ensaa.jee.controller.admin;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ma.ensaa.jee.entity.Utilisateur;
import ma.ensaa.jee.entity.enums.Role;
import ma.ensaa.jee.service.UtilisateurService;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/personnel")
public class PersonnelServlet extends HttpServlet{

    private UtilisateurService utilisateurService;

    @Override
    public void init() {
        this.utilisateurService = new UtilisateurService();
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
            List<Utilisateur> utilisateurs = utilisateurService.getAllUtilisateurs(userConnecte.getId());
            request.setAttribute("utilisateurs", utilisateurs);
            request.getRequestDispatcher("/WEB-INF/views/admin/personnel.jsp").forward(request, response);
        } catch (Exception e) {
            session.setAttribute("erreur", "Erreur lors du chargement du personnel : " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/login");
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
            action = "ajouter";
        }
        try {
            switch (action) {
                case "ajouter": {
                    String nom = request.getParameter("nom");
                    String prenom = request.getParameter("prenom");
                    String email = request.getParameter("email");
                    String telephone = request.getParameter("telephone");
                    String motDePasse = request.getParameter("motDePasse");
                    Utilisateur nouveau = new Utilisateur();
                    nouveau.setNom(nom);
                    nouveau.setPrenom(prenom);
                    nouveau.setEmail(email);
                    nouveau.setTelephone(telephone);
                    nouveau.setMotDePasse(motDePasse);
                    utilisateurService.ajouterUtilisateur(nouveau, userConnecte.getId());
                    session.setAttribute("succes", "Le membre du personnel a été ajouté avec succès (Réceptionniste).");
                    break;
                }
                case "modifier": {
                    String idStr = request.getParameter("id");
                    if (idStr == null || idStr.isBlank()) {
                        throw new IllegalArgumentException("L'identifiant de l'utilisateur est obligatoire.");
                    }
                    Long id = Long.parseLong(idStr);
                    String nom = request.getParameter("nom");
                    String prenom = request.getParameter("prenom");
                    String email = request.getParameter("email");
                    String telephone = request.getParameter("telephone");
                    Utilisateur modif = new Utilisateur();
                    modif.setId(id);
                    modif.setNom(nom);
                    modif.setPrenom(prenom);
                    modif.setEmail(email);
                    modif.setTelephone(telephone);
                    utilisateurService.modifierUtilisateur(modif, userConnecte.getId());
                    session.setAttribute("succes", "Les informations du personnel ont été mises à jour.");
                    break;
                }
                case "supprimer": {
                    String idStr = request.getParameter("id");
                    if (idStr == null || idStr.isBlank()) {
                        throw new IllegalArgumentException("L'identifiant est obligatoire.");
                    }
                    Long id = Long.parseLong(idStr);
                    utilisateurService.supprimerUtilisateur(id, userConnecte.getId());
                    session.setAttribute("succes", "Le compte du personnel a été désactivé / archivé.");
                    break;
                }
                default:
                    throw new IllegalArgumentException("Action non reconnue : " + action);
            }
        } catch (Exception e) {
            session.setAttribute("erreur", e.getMessage());
        }
        response.sendRedirect(request.getContextPath() + "/admin/personnel");
    }
}
