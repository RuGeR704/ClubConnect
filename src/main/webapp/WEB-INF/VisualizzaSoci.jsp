<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Application.GestioneGruppo.GruppoBean" %>
<%@ page import="Application.GestioneGruppo.ClubBean" %>
<%@ page import="Application.GestioneAccount.UtenteBean" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%
    // RECUPERO DATI DAL CONTROLLER (SERVLET)
    GruppoBean gruppo = (GruppoBean) request.getAttribute("gruppo");
    List<UtenteBean> listaSoci = (List<UtenteBean>) request.getAttribute("listaSoci");
    Map<Integer, Boolean> mappaGestori = (Map<Integer, Boolean>) request.getAttribute("mappaGestori");

    // Mappa per sapere lo stato pagamenti (Solo per Club): Key=IdUtente, Value=Boolean (True=Pagato/Regolare)
    // La servlet deve passare questa mappa se è un club
    Map<Integer, Boolean> statoPagamenti = (Map<Integer, Boolean>) request.getAttribute("statoPagamenti");

    boolean isClub = (gruppo instanceof ClubBean);

    // Gestione null pointer per evitare crash se la lista è vuota
    if(listaSoci == null) {
        listaSoci = new java.util.ArrayList<>();
    }
%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Soci - <%= gruppo != null ? gruppo.getNome() : "Gruppo" %> | ClubConnect</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <style>
        :root {
            --sidebar-width: 260px;
            --club-dark: #1E3A5F;
            --club-teal: #26A9BC;
        }

        body { background-color: #f8f9fa; }

        /* SIDEBAR STESSA DELLO SCHEMA DATO */
        .dashboard-sidebar {
            width: var(--sidebar-width);
            height: 100vh;
            position: fixed;
            top: 0; left: 0;
            background: white;
            border-right: 1px solid #eee;
            padding: 20px;
            z-index: 1000;
        }

        .main-content {
            margin-left: var(--sidebar-width);
            padding: 30px;
        }

        /* STILI SPECIFICI PER LA TABELLA SOCI */
        .table-soci thead th {
            background-color: #f8f9fa;
            color: #6c757d;
            font-weight: 600;
            text-transform: uppercase;
            font-size: 0.8rem;
            border-bottom: 2px solid #eee;
        }

        .avatar-initials {
            width: 40px; height: 40px;
            border-radius: 50%;
            object-fit: cover;
        }

        .status-badge {
            padding: 5px 12px;
            border-radius: 20px;
            font-size: 0.75rem;
            font-weight: 600;
        }
        .status-active { background-color: #d1e7dd; color: #0f5132; } /* Verde */
        .status-expired { background-color: #f8d7da; color: #842029; } /* Rosso */

        .nav-pills .nav-link.active {
            background-color: var(--club-dark);
        }
        .nav-pills .nav-link { color: #555; font-weight: 500; }
        .nav-pills .nav-link:hover { background-color: #f1f3f5; }

        .btn-club-primary {
            background-color: var(--club-teal);
            color: white;
            border: none;
        }
        .btn-club-primary:hover {
            background-color: #1d8a9b;
            color: white;
        }
    </style>
</head>
<body>

<div class="dashboard-sidebar d-flex flex-column">
    <a href="feedServlet" class="d-flex align-items-center gap-2 mb-5 text-decoration-none">
        <img src="./images/logo.png" width="35" alt="Logo">
        <span class="fs-4 fw-bold" style="color: #1E3A5F;">Club<span style="color: #26A9BC;">Connect</span></span>
    </a>

    <div class="d-flex align-items-center gap-3 mb-4 p-3 rounded-3 bg-light">
        <img src="<%= (gruppo != null && gruppo.getLogo() != null) ? gruppo.getLogo() : "https://via.placeholder.com/40" %>"
             class="rounded-circle object-fit-cover" width="40" height="40">
        <div class="overflow-hidden">
            <h6 class="mb-0 text-truncate fw-bold"><%= gruppo != null ? gruppo.getNome() : "Gruppo" %></h6>
            <small class="text-muted">Gestione</small>
        </div>
    </div>

    <ul class="nav nav-pills flex-column gap-2">
        <li class="nav-item">
            <a href="DashboardServlet?id=<%= gruppo.getId_gruppo() %>" class="nav-link"><i class="fa-solid fa-chart-line me-2 w-25"></i> Panoramica</a>
        </li>
        <li class="nav-item">
            <a href="#" class="nav-link active"><i class="fa-solid fa-users me-2 w-25"></i> Soci & Tessere</a>
        </li>
        <li class="nav-item mt-3">
            <a href="VisualizzaGruppoServlet?id=<%= gruppo != null ? gruppo.getId_gruppo() : 0 %>" class="nav-link text-primary border border-primary text-center">
                <i class="fa-solid fa-arrow-left me-2"></i> Torna al Gruppo
            </a>
        </li>
    </ul>

    <div class="mt-auto text-center small text-muted">
        &copy; 2026 ClubConnect
    </div>
</div>

<div class="main-content">

    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h3 class="fw-bold text-dark">Gestione Soci</h3>
            <p class="text-muted mb-0">Visualizza e gestisci gli iscritti al <%= isClub ? "Club" : "Gruppo" %>.</p>
        </div>
    </div>

    <div class="card border-0 shadow-sm rounded-4 p-0 overflow-hidden">
        <div class="p-4 border-bottom bg-white d-flex justify-content-between align-items-center">
            <div class="input-group" style="max-width: 300px;">
                <span class="input-group-text bg-light border-end-0"><i class="fa-solid fa-magnifying-glass text-muted"></i></span>
                <input type="text" class="form-control bg-light border-start-0" placeholder="Cerca socio...">
            </div>
            <div>
                <span class="text-muted small me-2">Totale soci: <strong><%= listaSoci.size() %></strong></span>
            </div>
        </div>

        <div class="table-responsive">
            <table class="table table-hover table-soci align-middle mb-0">
                <thead>
                <tr>
                    <th class="ps-4">Socio</th>
                    <th>Email / Contatti</th>
                    <th>Ruolo</th>

                    <%-- COLONNA EXTRA SOLO SE È UN CLUB --%>
                    <% if (isClub) { %>
                    <th class="text-center">Stato Pagamento</th>
                    <% } %>

                    <th class="text-end pe-4">Azioni</th>
                </tr>
                </thead>
                <tbody>
                <% if (listaSoci.isEmpty()) { %>
                <tr>
                    <td colspan="<%= isClub ? 5 : 4 %>" class="text-center py-5 text-muted">
                        <i class="fa-regular fa-folder-open fa-2x mb-3 d-block"></i>
                        Nessun socio trovato in questo gruppo.
                    </td>
                </tr>
                <% } else {
                    for (UtenteBean socio : listaSoci) {
                        boolean isGestore = false;
                        if(mappaGestori != null && mappaGestori.containsKey(socio.getId_utente())) {
                            isGestore = mappaGestori.get(socio.getId_utente());
                        }
                %>
                <tr>
                    <td class="ps-4">
                        <div class="d-flex align-items-center gap-3">
                            <img src="https://ui-avatars.com/api/?name=<%= socio.getNome() %>+<%= socio.getCognome() %>&background=<%= isGestore ? "1E3A5F&color=fff" : "random" %>"
                                 class="avatar-initials shadow-sm" alt="<%= socio.getNome() %>">
                            <div>
                                <div class="fw-bold text-dark"><%= socio.getNome() %> <%= socio.getCognome() %></div>
                                <div class="small text-muted">ID: #<%= socio.getId_utente() %></div>
                            </div>
                        </div>
                    </td>
                    <td>
                        <div class="text-dark"><%= socio.getEmail() %></div>
                        <div class="small text-muted"><i class="fa-solid fa-phone me-1"></i> <%= socio.getCellulare() != null ? socio.getCellulare() : "--" %></div>
                    </td>
                    <td>
                        <% if (isGestore) { %>
                        <span class="badge bg-primary text-white border-0" style="background-color: var(--club-dark) !important;">Gestore</span>
                        <% } else { %>
                        <span class="badge bg-light text-dark border">Membro</span>
                        <% } %>
                    </td>

                    <% if (isClub) { %>
                    <td class="text-center">
                        <% if (!isGestore) {
                            boolean isPagato = (statoPagamenti != null && statoPagamenti.getOrDefault(socio.getId_utente(), false));
                        %>
                        <span class="status-badge <%= isPagato ? "status-active" : "status-expired" %>">
                    <i class="fa-solid <%= isPagato ? "fa-check-circle" : "fa-triangle-exclamation" %> me-1"></i>
                    <%= isPagato ? "Regolare" : "Scaduto" %>
                </span>
                        <% } %>
                    </td>
                    <% } %>

                    <td class="text-end pe-4">
                        <% if (!isGestore) { %>
                        <form action="KickUtenteServlet" method="POST" style="display:inline;"
                              onsubmit="return confirm('Sei sicuro di voler espellere questo socio?');">
                            <input type="hidden" name="idUtente" value="<%= socio.getId_utente() %>">
                            <input type="hidden" name="idGruppo" value="<%= gruppo.getId_gruppo() %>">
                            <button type="submit" class="btn btn-sm btn-outline-danger border-0 fw-bold">
                                <i class="fa-solid fa-user-xmark me-1"></i> Espelli
                            </button>
                        </form>
                        <% } else { %>
                        <small class="text-muted italic">Protetto</small>
                        <% } %>
                    </td>
                </tr>
                <% } } %>
                </tbody>
            </table>
        </div>

        <div class="p-3 border-top bg-light d-flex justify-content-end">
            <nav>
                <ul class="pagination pagination-sm mb-0">
                    <li class="page-item disabled"><a class="page-link" href="#">Precedente</a></li>
                    <li class="page-item active"><a class="page-link bg-dark border-dark" href="#">1</a></li>
                    <li class="page-item"><a class="page-link text-dark" href="#">2</a></li>
                    <li class="page-item"><a class="page-link text-dark" href="#">Successivo</a></li>
                </ul>
            </nav>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>