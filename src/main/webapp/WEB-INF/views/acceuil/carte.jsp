<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="ma.ensaa.jee.entity.Menu" %>
<%@ page import="ma.ensaa.jee.entity.Plat" %>
<%@ page import="ma.ensaa.jee.entity.Utilisateur" %>
<%@ page import="ma.ensaa.jee.entity.enums.Categorie" %>
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

    Utilisateur userConnecte = (Utilisateur) session.getAttribute("user");
    Menu menuGeneral = (Menu) request.getAttribute("menuGeneral");
    List<Plat> plats = (List<Plat>) request.getAttribute("plats");
    String selectedCategorie = (String) request.getAttribute("selectedCategorie");
    if (selectedCategorie == null) selectedCategorie = "ALL";
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>La Carte - Gestion Restaurant</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
</head>
<body class="bg-light py-5">

<div class="container">
    <div class="row justify-content-center">
        <div class="col-12 col-lg-10 col-xl-9">
            <div class="card shadow-sm border">
                <div class="card-body p-4 p-md-5">
                    <div class="text-center mb-4">
                        <i class="bi bi-egg-fried fs-1 text-primary"></i>
                        <h3 class="fw-bold mt-2">
                            <%= menuGeneral != null ? menuGeneral.getNom() : "Carte du Restaurant" %>
                        </h3>
                        <p class="text-muted small">
                            <%= (menuGeneral != null && menuGeneral.getDescription() != null && !menuGeneral.getDescription().isBlank())
                                    ? menuGeneral.getDescription()
                                    : "Découvrez nos spécialités préparées chaque jour avec des ingrédients frais" %>
                        </p>
                    </div>

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

                    <!-- filtres par catégorie -->
                    <div class="d-flex justify-content-center flex-wrap gap-2 mb-4">
                        <a href="<%= contextPath %>/carte"
                           class="btn btn-sm <%= "ALL".equals(selectedCategorie) ? "btn-primary" : "btn-outline-secondary" %>">
                            Tous
                        </a>
                        <a href="<%= contextPath %>/carte?categorie=ENTREE"
                           class="btn btn-sm <%= "ENTREE".equals(selectedCategorie) ? "btn-primary" : "btn-outline-secondary" %>">
                            Entrées
                        </a>
                        <a href="<%= contextPath %>/carte?categorie=PLAT_PRINCIPAL"
                           class="btn btn-sm <%= "PLAT_PRINCIPAL".equals(selectedCategorie) ? "btn-primary" : "btn-outline-secondary" %>">
                            Plats Principaux
                        </a>
                        <a href="<%= contextPath %>/carte?categorie=DESSERT"
                           class="btn btn-sm <%= "DESSERT".equals(selectedCategorie) ? "btn-primary" : "btn-outline-secondary" %>">
                            Desserts
                        </a>
                        <a href="<%= contextPath %>/carte?categorie=BOISSON"
                           class="btn btn-sm <%= "BOISSON".equals(selectedCategorie) ? "btn-primary" : "btn-outline-secondary" %>">
                            Boissons
                        </a>
                    </div>

                    <!-- liste des plats -->
                    <div class="row g-3">
                        <% if (plats != null && !plats.isEmpty()) {
                            for (Plat p : plats) { %>
                        <div class="col-12 col-md-6">
                            <div class="border rounded p-3 h-100 d-flex flex-column justify-content-between bg-white">
                                <div>
                                    <div class="d-flex justify-content-between align-items-start mb-1">
                                        <span class="fw-bold text-dark"><%= p.getNom() %></span>
                                        <span class="badge bg-success-subtle text-success fw-bold">
                                                <%= String.format("%.2f", p.getPrix()) %> DH
                                            </span>
                                    </div>
                                    <p class="text-muted small mb-2">
                                        <%= (p.getDescription() != null && !p.getDescription().isBlank()) ? p.getDescription() : "Délicieuse spécialité de la maison." %>
                                    </p>
                                </div>
                                <div class="d-flex justify-content-between align-items-center pt-2 border-top">
                                        <span class="badge bg-light text-secondary border">
                                            <%= p.getCategorie().name().replace("_", " ") %>
                                        </span>
                                    <span class="text-success small">
                                            <i class="bi bi-check-circle-fill me-1"></i>Disponible
                                        </span>
                                </div>
                            </div>
                        </div>
                        <%   }
                        } else { %>
                        <div class="col-12 text-center py-4 text-muted">
                            <i class="bi bi-search fs-3 d-block mb-2"></i>
                            Aucun plat disponible dans cette catégorie.
                        </div>
                        <% } %>
                    </div>

                                     <hr class="my-4">
                                     <div class="text-center">
                                         <% if (userConnecte != null) { %>
                                         <p class="small text-muted mb-2">
                                             Connecté en tant que <strong><%= userConnecte.getPrenom() %> <%= userConnecte.getNom() %></strong>
                                             (<%= userConnecte.getRole() %>)
                                         </p>

                                         <!-- Actions Réservations pour client ou utilisateur connecté -->
                                         <div class="d-flex justify-content-center align-items-center gap-2 mb-3">
                                             <a href="<%= contextPath %>/client/reserver" class="btn btn-outline-primary btn-sm">
                                                 <i class="bi bi-calendar-check me-1"></i> Réserver une table
                                             </a>
                                             <a href="<%= contextPath %>/client/mes-reservations" class="btn btn-outline-secondary btn-sm">
                                                 <i class="bi bi-clock-history me-1"></i> Mes Réservations
                                             </a>
                                         </div>

                                         <% if (userConnecte.getRole() == ma.ensaa.jee.entity.enums.Role.ADMIN) { %>
                                         <a href="<%= contextPath %>/admin/personnel" class="text-decoration-none fw-semibold small me-3">
                                             <i class="bi bi-gear-fill"></i> Espace Admin
                                         </a>
                                         <% } %>
                                         <a href="<%= contextPath %>/logout" class="small text-danger text-decoration-none">
                                             <i class="bi bi-box-arrow-right"></i> Se déconnecter
                                         </a>

                                         <% } else { %>

                                         <p class="small text-muted mb-2">
                                             Vous souhaitez passer une commande ou réserver une table ?
                                         </p>
                                         <div class="d-flex justify-content-center gap-2 mb-2">
                                             <a href="<%= contextPath %>/login" class="btn btn-primary btn-sm">
                                                 <i class="bi bi-box-arrow-in-right me-1"></i> Se connecter
                                             </a>
                                             <a href="<%= contextPath %>/inscription" class="btn btn-outline-secondary btn-sm">
                                                 <i class="bi bi-person-plus me-1"></i> S'inscrire
                                             </a>
                                         </div>

                                         <% } %>
                                     </div>

                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>