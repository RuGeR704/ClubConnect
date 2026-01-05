<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Application.GestioneAccount.UtenteBean" %>
<%@ page import="Application.GestioneGruppo.GruppoBean" %>
<%@ page import="Application.GestioneEventi.EventoBean" %>
<%@ page import="Storage.GruppoDAO" %>
<%@ page import="Storage.ConPool" %>
<%@ page import="java.time.format.DateTimeFormatter" %>

<%
    // 1. RECUPERO UTENTE (Controllo Sessione)
    UtenteBean utente = (UtenteBean) session.getAttribute("utente");
    if (utente == null) {
        // Se non loggato, rimanda al login con redirect alla pagina corrente
        response.sendRedirect("login.jsp?redirect=VisualizzaEventoServlet?id=" + request.getParameter("id"));
        return;
    }

    // 2. RECUPERO EVENTO (Dalla Servlet)
    EventoBean evento = (EventoBean) request.getAttribute("evento");

    // Se la pagina viene aperta direttamente senza passare dalla Servlet, evento sarà null
    if (evento == null) {
        String idParam = request.getParameter("id");
        if(idParam != null) {
            // Forward automatico alla servlet se c'è l'ID ma manca l'oggetto
            request.getRequestDispatcher("VisualizzaEventoServlet").forward(request, response);
            return;
        } else {
            response.sendRedirect("feedServlet");
            return;
        }
    }

    // 3. RECUPERO DATI MANCANTI (Gruppo e Partecipazione)
    // Dato che la servlet ci ha dato solo l'evento, qui recuperiamo il resto
    GruppoBean gruppo = null;
    boolean isPartecipante = false;
    boolean isAdmin = false; // Se l'utente è admin del gruppo

    try {
        // A. Recupero il Gruppo che organizza l'evento
        GruppoDAO gruppoDAO = new GruppoDAO();
        gruppo = gruppoDAO.doRetrieveByid(ConPool.getConnection(), evento.getId_gruppo());

    } catch (Exception e) {
        e.printStackTrace();
    }
