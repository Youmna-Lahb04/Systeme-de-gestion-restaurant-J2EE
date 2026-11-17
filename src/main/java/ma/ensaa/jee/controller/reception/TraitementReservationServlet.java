package ma.ensaa.jee.controller.reception;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ma.ensaa.jee.entity.HistoriqueStatut;
import ma.ensaa.jee.entity.Reservation;
import ma.ensaa.jee.entity.Utilisateur;
import ma.ensaa.jee.entity.enums.Role;
import ma.ensaa.jee.service.HistoriqueStatutService;
import ma.ensaa.jee.service.ReservationService;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/reception/reservations", "/reservations"})
public class TraitementReservationServlet extends HttpServlet {

    private ReservationService reservationService;
    private HistoriqueStatutService historiqueStatutService;

    @Override
    public void init() {
        this.reservationService = new ReservationService();
        this.historiqueStatutService = new HistoriqueStatutService();
    }

    public TraitementReservationServlet(ReservationService reservationService,
                                        HistoriqueStatutService historiqueStatutService) {
        this.reservationService = reservationService;
        this.historiqueStatutService = historiqueStatutService;
    }

    public TraitementReservationServlet() {
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Utilisateur user = (session != null) ? (Utilisateur) session.getAttribute("user") : null;

        if (user == null || (user.getRole() != Role.RECEPTIONNISTE && user.getRole() != Role.ADMIN)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Si l'utilisateur clique sur "Voir Historique" pour une réservation
        String histoIdStr = request.getParameter("historiqueId");
        if (histoIdStr != null && !histoIdStr.isBlank()) {
            try {
                Long resId = Long.parseLong(histoIdStr);
                List<HistoriqueStatut> auditLogs = historiqueStatutService.getHistoriqueReservation(resId, user.getId());
                request.setAttribute("auditLogs", auditLogs);
                request.setAttribute("selectedResId", resId);
            } catch (Exception e) {
                request.setAttribute("erreur", e.getMessage());
            }
        }

        List<Reservation> reservations = reservationService.getAllReservations(user.getId());
        request.setAttribute("reservations", reservations);

        request.getRequestDispatcher("/WEB-INF/views/reception/reservations.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        Utilisateur user = (session != null) ? (Utilisateur) session.getAttribute("user") : null;

        if (user == null || (user.getRole() != Role.RECEPTIONNISTE && user.getRole() != Role.ADMIN)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        String resIdStr = request.getParameter("reservationId");

        try {
            Long resId = Long.parseLong(resIdStr);
            if ("confirmer".equals(action)) {
                reservationService.confirmerReservation(resId, user.getId());
                session.setAttribute("succes", "La réservation #" + resId + " a été CONFIRMÉE avec succès.");
            } else if ("refuser".equals(action)) {
                reservationService.refuserReservation(resId, user.getId());
                session.setAttribute("succes", "La réservation #" + resId + " a été REFUSÉE.");
            }
        } catch (Exception e) {
            session.setAttribute("erreur", e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/reception/reservations");
    }
}