<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="ma.ensaa.jee.entity.Menu" %>
<%@ page import="ma.ensaa.jee.entity.Plat" %>
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

    Menu menuGeneral = (Menu) request.getAttribute("menuGeneral");
    List<Plat> plats = (List<Plat>) request.getAttribute("plats");
    Categorie[] categories = (Categorie[]) request.getAttribute("categories");
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Menu & Plats - Gestion Restaurant</title>
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
                        <i class="bi bi-card-checklist fs-1 text-primary"></i>
                        <h3 class="fw-bold mt-2">Gestion du Menu & de la Carte</h3>
                        <p class="text-muted small">Administration du menu général et des plats du restaurant</p>
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

                    <!-- section menu général -->
                    <div class="p-3 bg-light rounded border mb-4 d-flex justify-content-between align-items-center">
                        <div>
                            <span class="badge bg-secondary mb-1">Configuration</span>
                            <h5 class="fw-bold mb-0">
                                <%= menuGeneral != null ? menuGeneral.getNom() : "Menu non initialisé" %>
                            </h5>
                            <p class="text-muted small mb-0">
                                <%= (menuGeneral != null && menuGeneral.getDescription() != null) ? menuGeneral.getDescription() : "Aucune description." %>
                            </p>
                        </div>
                        <div>
                            <% if (menuGeneral != null) { %>
                            <button type="button" class="btn btn-outline-secondary btn-sm"
                                    onclick="ouvrirModalModifMenu('<%= menuGeneral.getId() %>', '<%= menuGeneral.getNom() %>', '<%= menuGeneral.getDescription() != null ? menuGeneral.getDescription() : "" %>')">
                                <i class="bi bi-pencil-square"></i> Modifier
                            </button>
                            <% } else { %>
                            <button type="button" class="btn btn-success btn-sm" data-bs-toggle="modal" data-bs-target="#modalCreerMenu">
                                <i class="bi bi-plus-circle"></i> Initialiser
                            </button>
                            <% } %>
                        </div>
                    </div>

                    <!-- button nouveau plat -->
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <span class="small text-muted">Liste des plats de la carte</span>
                        <button type="button" class="btn btn-primary btn-sm px-3" data-bs-toggle="modal" data-bs-target="#modalAjoutPlat" <%= menuGeneral == null ? "disabled" : "" %>>
                            <i class="bi bi-plus-lg me-1"></i> Nouveau Plat
                        </button>
                    </div>

                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0">
                            <thead class="table-light small">
                            <tr>
                                <th>Nom du plat</th>
                                <th>Catégorie</th>
                                <th>Prix</th>
                                <th>Disponibilité</th>
                                <th class="text-end">Actions</th>
                            </tr>
                            </thead>
                            <tbody class="small">
                            <% if (plats != null && !plats.isEmpty()) {
                                for (Plat p : plats) { %>
                            <tr>
                                <td>
                                    <div class="fw-semibold"><%= p.getNom() %></div>
                                    <div class="text-muted small"><%= p.getDescription() != null ? p.getDescription() : "" %></div>
                                </td>
                                <td>
                                    <% if (p.getCategorie() == Categorie.ENTREE) { %>
                                    <span class="badge bg-info text-dark">Entrée</span>
                                    <% } else if (p.getCategorie() == Categorie.PLAT_PRINCIPAL) { %>
                                    <span class="badge bg-primary">Plat Principal</span>
                                    <% } else if (p.getCategorie() == Categorie.DESSERT) { %>
                                    <span class="badge bg-warning text-dark">Dessert</span>
                                    <% } else { %>
                                    <span class="badge bg-secondary">Boisson</span>
                                    <% } %>
                                </td>
                                <td class="fw-bold text-success"><%= String.format("%.2f", p.getPrix()) %> DH</td>
                                <td>
                                    <form action="<%= contextPath %>/admin/menu" method="post" class="d-inline">
                                        <input type="hidden" name="action" value="toggleDisponibilite">
                                        <input type="hidden" name="id" value="<%= p.getId() %>">
                                        <% if (p.isDisponible()) { %>
                                        <button type="submit" class="badge bg-success border-0" title="Cliquer pour changer">Disponible</button>
                                        <% } else { %>
                                        <button type="submit" class="badge bg-danger border-0" title="Cliquer pour changer">Épuisé</button>
                                        <% } %>
                                    </form>
                                </td>
                                <td class="text-end">
                                    <button type="button" class="btn btn-outline-secondary btn-sm py-0 px-2"
                                            onclick="ouvrirModalModifPlat('<%= p.getId() %>', '<%= p.getNom() %>', '<%= p.getDescription() != null ? p.getDescription() : "" %>', '<%= p.getPrix() %>', '<%= p.getCategorie().name() %>', <%= p.isDisponible() %>)">
                                        <i class="bi bi-pencil-square"></i>
                                    </button>
                                    <button type="button" class="btn btn-outline-danger btn-sm py-0 px-2"
                                            onclick="confirmerSuppressionPlat('<%= p.getId() %>', '<%= p.getNom() %>')">
                                        <i class="bi bi-trash"></i>
                                    </button>
                                </td>
                            </tr>
                            <%   }
                            } else { %>
                            <tr>
                                <td colspan="5" class="text-center py-4 text-muted">
                                    Aucun plat dans la carte.
                                </td>
                            </tr>
                            <% } %>
                            </tbody>
                        </table>
                    </div>

                    <hr class="my-4">
                    <div class="d-flex justify-content-between align-items-center">
                        <a href="<%= contextPath %>/admin/personnel" class="small text-secondary text-decoration-none">
                            <i class="bi bi-people"></i> Gérer le Personnel
                        </a>
                        <a href="<%= contextPath %>/carte" class="small text-primary text-decoration-none" target="_blank">
                            <i class="bi bi-eye"></i> Consulter la Carte
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

