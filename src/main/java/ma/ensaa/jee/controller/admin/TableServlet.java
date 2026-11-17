package ma.ensaa.jee.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ma.ensaa.jee.entity.TableRestaurant;
import ma.ensaa.jee.entity.Utilisateur;
import ma.ensaa.jee.entity.enums.Role;
import ma.ensaa.jee.service.TableService;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/tables")
public class TableServlet extends HttpServlet {

    private TableService tableService;

    @Override
    public void init() {
        this.tableService = new TableService();
    }

    public TableServlet(TableService tableService) {
        this.tableService = tableService;
    }

    public TableServlet() {
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Utilisateur user = (session != null) ? (Utilisateur) session.getAttribute("user") : null;

        if (user == null || user.getRole() != Role.ADMIN) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        List<TableRestaurant> tables = tableService.getAllTables();
        request.setAttribute("tables", tables);
        request.getRequestDispatcher("/WEB-INF/views/admin/tables.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        Utilisateur user = (session != null) ? (Utilisateur) session.getAttribute("user") : null;

        if (user == null || user.getRole() != Role.ADMIN) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) action = "ajouter";

        try {
            switch (action) {
                case "ajouter": {
                    Integer numero = Integer.parseInt(request.getParameter("numero"));
                    Integer capacite = Integer.parseInt(request.getParameter("capacite"));
                    TableRestaurant table = new TableRestaurant();
                    table.setNumero(numero);
                    table.setCapacite(capacite);
                    tableService.ajouterTable(table, user.getId());
                    session.setAttribute("succes", "La table N° " + numero + " a été ajoutée avec succès !");
                    break;
                }
                case "modifier": {
                    Long id = Long.parseLong(request.getParameter("id"));
                    Integer numero = Integer.parseInt(request.getParameter("numero"));
                    Integer capacite = Integer.parseInt(request.getParameter("capacite"));
                    TableRestaurant table = new TableRestaurant();
                    table.setId(id);
                    table.setNumero(numero);
                    table.setCapacite(capacite);
                    tableService.modifierTableRestaurant(table, user.getId());
                    session.setAttribute("succes", "La table N° " + numero + " a été mise à jour.");
                    break;
                }
                case "supprimer": {
                    Long id = Long.parseLong(request.getParameter("id"));
                    tableService.supprimerTable(id, user.getId());
                    session.setAttribute("succes", "La table a été supprimée avec succès.");
                    break;
                }
                default:
                    throw new IllegalArgumentException("Action inconnue : " + action);
            }
        } catch (Exception e) {
            session.setAttribute("erreur", e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/admin/tables");
    }
}