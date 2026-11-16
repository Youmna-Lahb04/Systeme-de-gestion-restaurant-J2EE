<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="ma.ensaa.jee.entity.Utilisateur" %>
<%@ page import="ma.ensaa.jee.entity.enums.Role" %>
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
    List<Utilisateur> utilisateurs = (List<Utilisateur>) request.getAttribute("utilisateurs");
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Personnel - Gestion Restaurant</title>
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
                    <div class="text-center mb-4">
                        <i class="bi bi-people-fill fs-1 text-primary"></i>
                        <h3 class="fw-bold mt-2">Gestion du Personnel</h3>
                        <p class="text-muted small">Administration des comptes réceptionnistes et utilisateurs</p>
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

                    <!-- Bouton nouveau user  -->
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <span class="small text-muted">Liste des membres du personnel</span>
                        <button type="button" class="btn btn-primary btn-sm px-3" data-bs-toggle="modal" data-bs-target="#modalAjout">
                            <i class="bi bi-person-plus-fill me-1"></i> Nouveau réceptionniste
                        </button>
                    </div>

                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0">
                            <thead class="table-light small">
                            <tr>
                                <th>Nom & Prénom</th>
                                <th>Email</th>
                                <th>Téléphone</th>
                                <th>Rôle</th>
                                <th>Statut</th>
                                <th class="text-end">Actions</th>
                            </tr>
                            </thead>
                            <tbody class="small">
                            <% if (utilisateurs != null && !utilisateurs.isEmpty()) {
                                for (Utilisateur u : utilisateurs) { %>
                            <tr>
                                <td class="fw-semibold"><%= u.getNom() %> <%= u.getPrenom() %></td>
                                <td><%= u.getEmail() %></td>
                                <td><%= (u.getTelephone() != null && !u.getTelephone().isBlank()) ? u.getTelephone() : "-" %></td>
                                <td>
                                    <% if (u.getRole() == Role.ADMIN) { %>
                                    <span class="badge bg-danger">ADMIN</span>
                                    <% } else if (u.getRole() == Role.RECEPTIONNISTE) { %>
                                    <span class="badge bg-primary">RÉCEPTIONNISTE</span>
                                    <% } else { %>
                                    <span class="badge bg-secondary">CLIENT</span>
                                    <% } %>
                                </td>
                                <td>
                                    <% if (u.isActif()) { %>
                                    <span class="badge bg-success-subtle text-success">Actif</span>
                                    <% } else { %>
                                    <span class="badge bg-danger-subtle text-danger">Désactivé</span>
                                    <% } %>
                                </td>
                                <td class="text-end">
                                    <button type="button" class="btn btn-outline-secondary btn-sm py-0 px-2"
                                            title="Modifier"
                                            onclick="ouvrirModalModification('<%= u.getId() %>', '<%= u.getNom() %>', '<%= u.getPrenom() %>', '<%= u.getEmail() %>', '<%= u.getTelephone() != null ? u.getTelephone() : "" %>')">
                                        <i class="bi bi-pencil-square"></i>
                                    </button>
                                    <% if (u.getRole() == Role.RECEPTIONNISTE && userConnecte != null && !u.getId().equals(userConnecte.getId()) && u.isActif()) { %>
                                    <button type="button" class="btn btn-outline-danger btn-sm py-0 px-2"
                                            title="Désactiver"
                                            onclick="confirmerSuppression('<%= u.getId() %>', '<%= u.getPrenom() %> <%= u.getNom() %>')">
                                        <i class="bi bi-person-x"></i>
                                    </button>
                                    <% } %>
                                </td>
                            </tr>
                            <%   }
                            } else { %>
                            <tr>
                                <td colspan="6" class="text-center py-4 text-muted">
                                    Aucun personnel enregistré.
                                </td>
                            </tr>
                            <% } %>
                            </tbody>
                        </table>
                    </div>

                    <hr class="my-4">
                    <div class="d-flex justify-content-between align-items-center">
                        <a href="<%= contextPath %>/admin/menu" class="small text-secondary text-decoration-none">
                            <i class="bi bi-card-checklist"></i> Gérer le Menu & Plats
                        </a>
                        <a href="<%= contextPath %>/logout" class="small text-danger text-decoration-none fw-semibold">
                            <i class="bi bi-box-arrow-right"></i> Déconnexion
                        </a>
                    </div>

                </div>
            </div>
        </div>
    </div>
</div>

