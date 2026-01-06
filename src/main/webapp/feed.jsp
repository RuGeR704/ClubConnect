<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="Application.GestioneAccount.UtenteBean" %>
<%@ page import="Application.GestioneGruppo.GruppoBean" %>
<%@ page import="Application.GestioneComunicazioni.ComunicazioniBean" %>
<%@ page import="Application.GestioneEventi.EventoBean" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="Storage.GruppoDAO" %>
<%@ page import="Storage.ConPool" %>
<%@ page import="Storage.EventoDAO" %>

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

        .sidebar-sticky {
            position: sticky;
            top: 100px; /* Distanza dal bordo superiore (Navbar + spazio) */
            z-index: 100; /* Assicura che stia sopra altri elementi se necessario */
        }
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
                    <li class="nav-item"><a href="#" class="nav-link active d-flex align-items-center gap-3 text-primary fw-bold rounded bg-light p-2"><i class="fa-solid fa-house"></i> Home</a></li>
                    <li class="nav-item"><a href="VisualizzaIscrizioniGruppiServlet" class="nav-link d-flex align-items-center gap-3 text-secondary p-2"><i class="fa-solid fa-users"></i> I Miei Gruppi</a></li>
                    <li class="nav-item"><a href="EsploraGruppiServlet" class="nav-link d-flex align-items-center gap-3 text-secondary p-2"><i class="fa-regular fa-compass me-2"></i>Esplora Gruppi</a></li>
                    <li class="nav-item"><a href="VisualizzaCalendarioEventiServlet" class="nav-link d-flex align-items-center gap-3 text-secondary p-2"><i class="fa-solid fa-calendar-days"></i> Calendario Eventi</a></li>
                </ul>
            </div>
            <a href="crea_gruppo.jsp" class="btn btn-club-teal w-100 py-3 rounded-4 shadow-sm fw-bold">
                <i class="fa-solid fa-plus me-2"></i> Crea Nuovo Gruppo
            </a>
            </div>
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
                            <a href="VisualizzaGruppoServlet?id=<%= g.getId_gruppo() %>" class="d-flex align-items-center gap-3 text-decoration-none text-dark" style="cursor: pointer;">
                                <div class="group-avatar overflow-hidden"> <% if (g.getLogo() != null && !g.getLogo().isEmpty()) { %>
                                    <img src="<%= g.getLogo() %>"
                                         alt="<%= g.getNome() %>"
                                         style="width: 100%; height: 100%; object-fit: cover;">
                                    <% } else { %>
                                    <%= g.getNome().substring(0,1).toUpperCase() %>
                                    <% } %>
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

                System.out.println("DEBUG JSP - Grandezza feedMisto: " + (feedMisto != null ? feedMisto.size() : "NULL"));
                if (feedMisto != null) {
                    for(Object obj : feedMisto) {
                        System.out.println("DEBUG JSP - Oggetto trovato: " + obj.getClass().getName());
                    }
                }

                    for(Object item : feedMisto) {

                        // --- LOGICA DI VISUALIZZAZIONE DINAMICA ---
                        if (item instanceof ComunicazioniBean) {
                            ComunicazioniBean com = (ComunicazioniBean) item;
            %>
            <div class="feed-card p-4">
                <div class="post-header">
                    <%
                        // 1. RECUPERO INFO GRUPPO (Necessario per avere Nome e Logo)
                        GruppoDAO gruppoDAOCom = new GruppoDAO();
                        GruppoBean gruppoDellaCom = gruppoDAOCom.doRetrieveByid(Storage.ConPool.getConnection(), com.getId_gruppo());
                    %>

                    <div class="d-flex align-items-center gap-3">

                        <div class="group-avatar" style="width: 45px; height: 45px;">
                            <% if(gruppoDellaCom != null && gruppoDellaCom.getLogo() != null && !gruppoDellaCom.getLogo().isEmpty()) { %>
                            <img src="<%= gruppoDellaCom.getLogo() %>" class="rounded-circle border"
                                 style="width: 100%; height: 100%; object-fit: cover;"
                                 alt="<%= gruppoDellaCom.getNome() %>">
                            <% } else { %>
                            <div class="rounded-circle bg-light border d-flex align-items-center justify-content-center fw-bold text-primary"
                                 style="width: 100%; height: 100%;">
                                <%= (gruppoDellaCom != null) ? gruppoDellaCom.getNome().substring(0,1).toUpperCase() : "?" %>
                            </div>
                            <% } %>
                        </div>

                        <div>
                            <h6 class="mb-0 fw-bold">
                                <a href="VisualizzaGruppoServlet?id=<%= com.getId_gruppo() %>" class="text-dark text-decoration-none">
                                    <%= (gruppoDellaCom != null) ? gruppoDellaCom.getNome() : "Gruppo sconosciuto" %>
                                </a>
                            </h6>
                            <small class="text-muted">
                                <%= (com.getDataPubblicazione() != null)
                                        ? com.getDataPubblicazione().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                                        : "" %>
                            </small>
                        </div>
                    </div>

                    <span class="badge rounded-pill py-2 px-3 badge-comm">
        <i class="fa-solid fa-bullhorn me-1"></i> Comunicazione
    </span>
                </div>
                <h5 class="fw-bold mb-2"><%= com.getTitolo()%></h5>
                <p class="text-secondary mb-3"><%= com.getContenuto() %></p>

                <% if (com.getFoto() != null && !com.getFoto().trim().isEmpty()) { %>

                <div class="mt-3 mb-3">
                    <img src="<%= com.getFoto() %>"
                         class="img-fluid w-100 rounded-4 shadow-sm border"
                         style="max-height: 400px; object-fit: cover; cursor: zoom-in;"
                         alt="Immagine post"
                         data-bs-toggle="modal"
                         data-bs-target="#imgModalCom<%= com.getId_comunicazione() %>"
                         onerror="this.style.display='none'">
                </div>

                <div class="modal fade" id="imgModalCom<%= com.getId_comunicazione() %>" tabindex="-1" aria-hidden="true">
                    <div class="modal-dialog modal-dialog-centered modal-xl">
                        <div class="modal-content bg-transparent border-0 pointer-event-none">

                            <div class="position-absolute top-0 end-0 p-3" style="z-index: 1055;">
                                <button type="button" class="btn-close btn-close-white bg-white rounded-circle p-2" data-bs-dismiss="modal" aria-label="Close" style="pointer-events: auto;"></button>
                            </div>

                            <div class="modal-body p-0 text-center d-flex justify-content-center align-items-center" style="min-height: 100vh;">
                                <img src="<%= com.getFoto() %>"
                                     class="img-fluid rounded-3 shadow-lg"
                                     style="max-height: 90vh; width: auto; pointer-events: auto;"
                                     alt="Full size">
                            </div>
                        </div>
                    </div>
                </div>

                <% } %>
            </div>

            <%          } else if (item instanceof EventoBean) {
                EventoBean ev = (EventoBean) item;
                // Formattazione data evento per il box quadrato
                LocalDateTime dataEv = ev.getData_ora();
            %>
            <div class="feed-card p-4">
                <div class="post-header">
                    <%
                        // 1. Istanzio il DAO fuori dal ciclo (se non c'è già)
                        GruppoDAO gruppoDAO = new GruppoDAO();
                    %>

                    <%
                        // 2. RECUPERO IL GRUPPO SPECIFICO DI QUESTO EVENTO
                        GruppoBean gruppoDellEvento = gruppoDAO.doRetrieveByid(ConPool.getConnection(), ev.getId_gruppo());

                        // Gestione Logo (se null, metti un default o l'iniziale)
                        String logoUrl = (gruppoDellEvento.getLogo() != null && !gruppoDellEvento.getLogo().isEmpty())
                                ? gruppoDellEvento.getLogo()
                                : "images/logo.png"; // O un placeholder
                    %>

                    <div class="d-flex align-items-center gap-3">
                        <div class="group-avatar" style="width: 45px; height: 45px;"> <% if(gruppoDellEvento.getLogo() != null && !gruppoDellEvento.getLogo().isEmpty()) { %>
                            <img src="<%= gruppoDellEvento.getLogo() %>" class="rounded-circle border"
                                 style="width: 100%; height: 100%; object-fit: cover;"
                                 alt="<%= gruppoDellEvento.getNome() %>">
                            <% } else { %>
                            <div class="rounded-circle bg-light border d-flex align-items-center justify-content-center fw-bold text-primary"
                                 style="width: 100%; height: 100%;">
                                <%= gruppoDellEvento.getNome().substring(0,1).toUpperCase() %>
                            </div>
                            <% } %>
                        </div>

                        <div>
                            <h6 class="mb-0 fw-bold">
                                <a href="VisualizzaGruppoServlet?id=<%= gruppoDellEvento.getId_gruppo() %>" class="text-dark text-decoration-none">
                                    <%= gruppoDellEvento.getNome() %>
                                </a>
                            </h6>
                            <small class="text-muted" style="font-size: 0.8rem;"><%= gruppoDellEvento.getSettore() %></small>
                        </div>
                    </div>
                    <span class="badge rounded-pill py-2 px-3 badge-event">
                        <i class="fa-solid fa-calendar-check me-1"></i> Evento
                    </span>
                </div>

                <h5 class="fw-bold mb-2"><%= ev.getNome() %></h5>
                <p class="text-secondary mb-3"><%= ev.getDescrizione() %></p>


                <% if (ev.getFoto() != null && !ev.getFoto().isEmpty()) { %>

                <div class="mt-3 mb-3">
                    <img src="<%= ev.getFoto() %>"
                         class="img-fluid w-100 rounded-4 shadow-sm border"
                         style="max-height: 400px; object-fit: cover; cursor: zoom-in;"
                         alt="Locandina <%= ev.getNome() %>"
                         data-bs-toggle="modal"
                         data-bs-target="#imgModal<%= ev.getId_evento() %>"
                         onerror="this.style.display='none'">
                </div>

                <div class="modal fade" id="imgModal<%= ev.getId_evento() %>" tabindex="-1" aria-hidden="true">
                    <div class="modal-dialog modal-dialog-centered modal-xl"> <div class="modal-content bg-transparent border-0">

                        <div class="position-absolute top-0 end-0 p-3" style="z-index: 1055;">
                            <button type="button" class="btn-close btn-close-white bg-white rounded-circle p-2" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>

                        <div class="modal-body p-0 text-center">
                            <img src="<%= ev.getFoto() %>"
                                 class="img-fluid rounded-3 shadow-lg"
                                 style="max-height: 90vh; width: auto;"
                                 alt="Full size <%= ev.getNome() %>">
                        </div>

                    </div>
                    </div>
                </div>

                <% } %>

                <div class="bg-light p-3 rounded-3 mb-3 border">
                    <div class="row g-0 align-items-center">

                        <div class="col-6 border-end pe-2">

                            <div class="mb-2 text-dark fw-bold small">
                                <i class="fa-regular fa-calendar-days me-2 text-primary"></i>
                                <%= (ev.getData_ora() != null)
                                        ? ev.getData_ora().format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm"))
                                        : "TBA" %>
                            </div>

                            <div class="fw-bold small">
                                <i class="fa-solid fa-tag me-2 text-primary"></i>
                                <% if (ev.getCosto() <= 0) { %>
                                <span class="text-success text-uppercase">Gratis</span>
                                <% } else { %>
                                <span>€ <%= String.format("%.2f", ev.getCosto()) %></span>
                                <% } %>
                            </div>

                        </div>

                        <div class="col-6 ps-3">

                            <div class="mb-2 text-muted small">
                                <i class="fa-solid fa-people-roof me-2"></i>Capienza: <%= ev.getCapienza_massima() %>
                            </div>

                            <div class="fw-bold small <%= (ev.getCapienza_massima() > 0) ? "text-success" : "text-danger" %>">
                                <% if (ev.getCapienza_massima() > 0) { %>
                                <i class="fa-solid fa-ticket me-2"></i>Posti disponibili: <%= ev.getPosti_disponibili() %>
                                <% } else { %>
                                <i class="fa-solid fa-circle-xmark me-2"></i>ESAURITO
                                <% } %>
                            </div>

                        </div>

                    </div>
                </div>

                <%
                    // 1. Controlliamo se l'utente è già iscritto
                    EventoDAO eventoDAO = new EventoDAO();
                    boolean isGiaIscritto = false;
                    try {
                        isGiaIscritto = eventoDAO.isUtentePartecipante(Storage.ConPool.getConnection(), utente.getId_utente(), ev.getId_evento());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                %>

                <form action="IscrizioneEventoServlet" method="POST" onsubmit="<%= isGiaIscritto ? "return confirm('Vuoi cancellare la tua iscrizione?');" : "" %>">

                    <input type="hidden" name="idEvento" value="<%= ev.getId_evento() %>">

                    <%-- CASO 1: L'UTENTE È GIÀ ISCRITTO --%>
                    <% if (isGiaIscritto) { %>
                    <input type="hidden" name="action" value="leave">

                    <button type="submit" class="btn btn-outline-danger w-100 fw-bold">
                        <i class="fa-solid fa-xmark me-2"></i> Annulla Iscrizione
                    </button>

                    <%-- CASO 2: NON ISCRITTO E C'È POSTO --%>
                    <% } else if (ev.getPosti_disponibili() > 0) { %>

                    <input type="hidden" name="action" value="join">

                    <button type="submit" class="btn btn-club-teal w-100 text-white fw-bold">
                        <i class="fa-solid fa-check me-2"></i> Partecipa all'evento
                    </button>

                    <%-- CASO 3: SOLD OUT --%>
                    <% } else { %>

                    <button type="button" class="btn btn-secondary w-100 fw-bold" disabled>
                        <i class="fa-solid fa-ban me-2"></i> Posti Esauriti
                    </button>

                    <% } %>

                </form>

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
                <div class="sidebar-sticky">
                <h6 class="fw-bold text-primary mb-3">Prossimi eventi</h6>
                <p class="small text-muted">Non sei ancora iscritto a nessun evento! Controlla i gruppi a cui sei iscritto.</p>
                </div>
            </div>
        </div>

    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>