<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Application.GestioneAccount.UtenteBean" %>
<%@ page import="Application.GestioneGruppo.GruppoBean" %>
<%@ page import="Application.GestioneGruppo.ClubBean" %>
<%@ page import="java.util.List" %>

<%
    // Controllo Sessione
    UtenteBean utente = (UtenteBean) session.getAttribute("utente");
    if(utente == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    // Recupero Liste dalla Servlet
    List<GruppoBean> gruppiAdmin = (List<GruppoBean>) request.getAttribute("gruppiAdmin");
    List<GruppoBean> gruppiIscritto = (List<GruppoBean>) request.getAttribute("gruppiIscritto");
%>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>I Miei Gruppi | ClubConnect</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <style>
        body { background-color: #F4F6F9; }

        /* Stile Card Gruppo */
        .group-card {
            transition: transform 0.2s, box-shadow 0.2s;
            border: none;
            overflow: hidden;
        }
        .group-card:hover {
            transform: translateY(-3px);
            box-shadow: 0 10px 20px rgba(0,0,0,0.1) !important;
        }

        .card-avatar {
            width: 60px; height: 60px;
            border-radius: 12px;
            object-fit: cover;
        }

        .avatar-placeholder {
            width: 60px; height: 60px;
            border-radius: 12px;
            background-color: #e9ecef;
            display: flex; align-items: center; justify-content: center;
            font-size: 1.5rem; font-weight: bold; color: #1E3A5F;
        }

        /* Badge Personalizzati */
        .badge-club { background-color: rgba(38, 169, 188, 0.1); color: #26A9BC; border: 1px solid rgba(38, 169, 188, 0.2); }
        .badge-assoc { background-color: rgba(30, 58, 95, 0.1); color: #1E3A5F; border: 1px solid rgba(30, 58, 95, 0.2); }


        /* Sidebar & Profilo */
        .profile-card-header { background: linear-gradient(135deg, #1E3A5F, #26A9BC); height: 100px; }
        .profile-avatar { width: 80px; height: 80px; background: white; border-radius: 50%; padding: 4px; margin-top: -40px; margin-bottom: 10px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); }
        .feed-card { background: white; border: none; border-radius: 16px; box-shadow: 0 2px 6px rgba(0,0,0,0.02); margin-bottom: 1.5rem; overflow: hidden; }
        .btn-club-primary { background-color: #1E3A5F; color: white; border: none; }
        .btn-club-primary:hover { background-color: #152943; color: white; }
        .btn-club-teal { background-color: #26A9BC; color: white; border: none; }

        /* Sidebar Link Attivo */
        .nav-link.active {
            background-color: #e7f1ff;
            color: #0d6efd;
            font-weight: bold;
        }
    </style>
</head>
<body>

<nav class="navbar navbar-expand-lg bg-white shadow-sm sticky-top">
    <div class="container">
        <a class="navbar-brand d-flex align-items-center gap-2 fw-bold fs-4" href="feedServlet">
            <div class="brand-icon" style="width: 40px; height: 40px;">
                <img src="./images/logo.png" alt="Logo" class="img-fluid">
            </div>
            <span style="color: #1E3A5F">Club<span style="color: #26A9BC">Connect</span></span>
        </a>
        <div class="ms-auto d-flex align-items-center gap-3">
            <div class="dropdown">
                <a href="#" class="d-flex align-items-center text-decoration-none dropdown-toggle text-dark fw-bold" data-bs-toggle="dropdown">
                    <img src="https://ui-avatars.com/api/?name=<%= utente.getNome() %>+<%= utente.getCognome() %>&background=1E3A5F&color=fff" class="rounded-circle me-2" width="35" height="35">
                    <span class="d-none d-lg-inline"><%= utente.getNome() %> <%= utente.getCognome() %></span>
                </a>
                <ul class="dropdown-menu dropdown-menu-end shadow-sm border-0">
                    <li>
                        <a class="dropdown-item d-flex align-items-center gap-2" href="ModificaDatiServlet">
                            <i class="fa-solid fa-user-circle text-primary"></i> Dati utente
                        </a>
                    </li>
                    <li><hr class="dropdown-divider opacity-25"></li>
                    <li>
                        <a class="dropdown-item d-flex align-items-center gap-2 text-danger" href="LogoutServlet">
                            <i class="fa-solid fa-right-from-bracket"></i> Esci
                        </a>
                    </li>
                </ul>
            </div>
        </div>
    </div>
</nav>

<div class="container py-4">

    <div class="row g-4">

        <div class="row g-4">

            <div class="col-lg-3 d-none d-lg-block">
                <div class="sidebar-sticky">
                    <div class="feed-card text-center pb-3">
                        <div class="profile-card-header"></div>
                        <img src="https://ui-avatars.com/api/?name=<%= utente.getNome() %>+<%= utente.getCognome() %>&background=random" class="profile-avatar shadow-sm">
                        <h5 class="fw-bold mb-0 text-primary"><%= utente.getNome() %> <%= utente.getCognome() %></h5>
                        <p class="text-muted small">@<%= utente.getUsername() %></p>
                        </div>
                    <div class="feed-card p-3">
                        <ul class="nav flex-column gap-2">
                            <li class="nav-item"><a href="feedServlet" class="nav-link d-flex align-items-center gap-3 text-secondary p-2"><i class="fa-solid fa-house"></i> Home</a></li>
                            <li class="nav-item"><a href="VisualizzaIscrizioniGruppiServlet" class="nav-link active d-flex align-items-center gap-3 text-primary fw-bold rounded bg-light p-2"><i class="fa-solid fa-users"></i> I Miei Gruppi</a></li>
                            <li class="nav-item"><a href="EsploraGruppiServlet" class="nav-link d-flex align-items-center gap-3 text-secondary p-2"><i class="fa-regular fa-compass me-2"></i>Esplora Gruppi</a></li>
                            <li class="nav-item"><a href="VisualizzaCalendarioEventiServlet" class="nav-link d-flex align-items-center gap-3 text-secondary p-2"><i class="fa-solid fa-calendar-days"></i> Calendario Eventi</a></li>
                        </ul>
                    </div>
                    <a href="crea_gruppo.jsp" class="btn btn-club-teal w-100 py-3 rounded-4 shadow-sm fw-bold">
                        <i class="fa-solid fa-plus me-2"></i> Crea Nuovo Gruppo
                    </a>
                </div>
            </div>

        <div class="col-lg-9">

            <div class="d-flex align-items-center justify-content-between mb-3">
                <h4 class="fw-bold text-dark mb-0"><i class="fa-solid fa-crown text-warning me-2"></i>Gruppi che Gestisci</h4>
                <a href="crea_gruppo.jsp" class="btn btn-sm btn-club-primary rounded-pill px-3 shadow-sm">
                    <i class="fa-solid fa-plus"></i> Nuovo
                </a>
            </div>

            <div class="row g-3 mb-5">
                <% if (gruppiAdmin == null || gruppiAdmin.isEmpty()) { %>
                <div class="col-12">
                    <div class="text-center py-5 border rounded-4 bg-white border-dashed">
                        <i class="fa-regular fa-folder-open fs-1 text-muted mb-3 opacity-50"></i>
                        <p class="text-muted">Non gestisci ancora nessun gruppo.</p>
                        <a href="crea_gruppo.jsp" class="fw-bold text-decoration-none">Creane uno ora!</a>
                    </div>
                </div>
                <% } else {
                    for (GruppoBean g : gruppiAdmin) {
                        boolean isClub = (g instanceof ClubBean);
                %>
                <div class="col-md-6">
                    <div class="card group-card h-100 shadow-sm rounded-4 p-3 bg-white">
                        <div class="d-flex align-items-start gap-3">
                            <% if (g.getLogo() != null && !g.getLogo().isEmpty()) { %>
                            <img src="<%= g.getLogo() %>" class="card-avatar">
                            <% } else { %>
                            <div class="avatar-placeholder"><%= g.getNome().substring(0,1) %></div>
                            <% } %>

                            <div class="flex-grow-1">
                                <div class="d-flex justify-content-between align-items-start">
                                    <h6 class="fw-bold mb-1 text-truncate" style="max-width: 150px;"><%= g.getNome() %></h6>
                                    <span class="badge <%= isClub ? "badge-club" : "badge-assoc" %> rounded-pill">
                                        <%= isClub ? "Club" : "Associazione" %>
                                    </span>
                                </div>
                                <p class="small text-muted mb-2 text-truncate"><%= g.getSlogan() != null ? g.getSlogan() : "Nessuno slogan" %></p>
                                <div class="d-flex align-items-center justify-content-between">
                                    <span class="small text-secondary fw-bold"><i class="fa-solid fa-tag me-1"></i><%= g.getSettore() %></span>
                                    <a href="VisualizzaGruppoServlet?id=<%= g.getId_gruppo() %>" class="btn btn-sm btn-outline-dark rounded-pill px-3" style="font-size: 0.8rem;">Gestisci</a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <% }} %>
            </div>

            <h4 class="fw-bold text-dark mb-3"><i class="fa-solid fa-address-card text-primary me-2"></i>Le tue Iscrizioni</h4>

            <div class="row g-3">
                <% if (gruppiIscritto == null || gruppiIscritto.isEmpty()) { %>
                <div class="col-12">
                    <div class="alert alert-light border rounded-4 text-center">
                        Non sei iscritto a nessun gruppo al momento. <a href="feedServlet" class="fw-bold">Esplora la Feed</a> per trovarne uno!
                    </div>
                </div>
                <% } else {
                    for (GruppoBean g : gruppiIscritto) {
                        boolean isClub = (g instanceof ClubBean);
                %>
                <div class="col-md-4">
                    <div class="card group-card h-100 shadow-sm rounded-4">
                        <div style="height: 60px; background: linear-gradient(to right, #eef2f3, #8e9eab); border-radius: 1rem 1rem 0 0;"></div>

                        <div class="card-body text-center pt-0 position-relative">
                            <div class="position-absolute top-0 start-50 translate-middle">
                                <% if (g.getLogo() != null && !g.getLogo().isEmpty()) { %>
                                <img src="<%= g.getLogo() %>" class="rounded-circle border border-3 border-white shadow-sm" style="width: 60px; height: 60px; object-fit: cover;">
                                <% } else { %>
                                <div class="rounded-circle border border-3 border-white shadow-sm bg-light d-flex align-items-center justify-content-center mx-auto fs-4 fw-bold text-secondary" style="width: 60px; height: 60px;">
                                    <%= g.getNome().substring(0,1) %>
                                </div>
                                <% } %>
                            </div>

                            <div class="mt-4 pt-2">
                                <h6 class="fw-bold mb-1 text-truncate"><%= g.getNome() %></h6>
                                <span class="badge <%= isClub ? "badge-club" : "badge-assoc" %> mb-2"><%= g.getSettore() %></span>
                                <p class="small text-muted mb-3 text-truncate" style="font-size: 0.8rem;">
                                    <%= g.getSlogan() != null ? g.getSlogan() : "" %>
                                </p>
                                <a href="VisualizzaGruppoServlet?id=<%= g.getId_gruppo() %>" class="btn btn-sm btn-club-primary w-100 rounded-pill">Accedi</a>
                            </div>
                        </div>
                    </div>
                </div>
                <% }} %>
            </div>
        </div>
        </div> </div> </div> <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
