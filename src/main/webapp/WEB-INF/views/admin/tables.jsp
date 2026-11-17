<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="ma.ensaa.jee.entity.TableRestaurant" %>
<%@ page import="ma.ensaa.jee.entity.Utilisateur" %>
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
    List<TableRestaurant> tables = (List<TableRestaurant>) request.getAttribute("tables");
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestion des Tables - Gestion Restaurant</title>
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
                        <i class="bi bi-grid-3x3-gap-fill fs-1 text-primary"></i>
                        <h3 class="fw-bold mt-2">Gestion des Tables</h3>
                        <p class="text-muted small">Administration et configuration de la salle du restaurant</p>
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

                    <!-- Bouton Nouvelle Table -->
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <span class="small text-muted">Liste des tables disponibles</span>
                        <button type="button" class="btn btn-primary btn-sm" data-bs-toggle="modal" data-bs-target="#modalAjoutTable">
                            <i class="bi bi-plus-circle"></i> Nouvelle Table
                        </button>
                    </div>

                    <!-- Tableau des Tables -->
                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0">
                            <thead class="table-light">
                            <tr>
                                <th class="ps-3">Numéro</th>
                                <th>Capacité d'accueil</th>
                                <th class="text-end pe-3">Actions</th>
                            </tr>
                            </thead>
                            <tbody>
                            <% if (tables == null || tables.isEmpty()) { %>
                            <tr>
                                <td colspan="3" class="text-center py-4 text-muted">
                                    <i class="bi bi-info-circle me-1"></i> Aucune table enregistrée pour le moment.
                                </td>
                            </tr>
                            <% } else {
                                for (TableRestaurant t : tables) { %>
                            <tr>
                                <td class="ps-3 fw-bold">
                                    <span class="badge bg-secondary">Table #<%= t.getNumero() %></span>
                                </td>
                                <td>
                                    <i class="bi bi-people-fill text-secondary me-1"></i>
                                    <strong><%= t.getCapacite() %></strong> personnes
                                </td>
                                <td class="text-end pe-3">
                                    <button type="button" class="btn btn-outline-secondary btn-sm py-0 px-2"
                                            title="Modifier"
                                            onclick="ouvrirModalModif('<%= t.getId() %>', '<%= t.getNumero() %>', '<%= t.getCapacite() %>')">
                                        <i class="bi bi-pencil-square"></i>
                                    </button>
                                    <button type="button" class="btn btn-outline-danger btn-sm py-0 px-2"
                                            title="Supprimer"
                                            onclick="confirmerSuppression('<%= t.getId() %>', '<%= t.getNumero() %>')">
                                        <i class="bi bi-trash"></i>
                                    </button>
                                </td>
                            </tr>
                            <%   }
                            } %>
                            </tbody>
                        </table>
                    </div>

                    <!-- Pied de carte : Navigation & Déconnexion -->
                    <hr class="my-4">
                    <div class="d-flex justify-content-between align-items-center">
                        <div class="d-flex gap-3">
                            <a href="<%= contextPath %>/admin/personnel" class="small text-secondary text-decoration-none">
                                <i class="bi bi-people"></i> Personnel
                            </a>
                            <a href="<%= contextPath %>/admin/menu" class="small text-secondary text-decoration-none">
                                <i class="bi bi-card-checklist"></i> Menu & Plats
                            </a>
                            <a href="<%= contextPath %>/reception/reservations" class="small text-secondary text-decoration-none">
                                <i class="bi bi-calendar-check"></i> Réservations
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

<!-- Modal Ajout Table -->
<div class="modal fade" id="modalAjoutTable" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border">
            <div class="modal-body p-4">
                <div class="text-center mb-3">
                    <i class="bi bi-plus-circle fs-2 text-primary"></i>
                    <h5 class="fw-bold mt-1">Ajouter une Table</h5>
                </div>
                <form action="<%= contextPath %>/admin/tables" method="post">
                    <input type="hidden" name="action" value="ajouter">
                    <div class="mb-3">
                        <label for="numero" class="form-label">Numéro de table <span class="text-danger">*</span></label>
                        <input type="number" class="form-control" id="numero" name="numero" required min="1" placeholder="Ex: 5">
                    </div>
                    <div class="mb-3">
                        <label for="capacite" class="form-label">Capacité (personnes) <span class="text-danger">*</span></label>
                        <input type="number" class="form-control" id="capacite" name="capacite" required min="1" placeholder="Ex: 4">
                    </div>
                    <button type="submit" class="btn btn-primary w-100 py-2">Créer la table</button>
                    <button type="button" class="btn btn-link text-secondary w-100 mt-1 text-decoration-none small" data-bs-dismiss="modal">Annuler</button>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- Modal Modification Table -->
<div class="modal fade" id="modalModifTable" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border">
            <div class="modal-body p-4">
                <div class="text-center mb-3">
                    <i class="bi bi-pencil-square fs-2 text-primary"></i>
                    <h5 class="fw-bold mt-1">Modifier la Table</h5>
                </div>
                <form action="<%= contextPath %>/admin/tables" method="post">
                    <input type="hidden" name="action" value="modifier">
                    <input type="hidden" id="modif_id" name="id">
                    <div class="mb-3">
                        <label for="modif_numero" class="form-label">Numéro de table <span class="text-danger">*</span></label>
                        <input type="number" class="form-control" id="modif_numero" name="numero" required min="1">
                    </div>
                    <div class="mb-3">
                        <label for="modif_capacite" class="form-label">Capacité (personnes) <span class="text-danger">*</span></label>
                        <input type="number" class="form-control" id="modif_capacite" name="capacite" required min="1">
                    </div>
                    <button type="submit" class="btn btn-primary w-100 py-2">Mettre à jour</button>
                    <button type="button" class="btn btn-link text-secondary w-100 mt-1 text-decoration-none small" data-bs-dismiss="modal">Annuler</button>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- Modal Confirmation Suppression -->
<div class="modal fade" id="modalSupprTable" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border">
            <div class="modal-body p-4 text-center">
                <i class="bi bi-trash fs-1 text-danger mb-2"></i>
                <h5 class="fw-bold">Supprimer la Table</h5>
                <p class="text-muted small">Êtes-vous sûr de vouloir supprimer la <strong>Table #<span id="suppr_num"></span></strong> ?</p>
                <form action="<%= contextPath %>/admin/tables" method="post">
                    <input type="hidden" name="action" value="supprimer">
                    <input type="hidden" id="suppr_id" name="id">
                    <button type="submit" class="btn btn-danger w-100 py-2 mt-3">Confirmer la suppression</button>
                    <button type="button" class="btn btn-link text-secondary w-100 mt-1 text-decoration-none small" data-bs-dismiss="modal">Annuler</button>
                </form>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function ouvrirModalModif(id, numero, capacite) {
        document.getElementById('modif_id').value = id;
        document.getElementById('modif_numero').value = numero;
        document.getElementById('modif_capacite').value = capacite;
        new bootstrap.Modal(document.getElementById('modalModifTable')).show();
    }
    function confirmerSuppression(id, numero) {
        document.getElementById('suppr_id').value = id;
        document.getElementById('suppr_num').innerText = numero;
        new bootstrap.Modal(document.getElementById('modalSupprTable')).show();
    }
</script>
</body>
</html>