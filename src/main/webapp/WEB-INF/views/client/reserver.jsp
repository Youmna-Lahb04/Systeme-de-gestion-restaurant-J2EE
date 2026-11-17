<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="ma.ensaa.jee.entity.TableRestaurant" %>
<%@ page import="ma.ensaa.jee.entity.Utilisateur" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.LocalDate" %>
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

    String dateCherchee = (String) request.getAttribute("dateCherchee");
    String heureCherchee = (String) request.getAttribute("heureCherchee");
    Object nbObj = request.getAttribute("nbPersonnesCherche");
    String nbCherche = (nbObj != null) ? String.valueOf(nbObj) : "2";

    if (dateCherchee == null) dateCherchee = LocalDate.now().toString();
    if (heureCherchee == null) heureCherchee = "19:30";

    List<TableRestaurant> tablesDisponibles = (List<TableRestaurant>) request.getAttribute("tablesDisponibles");
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Réserver une Table - Gestion Restaurant</title>
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
                        <i class="bi bi-calendar-check fs-1 text-primary"></i>
                        <h3 class="fw-bold mt-2">Réserver une Table</h3>
                        <p class="text-muted small">Choisissez votre date et créneau horaire pour trouver une table libre</p>
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

                    <!-- Étape 1 : Formulaire de recherche (Style encadré léger comme dans menu.jsp) -->
                    <div class="p-3 bg-light rounded border mb-4">
                        <h6 class="fw-bold text-primary mb-3">
                            <i class="bi bi-search me-1"></i> 1. Trouver un créneau disponible
                        </h6>
                        <form action="<%= contextPath %>/client/reserver" method="get" class="row g-3 align-items-end">
                            <div class="col-md-4">
                                <label for="date" class="form-label small">Date souhaitée</label>
                                <input type="date" class="form-control form-control-sm" id="date" name="date"
                                       value="<%= dateCherchee %>" min="<%= LocalDate.now() %>" required>
                            </div>
                            <div class="col-md-3">
                                <label for="heure" class="form-label small">Heure d'arrivée</label>
                                <input type="time" class="form-control form-control-sm" id="heure" name="heure"
                                       value="<%= heureCherchee %>" required>
                            </div>
                            <div class="col-md-3">
                                <label for="nombrePersonnes" class="form-label small">Personnes</label>
                                <input type="number" class="form-control form-control-sm" id="nombrePersonnes" name="nombrePersonnes"
                                       value="<%= nbCherche %>" min="1" max="20" required>
                            </div>
                            <div class="col-md-2">
                                <button type="submit" class="btn btn-primary btn-sm w-100">
                                    <i class="bi bi-search"></i> Vérifier
                                </button>
                            </div>
                        </form>
                    </div>

                    <!-- Étape 2 : Tables Disponibles -->
                    <% if (tablesDisponibles != null && !tablesDisponibles.isEmpty()) { %>
                    <div class="mb-4">
                        <h6 class="fw-bold text-success mb-3">
                            <i class="bi bi-check2-circle me-1"></i> 2. Tables disponibles trouvées (<%= tablesDisponibles.size() %>)
                        </h6>
                        <div class="row g-3">
                            <% for (TableRestaurant t : tablesDisponibles) { %>
                            <div class="col-md-4 col-sm-6">
                                <div class="card h-100 border text-center p-3 shadow-sm">
                                    <div class="mb-2">
                                        <span class="badge bg-secondary fs-6 px-3 py-2">
                                            Table #<%= t.getNumero() %>
                                        </span>
                                    </div>
                                    <p class="text-muted small mb-3">
                                        <i class="bi bi-people-fill text-primary"></i> Capacité : <strong><%= t.getCapacite() %> pers.</strong>
                                    </p>
                                    <form action="<%= contextPath %>/client/reserver" method="post">
                                        <input type="hidden" name="date" value="<%= dateCherchee %>">
                                        <input type="hidden" name="heureDebut" value="<%= heureCherchee %>">
                                        <input type="hidden" name="nombrePersonnes" value="<%= nbCherche %>">
                                        <input type="hidden" name="tableId" value="<%= t.getId() %>">
                                        <button type="submit" class="btn btn-outline-success btn-sm w-100">
                                            <i class="bi bi-check2"></i> Réserver
                                        </button>
                                    </form>
                                </div>
                            </div>
                            <% } %>
                        </div>
                    </div>
                    <% } else if (request.getParameter("date") != null) { %>
                    <div class="text-center py-4 text-muted">
                        <i class="bi bi-exclamation-circle fs-3 d-block mb-2 text-warning"></i>
                        Aucune table libre pour ce créneau horaire ou pour cette capacité. Veuillez tester une autre heure.
                    </div>
                    <% } %>

                    <!-- Pied de carte -->
                    <hr class="my-4">
                    <div class="d-flex justify-content-between align-items-center">
                        <div class="d-flex gap-3">
                            <a href="<%= contextPath %>/carte" class="small text-secondary text-decoration-none">
                                <i class="bi bi-book"></i> La Carte
                            </a>
                            <a href="<%= contextPath %>/client/mes-reservations" class="small text-primary text-decoration-none fw-semibold">
                                <i class="bi bi-clock-history"></i> Mes Réservations
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

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>