<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Application.GestioneAccount.UtenteBean" %>
<%@ page import="Application.GestioneGruppo.GruppoBean" %>
<%@ page import="java.util.List" %>

<%
    UtenteBean utente = (UtenteBean) session.getAttribute("utente");
    if(utente == null) { response.sendRedirect("login.jsp"); return; }
    List<GruppoBean> gruppi = (List<GruppoBean>) request.getAttribute("gruppiEsplora");
%>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Esplora Gruppi | ClubConnect</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <style>
        body { background-color: #F4F6F9; }
        .explore-card { transition: transform 0.2s; border: none; }
        .explore-card:hover { transform: translateY(-5px); box-shadow: 0 10px 20px rgba(0,0,0,0.1); }
        .group-cover-sm { height: 80px; background: linear-gradient(45deg, #1E3A5F, #26A9BC); border-radius: 12px 12px 0 0; }
        .group-avatar-sm { width: 50px; height: 50px; object-fit: cover; border: 3px solid white; margin-top: -25px; background: white; }

        /* Sidebar & Profilo */
        .profile-card-header { background: linear-gradient(135deg, #1E3A5F, #26A9BC); height: 100px; }
        .profile-avatar { width: 80px; height: 80px; background: white; border-radius: 50%; padding: 4px; margin-top: -40px; margin-bottom: 10px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); }
        .feed-card { background: white; border: none; border-radius: 16px; box-shadow: 0 2px 6px rgba(0,0,0,0.02); margin-bottom: 1.5rem; overflow: hidden; }
        .btn-club-primary { background-color: #1E3A5F; color: white; border: none; }
        .btn-club-primary:hover { background-color: #152943; color: white; }
        .btn-club-teal { background-color: #26A9BC; color: white; border: none; }
    </style>
</head>
<body>

<nav class="navbar navbar-expand-lg navbar-main py-2">
    <div class="container">
        <a href="#" class="d-inline-flex align-items-center gap-2 text-decoration-none">
            <div class="brand-icon" style="width: 40px; height: 40px;">
                <img src="./images/logo.png" alt="Logo" class="img-fluid">
            </div>
            <span class="fs-4 fw-bold" style="color: #1E3A5F;">Club<span style="color: #26A9BC;">Connect</span></span>
        </a>
        <div class="ms-auto d-flex align-items-center gap-3">
            <div class="dropdown">
                <a href="feedServlet" class="d-flex align-items-center text-decoration-none dropdown-toggle text-dark fw-bold" data-bs-toggle="dropdown">
                    <img src="https://ui-avatars.com/api/?name=<%= utente.getNome() %>+<%= utente.getCognome() %>&background=1E3A5F&color=fff" class="rounded-circle me-2" width="35" height="35">
                    <span class="d-none d-lg-inline"><%= utente.getNome() %> <%= utente.getCognome() %></span>
                </a>
                <ul class="dropdown-menu dropdown-menu-end shadow-sm border-0">
                    <li><a class="dropdown-item" href="LogoutServlet">Esci</a></li>
                </ul>
            </div>
        </div>
    </div>
</nav>

<div class="container py-4">

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
                            <li class="nav-item"><a href="VisualizzaIscrizioniGruppiServlet" class="nav-link d-flex align-items-center gap-3 text-secondary p-2"><i class="fa-solid fa-users"></i> I Miei Gruppi</a></li>
                            <li class="nav-item"><a href="EsploraGruppiServlet" class="nav-link active d-flex align-items-center gap-3 text-primary fw-bold rounded bg-light p-2"><i class="fa-regular fa-compass me-2"></i>Esplora Gruppi</a></li>
                            <li class="nav-item"><a href="VisualizzaCalendarioEventiServlet" class="nav-link d-flex align-items-center gap-3 text-secondary p-2"><i class="fa-solid fa-calendar-days"></i> Calendario Eventi</a></li>
                        </ul>
                    </div>
                    <a href="crea_gruppo.jsp" class="btn btn-club-teal w-100 py-3 rounded-4 shadow-sm fw-bold">
                        <i class="fa-solid fa-plus me-2"></i> Crea Nuovo Gruppo
                    </a>
                </div>
            </div>

        <div class="col-lg-9">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h3 class="fw-bold text-dark mb-0">Scopri nuove community</h3>
            </div>

            <div class="row g-3">
                <% if (gruppi == null || gruppi.isEmpty()) { %>
                <div class="col-12 text-center py-5">
                    <i class="fa-solid fa-check-circle fs-1 text-success mb-3"></i>
                    <h5>Wow! Sei iscritto a tutto!</h5>
                    <p class="text-muted">Al momento non ci sono nuovi gruppi disponibili.</p>
                </div>
                <% } else {
                    for (GruppoBean g : gruppi) { %>

                <div class="col-md-4">
                    <div class="card explore-card h-100 shadow-sm rounded-4">
                        <div class="group-cover-sm"></div>
                        <div class="card-body text-center pt-0">
                            <div class="d-flex justify-content-center">
                                <% if(g.getLogo()!=null && !g.getLogo().isEmpty()) { %>
                                <img src="<%= g.getLogo() %>" class="group-avatar-sm rounded-circle">
                                <% } else { %>
                                <div class="group-avatar-sm rounded-circle d-flex align-items-center justify-content-center fw-bold text-primary fs-5">
                                    <%= g.getNome().substring(0,1) %>
                                </div>
                                <% } %>
                            </div>

                            <h6 class="fw-bold mt-2 mb-1 text-truncate"><%= g.getNome() %></h6>
                            <span class="badge bg-light text-secondary border mb-2"><%= g.getSettore() %></span>
                            <p class="small text-muted mb-3 text-truncate"><%= g.getSlogan()!=null?g.getSlogan():"" %></p>

                            <div class="d-grid gap-2">
                                <a href="VisualizzaGruppoServlet?id=<%= g.getId_gruppo() %>" class="btn btn-outline-dark btn-sm rounded-pill">Dettagli</a>
                                <form action="IscrizioneGruppoServlet" method="POST">
                                    <input type="hidden" name="idGruppo" value="<%= g.getId_gruppo() %>">
                                    <button type="submit" class="btn btn-club-primary btn-sm w-100 rounded-pill">Iscriviti</button>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
                <% }} %>
            </div>
        </div>
    </div>
</div>
</body>
</html>