<!-- modal de l'ajout -->
<div class="modal fade" id="modalAjout" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border">
            <div class="modal-body p-4">
                <div class="text-center mb-3">
                    <i class="bi bi-person-plus fs-2 text-primary"></i>
                    <h5 class="fw-bold mt-1">Nouveau Réceptionniste</h5>
                </div>
                <form action="<%= contextPath %>/admin/personnel" method="post" autocomplete="off">
                    <input type="hidden" name="action" value="ajouter">
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="nom" class="form-label">Nom <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="nom" name="nom" required placeholder="Nom">
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="prenom" class="form-label">Prénom <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="prenom" name="prenom" required placeholder="Prénom">
                        </div>
                    </div>
                    <div class="mb-3">
                        <label for="email" class="form-label">Adresse Email <span class="text-danger">*</span></label>
                        <input type="email" class="form-control" id="email" name="email" required placeholder="nom@exemple.com" autocomplete="off">
                    </div>
                    <div class="mb-3">
                        <label for="telephone" class="form-label">Téléphone</label>
                        <input type="tel" class="form-control" id="telephone" name="telephone" placeholder="+212 6 00 00 00 00">
                    </div>
                    <div class="mb-3">
                        <label for="motDePasse" class="form-label">Mot de passe temporaire <span class="text-danger">*</span></label>
                        <input type="password" class="form-control" id="motDePasse" name="motDePasse" required minlength="8" placeholder="Min. 8 caractères" autocomplete="new-password">
                    </div>
                    <button type="submit" class="btn btn-primary w-100 py-2 mt-2">Enregistrer</button>
                    <button type="button" class="btn btn-link text-secondary w-100 mt-1 text-decoration-none small" data-bs-dismiss="modal">Annuler</button>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- Modal modif -->
<div class="modal fade" id="modalModif" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border">
            <div class="modal-body p-4">
                <div class="text-center mb-3">
                    <i class="bi bi-pencil-square fs-2 text-primary"></i>
                    <h5 class="fw-bold mt-1">Modifier le Personnel</h5>
                </div>
                <form action="<%= contextPath %>/admin/personnel" method="post">
                    <input type="hidden" name="action" value="modifier">
                    <input type="hidden" id="modif_id" name="id">
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="modif_nom" class="form-label">Nom <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="modif_nom" name="nom" required>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="modif_prenom" class="form-label">Prénom <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="modif_prenom" name="prenom" required>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label for="modif_email" class="form-label">Adresse Email <span class="text-danger">*</span></label>
                        <input type="email" class="form-control" id="modif_email" name="email" required>
                    </div>
                    <div class="mb-3">
                        <label for="modif_telephone" class="form-label">Téléphone</label>
                        <input type="tel" class="form-control" id="modif_telephone" name="telephone">
                    </div>
                    <button type="submit" class="btn btn-primary w-100 py-2 mt-2">Mettre à jour</button>
                    <button type="button" class="btn btn-link text-secondary w-100 mt-1 text-decoration-none small" data-bs-dismiss="modal">Annuler</button>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- Modal confirm suppression -->
<div class="modal fade" id="modalSuppr" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border">
            <div class="modal-body p-4 text-center">
                <i class="bi bi-exclamation-triangle-fill fs-1 text-danger mb-2"></i>
                <h5 class="fw-bold">Confirmer la désactivation</h5>
                <p class="text-muted small">Désactiver le compte de <strong id="suppr_nom"></strong> ?</p>
                <form action="<%= contextPath %>/admin/personnel" method="post">
                    <input type="hidden" name="action" value="supprimer">
                    <input type="hidden" id="suppr_id" name="id">
                    <button type="submit" class="btn btn-danger w-100 py-2 mt-3">Désactiver le compte</button>
                    <button type="button" class="btn btn-link text-secondary w-100 mt-1 text-decoration-none small" data-bs-dismiss="modal">Annuler</button>
                </form>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function ouvrirModalModification(id, nom, prenom, email, telephone) {
        document.getElementById('modif_id').value = id;
        document.getElementById('modif_nom').value = nom;
        document.getElementById('modif_prenom').value = prenom;
        document.getElementById('modif_email').value = email;
        document.getElementById('modif_telephone').value = telephone;
        new bootstrap.Modal(document.getElementById('modalModif')).show();
    }
    function confirmerSuppression(id, nomComplet) {
        document.getElementById('suppr_id').value = id;
        document.getElementById('suppr_nom').innerText = nomComplet;
        new bootstrap.Modal(document.getElementById('modalSuppr')).show();
    }
</script>
</body>
</html>