<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Application.GestioneAccount.UtenteBean" %>
<%@ page import="Application.GestioneEventi.EventoBean" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.time.format.DateTimeFormatter" %>

<%
  // 1. RECUPERO UTENTE (Solo per Header e Sidebar)
  UtenteBean utente = (UtenteBean) session.getAttribute("utente");
  if (utente == null) {
    response.sendRedirect("login.jsp");
    return;
  }

  // 2. RECUPERO DATI DALLA SERVLET
  // Non facciamo più query qui! Prendiamo quello che ci ha passato la Servlet.
  List<EventoBean> listaEventi = (List<EventoBean>) request.getAttribute("eventi");
  List<EventoBean> listaEventiUtente = (List<EventoBean>) request.getAttribute("eventiUtente");

  // Sicurezza: se la lista è null (errore Servlet), ne creiamo una vuota per non rompere la pagina
  if (listaEventi == null) {
    listaEventi = new ArrayList<>();
  }
%>

<!DOCTYPE html>
<html lang="it">
<head>
  <meta charset="UTF-8">
  <title>Calendario Eventi | ClubConnect</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

  <style>
    body { background-color: #f4f6f9; }
    .navbar-main { background: white; box-shadow: 0 2px 10px rgba(0,0,0,0.05); position: sticky; top: 0; z-index: 1000; }

    /* Sidebar & Profilo */
    .profile-card-header { background: linear-gradient(135deg, #1E3A5F, #26A9BC); height: 100px; border-radius: 16px 16px 0 0; }
    .profile-avatar { width: 80px; height: 80px; background: white; border-radius: 50%; padding: 4px; margin-top: -40px; margin-bottom: 10px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); object-fit: cover; }
    .feed-card { background: white; border: none; border-radius: 16px; box-shadow: 0 2px 6px rgba(0,0,0,0.02); margin-bottom: 1.5rem; overflow: hidden; }

    /* TICKET STYLE */
    .ticket-card {
      background: white; border-radius: 16px; border: none;
      box-shadow: 0 2px 10px rgba(0,0,0,0.03);
      transition: transform 0.2s ease, box-shadow 0.2s ease;
      overflow: hidden;
      display: flex;
    }
    .ticket-card:hover { transform: translateY(-3px); box-shadow: 0 5px 15px rgba(0,0,0,0.08); }

    .date-box {
      background-color: #eef2f7; color: #1E3A5F;
      min-width: 90px; display: flex; flex-direction: column;
      align-items: center; justify-content: center;
      border-right: 2px dashed #d1d9e6;
    }

    .btn-club-teal { background-color: #26A9BC; color: white; border: none; }
  </style>
</head>
<body>

<nav class="navbar navbar-expand-lg navbar-main py-2">
  <div class="container">
    <a href="feedServlet" class="d-inline-flex align-items-center gap-2 text-decoration-none">
      <div class="brand-icon" style="width: 40px; height: 40px;">
        <img src="./images/logo.png" alt="Logo" class="img-fluid">
      </div>
      <span class="fs-4 fw-bold" style="color: #1E3A5F;">Club<span style="color: #26A9BC;">Connect</span></span>
    </a>
    <div class="ms-auto d-flex align-items-center gap-3">
      <div class="dropdown">
        <a href="#" class="d-flex align-items-center text-decoration-none dropdown-toggle text-dark fw-bold" data-bs-toggle="dropdown">
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
            <li class="nav-item"><a href="EsploraGruppiServlet" class="nav-link d-flex align-items-center gap-3 text-secondary p-2"><i class="fa-regular fa-compass me-2"></i>Esplora Gruppi</a></li>
            <li class="nav-item"><a href="VisualizzaCalendarioEventiServlet" class="nav-link active d-flex align-items-center gap-3 text-primary fw-bold rounded bg-light p-2"><i class="fa-solid fa-calendar-days"></i> Calendario Eventi</a></li>
          </ul>
        </div>
        <a href="crea_gruppo.jsp" class="btn btn-club-teal w-100 py-3 rounded-4 shadow-sm fw-bold">
          <i class="fa-solid fa-plus me-2"></i> Crea Nuovo Gruppo
        </a>
      </div>
    </div>

    <div class="col-lg-9">

      <div class="d-flex align-items-center justify-content-between mb-4">
        <div>
          <h2 class="fw-bold text-dark mb-0">Calendario Eventi Prenotati</h2>
          <p class="text-muted mb-0">Tutti gli eventi in programma.</p>
        </div>
        <div class="bg-white px-4 py-2 rounded-pill shadow-sm">
          <span class="h4 fw-bold text-primary mb-0"><%= listaEventiUtente.size() %></span>
          <small class="text-muted ms-1">Eventi</small>
        </div>
      </div>

      <div class="row g-4">

        <% if (listaEventiUtente.isEmpty()) { %>
        <div class="col-12 text-center py-5 bg-white rounded-4 shadow-sm">
          <div class="mb-3 opacity-25"><i class="fa-solid fa-calendar-xmark fa-4x text-muted"></i></div>
          <h4 class="fw-bold text-muted">Nessun evento trovato</h4>
          <p class="text-muted">Il calendario è attualmente vuoto.</p>
        </div>
        <% } else { %>

        <% for (EventoBean ev : listaEventiUtente) { %>
        <div class="col-lg-12">
          <div class="ticket-card p-0">

            <div class="date-box p-3 text-center">
                                <span class="small fw-bold text-uppercase text-muted">
                                    <%= (ev.getData_ora() != null) ? ev.getData_ora().getMonth().toString().substring(0,3) : "ND" %>
                                </span>
              <span class="display-6 fw-bold text-dark" style="line-height: 1;">
                                    <%= (ev.getData_ora() != null) ? ev.getData_ora().getDayOfMonth() : "?" %>
                                </span>
              <span class="small text-muted mt-1">
                                    <%= (ev.getData_ora() != null) ? ev.getData_ora().format(DateTimeFormatter.ofPattern("HH:mm")) : "" %>
                                </span>
            </div>

            <div class="p-4 flex-grow-1 d-flex flex-column justify-content-center">
              <h5 class="fw-bold mb-1 text-dark">
                <a href="VisualizzaEventoServlet?id=<%= ev.getId_evento() %>" class="text-decoration-none text-dark stretched-link">
                  <%= ev.getNome() %>
                </a>
              </h5>

              <div class="mt-auto">
                <span class="badge bg-light text-dark border">
                  <i class="fa-solid fa-users me-1"></i> Posti: <%= ev.getPosti_disponibili() %>
                </span>
              </div>
            </div>

            <% if(ev.getFoto() != null && !ev.getFoto().isEmpty()) { %>
            <div class="d-none d-md-block" style="width: 150px; background-image: url('<%= ev.getFoto() %>'); background-size: cover; background-position: center;"></div>
            <% } %>

          </div>
        </div>
        <% } %>
        <% } %>
      </div>

      <br> <br> <br>

      <div class="row g-4">

        <div>
          <h2 class="fw-bold text-dark mb-0">Calendario Eventi a cui non ti sei registrato</h2>
          <p class="text-muted mb-0">Tutti gli eventi a cui potresti iscriverti</p>
        </div>

        <% if (listaEventi.isEmpty()) { %>
        <div class="col-12 text-center py-5 bg-white rounded-4 shadow-sm">
          <div class="mb-3 opacity-25"><i class="fa-solid fa-calendar-xmark fa-4x text-muted"></i></div>
          <h4 class="fw-bold text-muted">Nessun evento trovato</h4>
          <p class="text-muted">Il calendario è attualmente vuoto.</p>
        </div>
        <% } else { %>

        <% for (EventoBean ev : listaEventi) { %>
        <div class="col-lg-12">
          <div class="ticket-card p-0">

            <div class="date-box p-3 text-center">
                                <span class="small fw-bold text-uppercase text-muted">
                                    <%= (ev.getData_ora() != null) ? ev.getData_ora().getMonth().toString().substring(0,3) : "ND" %>
                                </span>
              <span class="display-6 fw-bold text-dark" style="line-height: 1;">
                                    <%= (ev.getData_ora() != null) ? ev.getData_ora().getDayOfMonth() : "?" %>
                                </span>
              <span class="small text-muted mt-1">
                                    <%= (ev.getData_ora() != null) ? ev.getData_ora().format(DateTimeFormatter.ofPattern("HH:mm")) : "" %>
                                </span>
            </div>

            <div class="p-4 flex-grow-1 d-flex flex-column justify-content-center">
              <h5 class="fw-bold mb-1 text-dark">
                <a href="VisualizzaEventoServlet?id=<%= ev.getId_evento() %>" class="text-decoration-none text-dark stretched-link">
                  <%= ev.getNome() %>
                </a>
              </h5>

              <div class="mt-auto">
                                    <span class="badge bg-light text-dark border">
                                        <i class="fa-solid fa-users me-1"></i> Posti: <%= ev.getPosti_disponibili() %>
                                    </span>
              </div>
            </div>

            <% if(ev.getFoto() != null && !ev.getFoto().isEmpty()) { %>
            <div class="d-none d-md-block" style="width: 150px; background-image: url('<%= ev.getFoto() %>'); background-size: cover; background-position: center;"></div>
            <% } %>

          </div>
        </div>
        <% } %>
        <% } %>
      </div>

    </div>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
