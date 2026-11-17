package ma.ensaa.jee.controller.client;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ma.ensaa.jee.entity.Reservation;
import ma.ensaa.jee.entity.Utilisateur;
import ma.ensaa.jee.service.ReservationService;

import java.io.IOException;
import java.util.List;

@WebServlet("/client/mes-reservations")
public class MesReservationsServlet extends HttpServlet {

    private ReservationService reservationService;

    @Override
    public void init() {
        this.reservationService = new ReservationService();
    }

    public MesReservationsServlet(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    public MesReservationsServlet() {
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

        List<Reservation> mesReservations = reservationService.getMesReservations(user.getId());
        request.setAttribute("reservations", mesReservations);
        request.getRequestDispatcher("/WEB-INF/views/client/mes-reservations.jsp").forward(request, response);
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

        String action = request.getParameter("action");
        String reservationIdStr = request.getParameter("reservationId");

        try {
            if ("annuler".equals(action)) {
                Long reservationId = Long.parseLong(reservationIdStr);
                reservationService.annulerReservation(reservationId, user.getId());
                session.setAttribute("succes", "Votre réservation a été annulée avec succès.");
            }
        } catch (Exception e) {
            session.setAttribute("erreur", e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/client/mes-reservations");
    }
}