<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String erreur = (String) request.getAttribute("erreur");
    if (erreur == null && session.getAttribute("erreur") != null) {
        erreur = (String) session.getAttribute("erreur");
        session.removeAttribute("erreur");
    }
    String email = (String) request.getAttribute("email");
    if (email == null) {
        email = "";
    }
    String succes = null;
    if (session.getAttribute("succes") != null) {
        succes = (String) session.getAttribute("succes");
        session.removeAttribute("succes");
    }
    String contextPath = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Connexion - Gestion Restaurant</title>
    <!-- Bootstrap 5 CSS & Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
</head>
<body class="bg-light d-flex align-items-center min-vh-100">

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-12 col-sm-10 col-md-8 col-lg-5">
            <div class="card shadow-sm border">
                <div class="card-body p-4 p-md-5">
                    <div class="text-center mb-4">
                        <i class="bi bi-person-circle fs-1 text-primary"></i>
                        <h3 class="fw-bold mt-2">Connexion</h3>
                        <p class="text-muted small">Accédez à votre espace restaurant</p>
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

                    <form action="<%= contextPath %>/login" method="post">
                        <div class="mb-3">
                            <label for="email" class="form-label">Adresse Email</label>
                            <input type="email" class="form-control" id="email" name="email"
                                   value="<%= email %>" required placeholder="nom@exemple.com" autofocus>
                        </div>
                        <div class="mb-4">
                            <label for="motDePasse" class="form-label">Mot de passe</label>
                            <input type="password" class="form-control" id="motDePasse" name="motDePasse" required placeholder="••••••••">
                        </div>
                        <button type="submit" class="btn btn-primary w-100 py-2">
                            Se connecter
                        </button>
                    </form>
                    <hr class="my-4">
                    <div class="text-center">
                        <p class="small text-muted mb-1">
                            Vous n'avez pas de compte ?
                            <a href="<%= contextPath %>/inscription" class="text-decoration-none fw-semibold">S'inscrire</a>
                        </p>
                        <a href="<%= contextPath %>/carte" class="small text-secondary text-decoration-none">
                            <i class="bi bi-arrow-left"></i> Consulter la carte
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