<!-- Modal créer menu -->
<div class="modal fade" id="modalCreerMenu" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border">
            <div class="modal-body p-4">
                <div class="text-center mb-3">
                    <i class="bi bi-plus-circle fs-2 text-primary"></i>
                    <h5 class="fw-bold mt-1">Créer le Menu Général</h5>
                </div>
                <form action="<%= contextPath %>/admin/menu" method="post">
                    <input type="hidden" name="action" value="creerMenu">
                    <div class="mb-3">
                        <label for="menu_nom" class="form-label">Nom du Menu <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="menu_nom" name="nom" required value="Menu Général">
                    </div>
                    <div class="mb-3">
                        <label for="menu_desc" class="form-label">Description</label>
                        <textarea class="form-control" id="menu_desc" name="description" rows="3"></textarea>
                    </div>
                    <button type="submit" class="btn btn-primary w-100 py-2">Créer le menu</button>
                    <button type="button" class="btn btn-link text-secondary w-100 mt-1 text-decoration-none small" data-bs-dismiss="modal">Annuler</button>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- Modal modif menu -->
<div class="modal fade" id="modalModifMenu" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border">
            <div class="modal-body p-4">
                <div class="text-center mb-3">
                    <i class="bi bi-pencil-square fs-2 text-primary"></i>
                    <h5 class="fw-bold mt-1">Modifier le Menu</h5>
                </div>
                <form action="<%= contextPath %>/admin/menu" method="post">
                    <input type="hidden" name="action" value="modifierMenu">
                    <input type="hidden" id="edit_menu_id" name="id">
                    <div class="mb-3">
                        <label for="edit_menu_nom" class="form-label">Nom du Menu <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="edit_menu_nom" name="nom" required>
                    </div>
                    <div class="mb-3">
                        <label for="edit_menu_desc" class="form-label">Description</label>
                        <textarea class="form-control" id="edit_menu_desc" name="description" rows="3"></textarea>
                    </div>
                    <button type="submit" class="btn btn-primary w-100 py-2">Mettre à jour</button>
                    <button type="button" class="btn btn-link text-secondary w-100 mt-1 text-decoration-none small" data-bs-dismiss="modal">Annuler</button>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- Modal ajout plat -->
<div class="modal fade" id="modalAjoutPlat" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border">
            <div class="modal-body p-4">
                <div class="text-center mb-3">
                    <i class="bi bi-egg-fried fs-2 text-primary"></i>
                    <h5 class="fw-bold mt-1">Nouveau Plat</h5>
                </div>
                <form action="<%= contextPath %>/admin/menu" method="post">
                    <input type="hidden" name="action" value="ajouterPlat">
                    <div class="mb-3">
                        <label for="plat_nom" class="form-label">Nom du Plat <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="plat_nom" name="nom" required placeholder="Ex: Tajine d'agneau">
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="plat_prix" class="form-label">Prix (DH) <span class="text-danger">*</span></label>
                            <input type="number" step="0.01" min="0.01" class="form-control" id="plat_prix" name="prix" required placeholder="80.00">
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="plat_cat" class="form-label">Catégorie <span class="text-danger">*</span></label>
                            <select class="form-select" id="plat_cat" name="categorie" required>
                                <% if (categories != null) {
                                    for (Categorie c : categories) { %>
                                <option value="<%= c.name() %>"><%= c.name() %></option>
                                <%   }
                                } %>
                            </select>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label for="plat_desc" class="form-label">Description</label>
                        <textarea class="form-control" id="plat_desc" name="description" rows="2" placeholder="Détails du plat..."></textarea>
                    </div>
                    <div class="form-check mb-3">
                        <input class="form-check-input" type="checkbox" id="plat_dispo" name="disponible" checked>
                        <label class="form-check-label small" for="plat_dispo">Disponible</label>
                    </div>
                    <button type="submit" class="btn btn-primary w-100 py-2">Ajouter à la carte</button>
                    <button type="button" class="btn btn-link text-secondary w-100 mt-1 text-decoration-none small" data-bs-dismiss="modal">Annuler</button>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- Modal modif plat -->
