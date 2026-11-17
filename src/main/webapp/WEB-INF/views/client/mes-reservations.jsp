<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="ma.ensaa.jee.entity.Reservation" %>
<%@ page import="ma.ensaa.jee.entity.Utilisateur" %>
<%@ page import="ma.ensaa.jee.entity.enums.StatutReservation" %>
<%@ page import="java.util.List" %>
<%
    String erreur = (String) request.getAttribute("erreur");
    if (erreur == null && session.getAttribute("erreur") != null) {
        erreur = (String) session.getAttribute("erreur");
        session.removeAttribute("erreur");
    }
    String succes = null;
    if (session.getAttribute("succes") != null) {
        succes = (String) session.getAttribute("succes");
        session.removeAttribute("succes");
    }
    String contextPath = request.getContextPath();

    Utilisateur userConnecte = (session != null) ? (Utilisateur) session.getAttribute("user") : null;
    List<Reservation> reservations = (List<Reservation>) request.getAttribute("reservations");
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mes Réservations - Gestion Restaurant</title>
    <!-- Bootstrap 5 CSS & Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
</head>
<body class="bg-light py-5">

<div class="container">
    <div class="row justify-content-center">
        <div class="col-12 col-lg-10 col-xl-9">
            <div class="card shadow-sm border">
                <div class="card-body p-4 p-md-5">

                    <!-- En-tête centré -->
                    <div class="text-center mb-4">
                        <i class="bi bi-clock-history fs-1 text-primary"></i>
                        <h3 class="fw-bold mt-2">Mes Réservations</h3>
                        <p class="text-muted small">Suivi et gestion de vos réservations de table</p>
                    </div>

                    <!-- Alertes -->
                    <% if (erreur != null && !erreur.isBlank()) { %>
                    <div class="alert alert-danger alert-dismissible fade show small" role="alert">
                        <i class="bi bi-exclamation-triangle-fill me-1"></i> <%= erreur %>
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Fermer"></button>
                    </div>
                    <% } %>

                    <% if (succes != null && !succes.isBlank()) { %>
                    <div class="alert alert-success alert-dismissible fade show small" role="alert">
                        <i class="bi bi-check-circle-fill me-1"></i> <%= succes %>
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Fermer"></button>
                    </div>
                    <% } %>

                    <!-- Bouton Nouvelle Réservation -->
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <span class="small text-muted">Historique de vos réservations</span>
                        <a href="<%= contextPath %>/client/reserver" class="btn btn-primary btn-sm">
                            <i class="bi bi-plus-circle"></i> Réserver une table
                        </a>
                    </div>

                    <!-- Tableau des Réservations -->
                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0">
                            <thead class="table-light">
                            <tr>
                                <th class="ps-3">Date</th>
                                <th>Créneau</th>
                                <th>Table</th>
                                <th>Pers.</th>
                                <th>Statut</th>
                                <th class="text-end pe-3">Action</th>
                            </tr>
                            </thead>
                            <tbody>
                            <% if (reservations == null || reservations.isEmpty()) { %>
                            <tr>
                                <td colspan="6" class="text-center py-5 text-muted">
                                    <i class="bi bi-calendar-x fs-2 d-block mb-2"></i>
                                    Vous n'avez aucune réservation enregistrée.<br>
                                    <a href="<%= contextPath %>/client/reserver" class="btn btn-outline-primary btn-sm mt-3">
                                        Réserver une table maintenant
                                    </a>
                                </td>
                            </tr>
                            <% } else {
                                for (Reservation r : reservations) {
                                    StatutReservation st = r.getStatutReservation();
                                    String badgeClass = "bg-secondary";
                                    if (st == StatutReservation.CONFIRMEE) badgeClass = "bg-success";
                                    else if (st == StatutReservation.EN_ATTENTE) badgeClass = "bg-warning text-dark";
                                    else if (st == StatutReservation.REFUSEE) badgeClass = "bg-danger";
                                    else if (st == StatutReservation.ANNULEE) badgeClass = "bg-secondary";
                                    else if (st == StatutReservation.TERMINEE) badgeClass = "bg-info text-dark";
                            %>
                            <tr>
                                <td class="ps-3 fw-bold"><%= r.getDateReservation() %></td>
                                <td>
                                    <i class="bi bi-clock me-1 text-muted"></i>
                                    <%= r.getHeureDebut() %> &agrave; <%= r.getHeureFin() %>
                                </td>
                                <td>
                                    <span class="badge bg-light text-dark border">
                                        Table #<%= (r.getTableRestaurant() != null) ? r.getTableRestaurant().getNumero() : "-" %>
                                    </span>
                                </td>
                                <td>
                                    <i class="bi bi-people text-secondary me-1"></i><%= r.getNombrePersonnes() %>
                                </td>
                                <td>
                                    <span class="badge <%= badgeClass %> px-2 py-1"><%= st %></span>
                                </td>
                                <td class="text-end pe-3">
                                    <% if (st == StatutReservation.EN_ATTENTE || st == StatutReservation.CONFIRMEE) { %>
                                    <button type="button" class="btn btn-outline-danger btn-sm py-0 px-2"
                                            title="Annuler la réservation"
                                            onclick="confirmerAnnulation('<%= r.getId() %>', '<%= r.getDateReservation() %>', '<%= r.getHeureDebut() %>')">
                                        <i class="bi bi-x-circle"></i> Annuler
                                    </button>
                                    <% } else { %>
                                    <span class="text-muted small">&mdash;</span>
                                    <% } %>
                                </td>
                            </tr>
                            <%   }
                            } %>
                            </tbody>
                        </table>
                    </div>

                    <!-- Pied de carte -->
                    <hr class="my-4">
                    <div class="d-flex justify-content-between align-items-center">
                        <div class="d-flex gap-3">
                            <a href="<%= contextPath %>/carte" class="small text-secondary text-decoration-none">
                                <i class="bi bi-book"></i> Consulter la Carte
                            </a>
                            <a href="<%= contextPath %>/client/reserver" class="small text-primary text-decoration-none">
                                <i class="bi bi-plus-circle"></i> Nouvelle réservation
                            </a>
                        </div>
                        <a href="<%= contextPath %>/logout" class="small text-danger text-decoration-none fw-semibold">
                            <i class="bi bi-box-arrow-right"></i> Déconnexion
                        </a>
                    </div>

                </div>
            </div>
        </div>
    </div>
