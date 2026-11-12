<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String erreur = (String) request.getAttribute("erreur");
    String nom = (String) request.getAttribute("nom");
    String prenom = (String) request.getAttribute("prenom");
    String email = (String) request.getAttribute("email");
    String telephone = (String) request.getAttribute("telephone");

    if (nom == null) nom = "";
    if (prenom == null) prenom = "";
    if (email == null) email = "";
    if (telephone == null) telephone = "";

    String contextPath = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Inscription - Gestion Restaurant</title>
    <!-- Bootstrap 5 CSS & Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
</head>
<body class="bg-light d-flex align-items-center min-vh-100">

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-12 col-sm-10 col-md-8 col-lg-6">
            <div class="card shadow-sm border">
                <div class="card-body p-4 p-md-5">
                    <div class="text-center mb-4">
                        <i class="bi bi-person-plus fs-1 text-primary"></i>
                        <h3 class="fw-bold mt-2">Créer un compte</h3>
                        <p class="text-muted small">Remplissez le formulaire pour créer votre compte</p>
                    </div>

                    <% if (erreur != null && !erreur.isBlank()) { %>
                    <div class="alert alert-danger alert-dismissible fade show small" role="alert">
                        <i class="bi bi-exclamation-triangle-fill me-1"></i> <%= erreur %>
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Fermer"></button>
                    </div>
                    <% } %>

                    <form action="<%= contextPath %>/inscription" method="post">
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="nom" class="form-label">Nom <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="nom" name="nom"
                                       value="<%= nom %>" required placeholder="Votre nom" autofocus>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label for="prenom" class="form-label">Prénom <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="prenom" name="prenom"
                                       value="<%= prenom %>" required placeholder="Votre prénom">
                            </div>
                        </div>

                        <div class="mb-3">
                            <label for="email" class="form-label">Adresse Email <span class="text-danger">*</span></label>
                            <input type="email" class="form-control" id="email" name="email"
                                   value="<%= email %>" required placeholder="nom@exemple.com">
                        </div>

                        <div class="mb-3">
                            <label for="telephone" class="form-label">Téléphone</label>
                            <input type="tel" class="form-control" id="telephone" name="telephone"
                                   value="<%= telephone %>" placeholder="+212 6 00 00 00 00">
                        </div>

                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="motDePasse" class="form-label">Mot de passe <span class="text-danger">*</span></label>
                                <input type="password" class="form-control" id="motDePasse" name="motDePasse"
                                       required minlength="8" placeholder="Min. 8 caractères">
                            </div>
                            <div class="col-md-6 mb-4">
                                <label for="confirmationMotDePasse" class="form-label">Confirmation <span class="text-danger">*</span></label>
                                <input type="password" class="form-control" id="confirmationMotDePasse" name="confirmationMotDePasse"
                                       required minlength="8" placeholder="Confirmer">
                            </div>
                        </div>

                        <button type="submit" class="btn btn-primary w-100 py-2">
                            Créer mon compte
                        </button>
                    </form>

                    <hr class="my-4">

                    <div class="text-center">
                        <p class="small text-muted mb-0">
                            Vous avez déjà un compte ?
                            <a href="<%= contextPath %>/login" class="text-decoration-none fw-semibold">Se connecter</a>
                        </p>
                    </div>

                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>