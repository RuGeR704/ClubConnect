<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="Application.GestioneAccount.UtenteBean" %>
<%@ page import="Application.GestioneGruppo.GruppoBean" %>
<%@ page import="Application.GestioneComunicazioni.ComunicazioniBean" %>
<%@ page import="Application.GestioneEventi.EventoBean" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.LocalDateTime" %>

<%
    // Recupero Utente
    UtenteBean utente = (UtenteBean) session.getAttribute("utente");
    if(utente == null) { response.sendRedirect("login.jsp"); return; }

    Boolean hasIscrizioniObj = (Boolean) request.getAttribute("hasIscrizioni");
    boolean hasIscrizioni = (hasIscrizioniObj != null) ? hasIscrizioniObj : false;

    // Recupero le liste
    List<Object> feedMisto = (List<Object>) request.getAttribute("feedMisto");
    Collection<GruppoBean> gruppiSuggeriti = (Collection<GruppoBean>) request.getAttribute("gruppiSuggeriti");

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
    SimpleDateFormat monthFormat = new SimpleDateFormat("MMM"); // Es: AGO
%>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Feed - ClubConnect</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style-bootstrap.css">

    <style>
        body { background-color: #F4F6F9; }
        .navbar-main { background: white; box-shadow: 0 2px 10px rgba(0,0,0,0.05); position: sticky; top: 0; z-index: 1000; }
        .feed-card { background: white; border: none; border-radius: 16px; box-shadow: 0 2px 6px rgba(0,0,0,0.02); margin-bottom: 1.5rem; overflow: hidden; }

        /* Sidebar & Profilo */
        .profile-card-header { background: linear-gradient(135deg, #1E3A5F, #26A9BC); height: 100px; }
        .profile-avatar { width: 80px; height: 80px; background: white; border-radius: 50%; padding: 4px; margin-top: -40px; margin-bottom: 10px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); }

        /* Post Styling */
        .post-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 1rem; }
        .group-avatar { width: 45px; height: 45px; border-radius: 12px; background: #eee; display: flex; align-items: center; justify-content: center; font-weight: bold; color: #1E3A5F; }

        /* Empty State */
        .empty-state-hero { text-align: center; padding: 3rem 2rem; background: white; border-radius: 20px; border: 2px dashed #cbd5e1; }
        .suggestion-card { border: 1px solid #f1f5f9; transition: all 0.2s; }
        .suggestion-card:hover { border-color: #26A9BC; background: #fcfdfd; transform: translateY(-2px); }

        /* Colors & Badges */
        .btn-club-primary { background-color: #1E3A5F; color: white; border: none; }
        .btn-club-primary:hover { background-color: #152943; color: white; }
        .btn-club-teal { background-color: #26A9BC; color: white; border: none; }
        .badge-event { background-color: #fff3cd; color: #856404; border: 1px solid #ffeeba; }
        .badge-comm { background-color: #cff4fc; color: #055160; border: 1px solid #b6effb; }
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
            <div class="feed-card text-center pb-3">
                <div class="profile-card-header"></div>
                <img src="https://ui-avatars.com/api/?name=<%= utente.getNome() %>+<%= utente.getCognome() %>&background=random" class="profile-avatar shadow-sm">
                <h5 class="fw-bold mb-0 text-primary"><%= utente.getNome() %> <%= utente.getCognome() %></h5>
                <p class="text-muted small">@<%= utente.getUsername() %></p>
            </div>
            <div class="feed-card p-3">
                <ul class="nav flex-column gap-2">
                    <li class="nav-item"><a href="#" class="nav-link active d-flex align-items-center gap-3 text-primary fw-bold rounded bg-light p-2"><i class="fa-solid fa-house"></i> Home Feed</a></li>
                    <li class="nav-item"><a href="miei_gruppi.jsp" class="nav-link d-flex align-items-center gap-3 text-secondary p-2"><i class="fa-solid fa-users"></i> I Miei Gruppi</a></li>
                </ul>
            </div>
            <a href="crea_gruppo.jsp" class="btn btn-club-teal w-100 py-3 rounded-4 shadow-sm fw-bold">
                <i class="fa-solid fa-plus me-2"></i> Crea Nuovo Gruppo
            </a>
        </div>

        <div class="col-lg-6">

            <% if (!hasIscrizioni) { %>
            <div class="empty-state-hero mb-4">
                <h2 class="fw-bold" style="color: #1E3A5F;">Benvenuto nel Club!</h2>
                <p class="text-muted">Non segui ancora nessuno. Ecco alcune community:</p>
                <div class="d-flex flex-column gap-3 text-start">
                    <%if (gruppiSuggeriti != null) { for(GruppoBean g : gruppiSuggeriti) {%>
                    <div class="card suggestion-card p-3 border rounded-4">
                        <div class="d-flex align-items-center justify-content-between">
                            <% System.out.println(g.getId_gruppo()); %>
                            <a href="VisualizzaGruppoServlet?id=<%= g.getId_gruppo() %>" class="d-flex align-items-center gap-3 text-decoration-none text-dark" style="cursor: pointer;">
                                <div class="group-avatar">
                                    <%= g.getNome().substring(0,1) %>
                                </div>
                                <div>
                                    <h6 class="mb-0 fw-bold"><%= g.getNome() %></h6>
                                    <span class="badge bg-light text-secondary border"><%= g.getSettore() %></span>
                                </div>
                            </a>
                            <form action="IscrizioneGruppoServlet" method="POST">
                                <input type="hidden" name="idGruppo" value="<%= g.getId_gruppo() %>">
                                <button type="submit" class="btn btn-sm btn-club-primary rounded-pill px-3">Iscriviti</button>
                            </form>
                        </div>
                    </div>
                    <% }} %>
                </div>
            </div>

            <% } else { %>

            <%
                if (feedMisto != null && !feedMisto.isEmpty()) {
                    for(Object item : feedMisto) {

                        // --- LOGICA DI VISUALIZZAZIONE DINAMICA ---
                        if (item instanceof ComunicazioniBean) {
                            ComunicazioniBean com = (ComunicazioniBean) item;
            %>
            <div class="feed-card p-4">
                <div class="post-header">
                    <div class="d-flex align-items-center gap-3">
                        <div class="group-avatar"><%= com.getId_gruppo() %></div> <div>
                        <h6 class="mb-0 fw-bold text-dark">Gruppo #<%= com.getId_gruppo() %></h6>
                        <small class="text-muted"><%= sdf.format(com.getDataPubblicazione()) %></small>
                    </div>
                    </div>
                    <span class="badge rounded-pill py-2 px-3 badge-comm">
                                                <i class="fa-solid fa-bullhorn me-1"></i> Comunicazione
                                            </span>
                </div>
                <h5 class="fw-bold mb-2">Titolo</h5>
                <p class="text-secondary mb-3"><%= com.getContenuto() %></p>
            </div>

            <%          } else if (item instanceof EventoBean) {
                EventoBean ev = (EventoBean) item;
                // Formattazione data evento per il box quadrato
                LocalDateTime dataEv = ev.getData_ora();
            %>
            <div class="feed-card p-4">
                <div class="post-header">
                    <div class="d-flex align-items-center gap-3">
                        <div class="group-avatar"><%= ev.getId_gruppo() %></div>
                        <div>
                            <h6 class="mb-0 fw-bold text-dark">Gruppo #<%= ev.getId_gruppo() %></h6>
                        </div>
                    </div>
                    <span class="badge rounded-pill py-2 px-3 badge-event">
                                                <i class="fa-solid fa-calendar-check me-1"></i> Evento
                                            </span>
                </div>

                <h5 class="fw-bold mb-2"><%= ev.getNome() %></h5>
                <p class="text-secondary mb-3"><%= ev.getDescrizione() %></p>

                <div class="bg-light p-3 rounded-3 mb-3 border d-flex align-items-center gap-3">
                    <div class="text-center px-2 border-end">
                        <span class="d-block fw-bold text-danger text-uppercase"><%= monthFormat.format(dataEv) %></span>
                        <span class="h4 fw-bold mb-0"><%= dayFormat.format(dataEv) %></span>
                    </div>
                    <div>
                        <div class="fw-bold text-dark">
                            <i class="fa-solid fa-location-dot text-danger me-1"></i> <%= ev.getCapienza_massima() %>
                        </div>
                        <div class="small text-muted">
                            Ore <%= sdf.format(dataEv).split(" ")[1] %>
                        </div>
                    </div>
                </div>

                <button class="btn btn-club-teal w-100 text-white fw-bold"><i class="fa-solid fa-check"></i> Partecipa all'evento</button>
            </div>
            <%
                    }
                } // Fine for
            } else {
            %>
            <div class="text-center py-5 text-muted">Nessuna novità nei tuoi gruppi.</div>
            <% } %>

            <% } %>

        </div>

        <div class="col-lg-3 d-none d-lg-block">
            <div class="feed-card p-3">
                <h6 class="fw-bold text-primary mb-3">Info</h6>
                <p class="small text-muted">Le comunicazioni e gli eventi qui mostrati provengono dai gruppi a cui sei iscritto.</p>
            </div>
        </div>

    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>