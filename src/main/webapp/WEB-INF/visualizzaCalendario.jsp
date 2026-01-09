<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Application.GestioneAccount.UtenteBean" %>
<%@ page import="Application.GestioneEventi.EventoBean" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.time.format.DateTimeFormatter" %>

<%
  UtenteBean utente = (UtenteBean) session.getAttribute("utente");
  if (utente == null) {
    response.sendRedirect("login.jsp");
    return;
  }

  List<EventoBean> listaEventi = (List<EventoBean>) request.getAttribute("eventi");
  List<EventoBean> listaEventiUtente = (List<EventoBean>) request.getAttribute("eventiUtente");

  if (listaEventi == null) { listaEventi = new ArrayList<>(); }
  if (listaEventiUtente == null) { listaEventiUtente = new ArrayList<>(); }
%>

<!DOCTYPE html>
<html lang="it">
<head>
  <meta charset="UTF-8">
  <title>Calendario Eventi | ClubConnect</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

  <style>
    :root {
      --club-blue: #1E3A5F;
      --club-teal: #26A9BC;
      --club-bg: #f4f6f9;
    }

    body { background-color: var(--club-bg); }
    .navbar-main { background: white; box-shadow: 0 2px 10px rgba(0,0,0,0.05); position: sticky; top: 0; z-index: 1000; }

    .profile-card-header { background: linear-gradient(135deg, var(--club-blue), var(--club-teal)); height: 100px; border-radius: 16px 16px 0 0; }
    .profile-avatar { width: 80px; height: 80px; background: white; border-radius: 50%; padding: 4px; margin-top: -40px; margin-bottom: 10px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); object-fit: cover; }
    .feed-card { background: white; border: none; border-radius: 16px; box-shadow: 0 2px 6px rgba(0,0,0,0.02); margin-bottom: 1.5rem; overflow: hidden; }

    .ticket-card {
      background: white; border-radius: 16px; border: none;
      box-shadow: 0 2px 10px rgba(0,0,0,0.03);
      transition: all 0.2s ease;
      overflow: hidden;
      display: flex;
      cursor: pointer; /* Indica che è cliccabile */
    }
    .ticket-card:hover { transform: translateY(-3px); box-shadow: 0 8px 20px rgba(0,0,0,0.1); background-color: #f8fafc; }

    .date-box {
      background-color: #eef2f7; color: var(--club-blue);
      min-width: 90px; display: flex; flex-direction: column;
      align-items: center; justify-content: center;
      border-right: 2px dashed #d1d9e6;
    }

    .btn-club-teal { background-color: var(--club-teal); color: white; border: none; }
    .dropdown-menu { border-radius: 12px; border: none; box-shadow: 0 10px 30px rgba(0,0,0,0.1) !important; }
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
        <a href="#" class="d-flex align-items-center text-decoration-none dropdown-toggle text-dark fw-bold" data-bs-toggle="dropdown" aria-expanded="false">
          <img src="https://ui-avatars.com/api/?name=<%= utente.getNome() %>+<%= utente.getCognome() %>&background=1E3A5F&color=fff" class="rounded-circle me-2" width="35" height="35">
          <span class="d-none d-lg-inline"><%= utente.getNome() %> <%= utente.getCognome() %></span>
        </a>
        <ul class="dropdown-menu dropdown-menu-end shadow-sm border-0">
          <li><a class="dropdown-item d-flex align-items-center gap-2" href="ModificaDatiServlet"><i class="fa-solid fa-user-circle text-primary"></i> Dati utente</a></li>
          <li><hr class="dropdown-divider opacity-25"></li>
          <li><a class="dropdown-item d-flex align-items-center gap-2 text-danger" href="LogoutServlet"><i class="fa-solid fa-right-from-bracket"></i> Esci</a></li>
        </ul>
      </div>
    </div>
  </div>
</nav>

<%
  String esito = request.getParameter("esito");
  if (esito != null) {
%>
<div class="container mt-3">
  <% if ("success".equals(esito)) { %>
  <div class="alert alert-success alert-dismissible fade show shadow-sm border-start border-success border-4" role="alert">
    <i class="fa-solid fa-check-circle me-2 fs-5"></i>
    <strong>Ottimo!</strong> L'evento è stato rimosso correttamente.
    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
  </div>
  <% } else if ("errore".equals(esito)) { %>
  <div class="alert alert-danger alert-dismissible fade show shadow-sm border-start border-danger border-4" role="alert">
    <i class="fa-solid fa-triangle-exclamation me-2 fs-5"></i>
    <strong>Errore:</strong> Impossibile rimuovere l'evento. Riprova più tardi.
    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
  </div>
  <% } %>
</div>

<script>
  setTimeout(function() {
    let alerts = document.querySelectorAll('.alert');
    alerts.forEach(function(alert) {
      let bsAlert = new bootstrap.Alert(alert);
      bsAlert.close();
    });
  }, 4000); // 4000 ms = 4 secondi
</script>
<% } %>

<div class="container py-4">
  <div class="row g-4">
    <div class="col-lg-3 d-none d-lg-block">
      <div class="feed-card text-center pb-3">
        <div class="profile-card-header"></div>
        <img src="https://ui-avatars.com/api/?name=<%= utente.getNome() %>+<%= utente.getCognome() %>&background=1E3A5F&color=fff" class="profile-avatar shadow-sm">
        <h5 class="fw-bold mb-0" style="color: var(--club-blue)"><%= utente.getNome() %> <%= utente.getCognome() %></h5>
        <p class="text-muted small">@<%= utente.getUsername() %></p>
      </div>
      <div class="feed-card p-3">
        <ul class="nav flex-column gap-2">
          <li class="nav-item"><a href="feedServlet" class="nav-link d-flex align-items-center gap-3 text-secondary p-2"><i class="fa-solid fa-house"></i> Home</a></li>
          <li class="nav-item"><a href="VisualizzaIscrizioniGruppiServlet" class="nav-link d-flex align-items-center gap-3 text-secondary p-2"><i class="fa-solid fa-users"></i> I Miei Gruppi</a></li>
          <li class="nav-item"><a href="EsploraGruppiServlet" class="nav-link d-flex align-items-center gap-3 text-secondary p-2"><i class="fa-regular fa-compass"></i> Esplora Gruppi</a></li>
          <li class="nav-item"><a href="VisualizzaCalendarioEventiServlet" class="nav-link active d-flex align-items-center gap-3 fw-bold rounded bg-light p-2" style="color: var(--club-blue)"><i class="fa-solid fa-calendar-days"></i> Calendario Eventi</a></li>
        </ul>
      </div>
    </div>

    <div class="col-lg-9">
      <h2 class="fw-bold text-dark mb-4">Calendario Eventi</h2>

      <div class="row g-4 mb-5">
        <% if (!listaEventiUtente.isEmpty()) { %>
        <% for (EventoBean ev : listaEventiUtente) { %>
        <div class="col-12">
          <div class="ticket-card" onclick="window.location.href='VisualizzaEventoServlet?id=<%= ev.getId_evento() %>'">
            <div class="date-box p-3">
              <span class="small fw-bold text-uppercase text-muted"><%= (ev.getData_ora() != null) ? ev.getData_ora().getMonth().toString().substring(0,3) : "ND" %></span>
              <span class="display-6 fw-bold text-dark"><%= (ev.getData_ora() != null) ? ev.getData_ora().getDayOfMonth() : "?" %></span>
            </div>
            <div class="p-4 flex-grow-1">
              <h5 class="fw-bold mb-1 text-dark"><%= ev.getNome() %></h5>
              <span class="badge bg-light text-dark border">Prenotato</span>
            </div>
          </div>
        </div>
        <% } %>
        <% } %>
      </div>

      <div class="row g-4">
        <% if (!listaEventi.isEmpty()) { %>
        <% for (EventoBean ev : listaEventi) { %>
        <div class="col-12">
          <div class="ticket-card" onclick="window.location.href='VisualizzaEventoServlet?id=<%= ev.getId_evento() %>'">
            <div class="date-box p-3">
              <span class="small fw-bold text-uppercase text-muted"><%= (ev.getData_ora() != null) ? ev.getData_ora().getMonth().toString().substring(0,3) : "ND" %></span>
              <span class="display-6 fw-bold text-dark"><%= (ev.getData_ora() != null) ? ev.getData_ora().getDayOfMonth() : "?" %></span>
            </div>
            <div class="p-4 flex-grow-1">
              <h5 class="fw-bold mb-1 text-dark"><%= ev.getNome() %></h5>
              <span class="badge bg-club-teal text-white">Disponibile</span>
            </div>
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