%>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title><%= evento.getNome() %> | ClubConnect</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <style>
        /* STILI PER LA PAGINA DETTAGLIO */
        body { background-color: transparent; }

        /* Sfondo sfocato "Immersivo" */
        .ambient-background {
            position: fixed; top: 0; left: 0; width: 100%; height: 100vh;
            z-index: -1; overflow: hidden; background-color: #f4f6f9;
        }
        .ambient-background::before {
            content: ""; position: absolute;
            top: -50px; left: -50px; right: -50px; bottom: -50px;
            /* Usa la foto evento o il logo gruppo come sfondo */
            background-image: var(--bg-image);
            background-size: cover; background-position: center;
            filter: blur(60px) saturate(120%) brightness(0.9); opacity: 0.5;
            mask-image: linear-gradient(to bottom, black, transparent);
        }

        .event-card-main {
            background: white; border: none; border-radius: 20px;
            box-shadow: 0 5px 20px rgba(0,0,0,0.05); padding: 2rem;
        }

        .sidebar-card {
            background: white; border: none; border-radius: 20px;
            box-shadow: 0 5px 20px rgba(0,0,0,0.05); padding: 1.5rem;
            position: sticky; top: 100px; /* Sidebar fissa mentre scrolli */
        }

        .img-zoomable { cursor: zoom-in; transition: transform 0.2s; }
        .img-zoomable:hover { transform: scale(1.01); }

        .organizer-pill {
            background: #f8f9fa; border-radius: 50px; padding: 5px 15px 5px 5px;
            display: inline-flex; align-items: center; gap: 10px;
            text-decoration: none; color: #333; transition: 0.2s; border: 1px solid #eee;
        }
        .organizer-pill:hover { background: #e9ecef; color: #000; }

        /* Animazione entrata */
        .animate-fade-in { animation: fadeIn 0.5s ease-out; }
        @keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
    </style>
</head>
<body>

<nav class="navbar navbar-expand-lg navbar-light bg-white shadow-sm sticky-top">
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

<%
    String bgUrl = (evento.getFoto() != null && !evento.getFoto().isEmpty())
            ? evento.getFoto()
            : (gruppo.getLogo() != null ? gruppo.getLogo() : "images/default-pattern.jpg");
%>
<div class="ambient-background" style="--bg-image: url('<%= bgUrl %>');"></div>

<div class="container py-5">
    <div class="row g-4">

        <div class="col-lg-8">
            <div class="event-card-main mb-4 animate-fade-in">

                <div class="mb-3">
                    <span class="badge bg-primary bg-opacity-10 text-primary px-3 py-2 rounded-pill fw-bold text-uppercase">
                        <i class="fa-regular fa-calendar-check me-2"></i> Evento
                    </span>
                    <% if(isPartecipante) { %>
                    <span class="badge bg-success text-white px-3 py-2 rounded-pill fw-bold ms-2 animate-fade-in">
                            <i class="fa-solid fa-check me-1"></i> Iscritto
                        </span>
                    <% } %>
                </div>

                <h1 class="fw-bold mb-3" style="color: #1E3A5F;"><%= evento.getNome() %></h1>

                <a href="VisualizzaGruppoServlet?id=<%= gruppo.getId_gruppo() %>" class="organizer-pill mb-4">
                    <% if(gruppo.getLogo() != null) { %>
                    <img src="<%= gruppo.getLogo() %>" class="rounded-circle" width="40" height="40" style="object-fit: cover;">
                    <% } else { %>
                    <div class="rounded-circle bg-primary text-white d-flex align-items-center justify-content-center fw-bold" style="width: 40px; height: 40px;">
                        <%= gruppo.getNome().substring(0,1) %>
                    </div>
                    <% } %>
                    <span class="fw-bold small">Organizzato da <%= gruppo.getNome() %></span>
                </a>

                <% if(evento.getFoto() != null && !evento.getFoto().isEmpty()) { %>
                <div class="mb-4">
                    <img src="<%= evento.getFoto() %>"
                         class="img-fluid w-100 rounded-4 shadow-sm border img-zoomable"
                         style="max-height: 500px; object-fit: cover;"
                         data-bs-toggle="modal" data-bs-target="#imgModal">
                </div>

                <div class="modal fade" id="imgModal" tabindex="-1">
                    <div class="modal-dialog modal-dialog-centered modal-xl">
                        <div class="modal-content bg-transparent border-0 text-center pointer-event-none">
                            <button type="button" class="btn-close btn-close-white position-absolute top-0 end-0 m-3" data-bs-dismiss="modal" style="pointer-events: auto; z-index: 1060;"></button>
                            <div class="modal-body p-0 d-flex justify-content-center align-items-center" style="min-height: 100vh;">
                                <img src="<%= evento.getFoto() %>" class="rounded-3 shadow-lg" style="max-height: 90vh; max-width: 95vw; width: auto; height: auto; object-fit: contain; pointer-events: auto;">
                            </div>
                        </div>
                    </div>
                </div>
                <% } %>

                <h5 class="fw-bold mt-4 mb-3">Descrizione</h5>
                <p class="text-muted" style="white-space: pre-line; line-height: 1.7; font-size: 1.05rem;">
                    <%= evento.getDescrizione() %>
                </p>

            </div>
        </div>

        <div class="col-lg-4">
            <div class="sidebar-card animate-fade-in">

                <h5 class="fw-bold mb-4">Dettagli Iscrizione</h5>

                <div class="d-flex align-items-center mb-3">
                    <div class="bg-light p-3 rounded-3 text-primary me-3">
                        <i class="fa-regular fa-calendar-days fs-4"></i>
                    </div>
                    <div>
                        <small class="text-muted d-block text-uppercase fw-bold" style="font-size: 0.7rem;">Data e Ora</small>
                        <span class="fw-bold text-dark">
                            <%= (evento.getData_ora() != null) ? evento.getData_ora().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")) : "Da definire" %>
                        </span>
                        <div class="small text-muted">
                            <%= (evento.getData_ora() != null) ? evento.getData_ora().format(DateTimeFormatter.ofPattern("HH:mm")) : "--:--" %>
                        </div>
                    </div>
                </div>


                <div class="d-flex align-items-center mb-3">
                    <div class="bg-light p-3 rounded-3 text-success me-3">
                        <i class="fa-solid fa-tag fs-4"></i>
                    </div>
                    <div>
                        <small class="text-muted d-block text-uppercase fw-bold" style="font-size: 0.7rem;">Costo Biglietto</small>
                        <% if(evento.getCosto() > 0) { %>
                        <span class="fw-bold text-dark fs-5">€ <%= String.format("%.2f", evento.getCosto()) %></span>
                        <% } else { %>
                        <span class="fw-bold text-success fs-5">GRATIS</span>
                        <% } %>
                    </div>
                </div>

                <hr class="my-4">

                <div class="d-flex justify-content-between mb-2">
                    <span class="small fw-bold text-muted">Disponibilità</span>
                    <span class="small fw-bold <%= evento.getPosti_disponibili() > 0 ? "text-success" : "text-danger" %>">
                        <%= evento.getPosti_disponibili() > 0 ? evento.getPosti_disponibili() + " posti rimasti" : "Esaurito" %>
                    </span>
                </div>

                <%
                    int max = evento.getCapienza_massima();
                    int disp = evento.getPosti_disponibili();
                    int occupati = max - disp;
                    int percent = (max > 0) ? (occupati * 100 / max) : 100;
                %>
                <div class="progress mb-4" style="height: 8px;">
                    <div class="progress-bar bg-primary" role="progressbar" style="width: <%= percent %>%"></div>
                </div>

                <% if(isPartecipante) { %>
                <form action="IscrizioneEventoServlet" method="POST" onsubmit="return confirm('Sei sicuro di voler cancellare la tua prenotazione?');">
                    <input type="hidden" name="action" value="leave"> <input type="hidden" name="idEvento" value="<%= evento.getId_evento() %>">
                    <button type="submit" class="btn btn-outline-danger w-100 rounded-pill py-2 fw-bold">
                        <i class="fa-solid fa-xmark me-2"></i> Annulla Partecipazione
                    </button>
                </form>
                <div class="text-center mt-2">
                    <small class="text-muted">Il tuo posto è prenotato.</small>
                </div>

                <% } else { %>
                <form action="IscrizioneEventoServlet" method="POST">
                    <input type="hidden" name="action" value="join"> <input type="hidden" name="idEvento" value="<%= evento.getId_evento() %>">

                    <% if(evento.getPosti_disponibili() > 0) { %>
                    <button type="submit" class="btn btn-primary w-100 rounded-pill py-3 fw-bold shadow">
                        <i class="fa-solid fa-ticket me-2"></i> Prenota un posto
                    </button>
                    <% } else { %>
                    <button type="button" class="btn btn-secondary w-100 rounded-pill py-3 fw-bold" disabled>
                        <i class="fa-solid fa-ban me-2"></i> Sold Out
                    </button>
                    <% } %>
                </form>
                <% } %>

                <% if(isAdmin) { %>
                <div class="mt-4 pt-3 border-top text-center">
                    <form action="EliminaEventoServlet" method="POST" onsubmit="return confirm('ATTENZIONE: Questa azione è irreversibile. Vuoi eliminare l\'evento?');">
                        <input type="hidden" name="idEvento" value="<%= evento.getId_evento() %>">
                        <input type="hidden" name="idGruppo" value="<%= gruppo.getId_gruppo() %>">
                        <button type="submit" class="btn btn-link text-danger text-decoration-none small">
                            <i class="fa-regular fa-trash-can me-1"></i> Elimina Evento
                        </button>
                    </form>
                </div>
                <% } %>

            </div>
        </div>

    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