</div>

<!-- Modal Confirmation Annulation (Gabarit binôme) -->
<div class="modal fade" id="modalAnnuler" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border">
            <div class="modal-body p-4 text-center">
                <i class="bi bi-exclamation-circle fs-1 text-danger mb-2"></i>
                <h5 class="fw-bold">Annuler la Réservation</h5>
                <p class="text-muted small">
                    Confirmez-vous l'annulation de votre réservation du <strong id="annul_date"></strong> à <strong id="annul_heure"></strong> ?
                </p>
                <form action="<%= contextPath %>/client/mes-reservations" method="post">
                    <input type="hidden" name="action" value="annuler">
                    <input type="hidden" id="annul_id" name="reservationId">
                    <button type="submit" class="btn btn-danger w-100 py-2 mt-3">Confirmer l'annulation</button>
                    <button type="button" class="btn btn-link text-secondary w-100 mt-1 text-decoration-none small" data-bs-dismiss="modal">Retour</button>
                </form>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function confirmerAnnulation(id, date, heure) {
        document.getElementById('annul_id').value = id;
        document.getElementById('annul_date').innerText = date;
        document.getElementById('annul_heure').innerText = heure;
        new bootstrap.Modal(document.getElementById('modalAnnuler')).show();
    }
</script>
</body>
</html>