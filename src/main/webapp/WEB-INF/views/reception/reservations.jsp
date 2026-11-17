<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="ma.ensaa.jee.entity.Reservation" %>
<%@ page import="ma.ensaa.jee.entity.HistoriqueStatut" %>
<%@ page import="ma.ensaa.jee.entity.Utilisateur" %>
<%@ page import="ma.ensaa.jee.entity.enums.Role" %>
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
    List<HistoriqueStatut> auditLogs = (List<HistoriqueStatut>) request.getAttribute("auditLogs");
    Long selectedResId = (Long) request.getAttribute("selectedResId");
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Traitement des Réservations - Réception</title>
    <!-- Bootstrap 5 CSS & Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
</head>
<body class="bg-light py-5">

<div class="container-fluid px-lg-5">
    <div class="row justify-content-center">
        <div class="col-12 col-xl-11">
            <div class="card shadow-sm border">
                <div class="card-body p-4 p-md-5">

                    <!-- En-tête centré -->
                    <div class="text-center mb-4">
                        <i class="bi bi-inbox fs-1 text-primary"></i>
                        <h3 class="fw-bold mt-2">Tableau de Bord des Réservations</h3>
                        <p class="text-muted small">Validation, refus et suivi de l'historique d'audit des demandes</p>
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

                    <!-- Section Audit Historique si cliqué -->
                    <% if (auditLogs != null) { %>
                    <div class="p-3 bg-light rounded border mb-4 border-primary">
                        <div class="d-flex justify-content-between align-items-center mb-2">
                            <h6 class="fw-bold text-primary mb-0">
                                <i class="bi bi-clock-history me-1"></i> Historique d'audit pour la Réservation #<%= selectedResId %>
                            </h6>
                            <a href="<%= contextPath %>/reception/reservations" class="btn btn-outline-secondary btn-sm py-0">
                                <i class="bi bi-x"></i> Fermer
                            </a>
                        </div>
                        <div class="table-responsive">
                            <table class="table table-sm table-striped align-middle mb-0">
                                <thead class="table-light">
                                <tr>
                                    <th class="ps-2">Date et Heure</th>
                                    <th>Ancien Statut</th>
                                    <th>Nouveau Statut</th>
                                    <th>Modifié par</th>
                                </tr>
                                </thead>
                                <tbody>
                                <% if (auditLogs.isEmpty()) { %>
                                <tr><td colspan="4" class="text-center py-2 text-muted">Aucun historique enregistré.</td></tr>
                                <% } else {
                                    for (HistoriqueStatut h : auditLogs) { %>
                                <tr>
                                    <td class="ps-2 small"><%= h.getDateModification() %></td>
                                    <td><span class="badge bg-secondary"><%= (h.getAncienStatut() != null) ? h.getAncienStatut() : "INITIAL" %></span></td>
                                    <td><span class="badge bg-primary"><%= h.getNouveauStatut() %></span></td>
                                    <td class="small">
                                        <i class="bi bi-person me-1"></i>
                                        <%= (h.getModifiePar() != null) ? h.getModifiePar().getPrenom() + " " + h.getModifiePar().getNom() : "Système" %>
                                    </td>
                                </tr>
                                <%   }
                                } %>
                                </tbody>
                            </table>
                        </div>
                    </div>
                    <% } %>

                    <!-- Tableau Principal des Réservations -->
                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0">
                            <thead class="table-light">
                            <tr>
                                <th class="ps-3"># ID</th>
                                <th>Client</th>
                                <th>Date & Créneau</th>
                                <th>Table</th>
                                <th>Pers.</th>
                                <th>Statut</th>
                                <th class="text-end pe-3">Décision / Actions</th>
                            </tr>
                            </thead>
                            <tbody>
                            <% if (reservations == null || reservations.isEmpty()) { %>
                            <tr>
                                <td colspan="7" class="text-center py-5 text-muted">
                                    <i class="bi bi-check2-circle fs-2 d-block mb-2 text-success"></i>
                                    Aucune réservation enregistrée à traiter.
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

                                    Utilisateur client = r.getClient();
                            %>
                            <tr>
                                <td class="ps-3 fw-bold">#<%= r.getId() %></td>
                                <td>
                                    <div class="fw-semibold"><%= (client != null) ? client.getPrenom() + " " + client.getNom() : "Inconnu" %></div>
                                    <div class="small text-muted">
                                        <i class="bi bi-envelope"></i> <%= (client != null) ? client.getEmail() : "" %>
                                        <% if (client != null && client.getTelephone() != null) { %>
                                            | <i class="bi bi-telephone"></i> <%= client.getTelephone() %>
                                        <% } %>
                                    </div>
                                </td>
                                <td>
                                    <strong><%= r.getDateReservation() %></strong><br>
                                    <span class="small text-muted"><i class="bi bi-clock"></i> <%= r.getHeureDebut() %> &agrave; <%= r.getHeureFin() %></span>
                                </td>
                                <td>
                                    <span class="badge bg-light text-dark border">
                                        Table #<%= (r.getTableRestaurant() != null) ? r.getTableRestaurant().getNumero() : "-" %>
                                    </span>
                                </td>
                                <td><i class="bi bi-people text-secondary"></i> <%= r.getNombrePersonnes() %></td>
                                <td>
                                    <span class="badge <%= badgeClass %> px-2 py-1"><%= st %></span>
                                </td>
                                <td class="text-end pe-3">
                                    <% if (st == StatutReservation.EN_ATTENTE) { %>
                                    <form action="<%= contextPath %>/reception/reservations" method="post" class="d-inline">
                                        <input type="hidden" name="action" value="confirmer">
                                        <input type="hidden" name="reservationId" value="<%= r.getId() %>">
                                        <button type="submit" class="btn btn-outline-success btn-sm py-0 px-2" title="Valider">
                                            <i class="bi bi-check-lg"></i> Valider
                                        </button>
                                    </form>
                                    <button type="button" class="btn btn-outline-danger btn-sm py-0 px-2" title="Refuser"
                                            onclick="confirmerRefus('<%= r.getId() %>', '<%= (client != null) ? client.getPrenom() + " " + client.getNom() : "" %>')">
                                        <i class="bi bi-x-lg"></i> Refuser
                                    </button>
                                    <% } %>
                                    <!-- Lien corrigé historiqueId sans typo -->
                                    <a href="<%= contextPath %>/reception/reservations?historiqueId=<%= r.getId() %>"
                                       class="btn btn-outline-secondary btn-sm py-0 px-2" title="Historique d'audit">
                                        <i class="bi bi-clock-history"></i>
                                    </a>
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
                            <% if (userConnecte != null && userConnecte.getRole() == Role.ADMIN) { %>
                            <a href="<%= contextPath %>/admin/personnel" class="small text-secondary text-decoration-none">
                                <i class="bi bi-shield-lock"></i> Administration
                            </a>
                            <a href="<%= contextPath %>/admin/tables" class="small text-secondary text-decoration-none">
                                <i class="bi bi-grid-3x3-gap"></i> Tables
                            </a>
                            <% } %>
                            <a href="<%= contextPath %>/carte" class="small text-secondary text-decoration-none">
                                <i class="bi bi-book"></i> La Carte
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

<!-- Modal Confirmation Refus (Gabarit binôme) -->
<div class="modal fade" id="modalRefus" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border">
            <div class="modal-body p-4 text-center">
                <i class="bi bi-x-circle fs-1 text-danger mb-2"></i>
                <h5 class="fw-bold">Refuser la Réservation</h5>
                <p class="text-muted small">
                    Confirmez-vous le refus de la réservation #<strong id="refus_id_label"></strong> pour <strong id="refus_nom"></strong> ?
                </p>
                <form action="<%= contextPath %>/reception/reservations" method="post">
                    <input type="hidden" name="action" value="refuser">
                    <input type="hidden" id="refus_id" name="reservationId">
                    <button type="submit" class="btn btn-danger w-100 py-2 mt-3">Confirmer le refus</button>
                    <button type="button" class="btn btn-link text-secondary w-100 mt-1 text-decoration-none small" data-bs-dismiss="modal">Annuler</button>
                </form>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function confirmerRefus(id, nomClient) {
        document.getElementById('refus_id').value = id;
        document.getElementById('refus_id_label').innerText = id;
        document.getElementById('refus_nom').innerText = nomClient;
        new bootstrap.Modal(document.getElementById('modalRefus')).show();
    }
</script>
</body>
</html>