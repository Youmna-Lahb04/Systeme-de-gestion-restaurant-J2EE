package ma.ensaa.jee.controller.client;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ma.ensaa.jee.entity.Reservation;
import ma.ensaa.jee.entity.TableRestaurant;
import ma.ensaa.jee.entity.Utilisateur;
import ma.ensaa.jee.service.ReservationService;
import ma.ensaa.jee.service.TableService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

@WebServlet("/client/reserver")
public class ReserverServlet extends HttpServlet {

    private ReservationService reservationService;
    private TableService tableService;

    @Override
    public void init() {
        this.reservationService = new ReservationService();
        this.tableService = new TableService();
    }

    public ReserverServlet(ReservationService reservationService, TableService tableService) {
        this.reservationService = reservationService;
        this.tableService = tableService;
    }

    public ReserverServlet() {
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Utilisateur user = (session != null) ? (Utilisateur) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String dateStr = request.getParameter("date");
        String heureStr = request.getParameter("heure");
        String nbPersonnesStr = request.getParameter("nombrePersonnes");

        List<TableRestaurant> tablesDisponibles = Collections.emptyList();

        if (dateStr != null && !dateStr.isBlank() &&
                heureStr != null && !heureStr.isBlank() &&
                nbPersonnesStr != null && !nbPersonnesStr.isBlank()) {
            try {
                LocalDate date = LocalDate.parse(dateStr);
                LocalTime heure = LocalTime.parse(heureStr);
                int nb = Integer.parseInt(nbPersonnesStr);
                tablesDisponibles = tableService.getTablesDisponibles(date, heure, nb);
                request.setAttribute("dateCherchee", dateStr);
                request.setAttribute("heureCherchee", heureStr);
                request.setAttribute("nbPersonnesCherche", nb);
            } catch (Exception e) {
                request.setAttribute("erreur", e.getMessage());
            }
        }

        request.setAttribute("tablesDisponibles", tablesDisponibles);
        request.getRequestDispatcher("/WEB-INF/views/client/reserver.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        Utilisateur user = (session != null) ? (Utilisateur) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String dateStr = request.getParameter("date");
        String heureDebutStr = request.getParameter("heureDebut");
        String nbPersonnesStr = request.getParameter("nombrePersonnes");
        String tableIdStr = request.getParameter("tableId");

        try {
            LocalDate date = LocalDate.parse(dateStr);
            LocalTime heureDebut = LocalTime.parse(heureDebutStr);
            LocalTime heureFin = heureDebut.plusHours(3); // Durée standard du créneau (3h)
            int nbPersonnes = Integer.parseInt(nbPersonnesStr);
            Long tableId = Long.parseLong(tableIdStr);

            Reservation reservation = new Reservation();
            reservation.setDateReservation(date);
            reservation.setHeureDebut(heureDebut);
            reservation.setHeureFin(heureFin);
            reservation.setNombrePersonnes(nbPersonnes);

            TableRestaurant table = new TableRestaurant();
            table.setId(tableId);
            reservation.setTableRestaurant(table);

            reservationService.creerReservation(reservation, user.getId());

            session.setAttribute("succes", "Votre réservation pour le " + date + " à " + heureDebut + " a été créée avec succès !");
            response.sendRedirect(request.getContextPath() + "/client/mes-reservations");

        } catch (Exception e) {
            request.setAttribute("erreur", e.getMessage());
            request.setAttribute("dateCherchee", dateStr);
            request.setAttribute("heureCherchee", heureDebutStr);
            request.setAttribute("nbPersonnesCherche", nbPersonnesStr);
            request.getRequestDispatcher("/WEB-INF/views/client/reserver.jsp").forward(request, response);
        }
    }
}