<div class="modal fade" id="modalModifPlat" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border">
            <div class="modal-body p-4">
                <div class="text-center mb-3">
                    <i class="bi bi-pencil-square fs-2 text-primary"></i>
                    <h5 class="fw-bold mt-1">Modifier le Plat</h5>
                </div>
                <form action="<%= contextPath %>/admin/menu" method="post">
                    <input type="hidden" name="action" value="modifierPlat">
                    <input type="hidden" id="edit_plat_id" name="id">
                    <div class="mb-3">
                        <label for="edit_plat_nom" class="form-label">Nom du Plat <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="edit_plat_nom" name="nom" required>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="edit_plat_prix" class="form-label">Prix (DH) <span class="text-danger">*</span></label>
                            <input type="number" step="0.01" min="0.01" class="form-control" id="edit_plat_prix" name="prix" required>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="edit_plat_cat" class="form-label">Catégorie <span class="text-danger">*</span></label>
                            <select class="form-select" id="edit_plat_cat" name="categorie" required>
                                <% if (categories != null) {
                                    for (Categorie c : categories) { %>
                                <option value="<%= c.name() %>"><%= c.name() %></option>
                                <%   }
                                } %>
                            </select>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label for="edit_plat_desc" class="form-label">Description</label>
                        <textarea class="form-control" id="edit_plat_desc" name="description" rows="2"></textarea>
                    </div>
                    <div class="form-check mb-3">
                        <input class="form-check-input" type="checkbox" id="edit_plat_dispo" name="disponible">
                        <label class="form-check-label small" for="edit_plat_dispo">Disponible</label>
                    </div>
                    <button type="submit" class="btn btn-primary w-100 py-2">Mettre à jour</button>
                    <button type="button" class="btn btn-link text-secondary w-100 mt-1 text-decoration-none small" data-bs-dismiss="modal">Annuler</button>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- Modal confirm suppression plat -->
<div class="modal fade" id="modalSupprPlat" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border">
            <div class="modal-body p-4 text-center">
                <i class="bi bi-trash fs-1 text-danger mb-2"></i>
                <h5 class="fw-bold">Supprimer le Plat</h5>
                <p class="text-muted small">Retirer définitivement <strong id="suppr_plat_nom"></strong> ?</p>
                <form action="<%= contextPath %>/admin/menu" method="post">
                    <input type="hidden" name="action" value="supprimerPlat">
                    <input type="hidden" id="suppr_plat_id" name="id">
                    <button type="submit" class="btn btn-danger w-100 py-2 mt-3">Supprimer</button>
                    <button type="button" class="btn btn-link text-secondary w-100 mt-1 text-decoration-none small" data-bs-dismiss="modal">Annuler</button>
                </form>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function ouvrirModalModifMenu(id, nom, desc) {
        document.getElementById('edit_menu_id').value = id;
        document.getElementById('edit_menu_nom').value = nom;
        document.getElementById('edit_menu_desc').value = desc;
        new bootstrap.Modal(document.getElementById('modalModifMenu')).show();
    }
    function ouvrirModalModifPlat(id, nom, desc, prix, cat, dispo) {
        document.getElementById('edit_plat_id').value = id;
        document.getElementById('edit_plat_nom').value = nom;
        document.getElementById('edit_plat_desc').value = desc;
        document.getElementById('edit_plat_prix').value = prix;
        document.getElementById('edit_plat_cat').value = cat;
        document.getElementById('edit_plat_dispo').checked = dispo;
        new bootstrap.Modal(document.getElementById('modalModifPlat')).show();
    }
    function confirmerSuppressionPlat(id, nom) {
        document.getElementById('suppr_plat_id').value = id;
        document.getElementById('suppr_plat_nom').innerText = nom;
        new bootstrap.Modal(document.getElementById('modalSupprPlat')).show();
    }
</script>
</body>
</html>