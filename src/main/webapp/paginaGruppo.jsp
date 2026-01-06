<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Application.GestioneAccount.UtenteBean" %>
<%@ page import="Application.GestioneGruppo.GruppoBean" %>
<%@ page import="Application.GestioneGruppo.ClubBean" %>
<%@ page import="Application.GestioneEventi.EventoBean" %>
<%@ page import="Application.GestioneComunicazioni.ComunicazioniBean" %>
<%@ page import="Storage.EventoDAO" %>
<%@ page import="Storage.ComunicazioneDAO" %>
<%@ page import="Storage.ConPool" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.Comparator" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="java.time.format.DateTimeFormatter" %>

<%
    // 1. RECUPERO DATI
    UtenteBean utente = (UtenteBean) session.getAttribute("utente");

    // CHECK DI SICUREZZA: Se la sessione è scaduta, vai al login
    if (utente == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    GruppoBean gruppo = (GruppoBean) request.getAttribute("gruppo");
    boolean isAdmin = (request.getAttribute("isAdmin") != null) ? (boolean) request.getAttribute("isAdmin") : false;
    boolean isIscritto = (request.getAttribute("isIscritto") != null) ? (boolean) request.getAttribute("isIscritto") : false;

    if (gruppo == null) {
        response.sendRedirect("feedServlet");
        return;
    }
%>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title><%= gruppo.getNome() %> - ClubConnect</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <style>
        body { background-color: transparent; }

        .group-header {
            background-color: transparent;
            padding-bottom: 1rem;
            margin-bottom: 2rem;
        }

        .cover-image {
            height: 300px; width: 100%;
            position: relative;
            background: linear-gradient(to bottom, transparent, rgba(0,0,0,0.3));
        }

        .edit-cover-btn {
            position: absolute; bottom: 20px; right: 20px;
            background: rgba(0,0,0,0.6); color: white;
            border: none; padding: 8px 15px; border-radius: 8px;
            backdrop-filter: blur(5px); transition: 0.2s;
        }
        .edit-cover-btn:hover { background: rgba(0,0,0,0.8); }

        .group-avatar-container {
            position: relative; margin-top: -80px; margin-left: 20px;
            width: 160px; height: 160px;
        }

        .group-avatar {
            width: 100%; height: 100%; border-radius: 50%;
            border: 5px solid white; background: white;
            object-fit: cover; box-shadow: 0 4px 15px rgba(0,0,0,0.1);
        }

        .edit-avatar-btn {
            position: absolute; bottom: 5px; right: 5px;
            width: 40px; height: 40px; border-radius: 50%;
            background: #26A9BC; color: white; border: 3px solid white;
            display: flex; align-items: center; justify-content: center; cursor: pointer;
        }

        .info-card {
            background: white; border: none; border-radius: 16px;
            box-shadow: 0 2px 6px rgba(0,0,0,0.02); padding: 1.5rem; margin-bottom: 1.5rem;
        }

        .stat-box { text-align: center; padding: 10px; border-right: 1px solid #eee; }
        .stat-box:last-child { border-right: none; }

        .btn-join { background-color: #26A9BC; color: white; border: none; padding: 10px 30px; border-radius: 30px; font-weight: bold; }
        .btn-join:hover { background-color: #1e8a9b; color: white; }

        .btn-outline-admin { border: 2px solid #1E3A5F; color: #1E3A5F; border-radius: 30px; font-weight: bold; }
        .btn-outline-admin:hover { background: #1E3A5F; color: white; }

        .nav-pills .nav-link { color: #6c757d; background-color: transparent; transition: all 0.3s ease; }
        .nav-pills .nav-link.active { background-color: #26A9BC; color: white !important; box-shadow: 0 4px 6px rgba(38, 169, 188, 0.3); }
        .nav-pills .nav-link:hover:not(.active) { background-color: rgba(38, 169, 188, 0.1); color: #26A9BC; }

        /* SFONDO DINAMICO */
        .ambient-background {
            position: fixed; top: 0; left: 0; width: 100%; height: 100vh;
            z-index: -1; overflow: hidden; background-color: #f4f6f9;
        }
        .ambient-background::before {
            content: ""; position: absolute;
            top: -50px; left: -50px; right: -50px; bottom: -50px;
            background-image: var(--bg-image); background-size: cover; background-position: center;
            filter: blur(60px) saturate(150%) brightness(0.8); opacity: 0.6;
            mask-image: linear-gradient(to bottom, black, transparent);
        }

        /* CLASSI SPOSTATE FUORI (Correzione CSS) */
        .animate-fade-in { animation: fadeIn 0.3s ease-in-out; }
        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(-10px); }
            to { opacity: 1; transform: translateY(0); }
        }
        .cursor-pointer { cursor: pointer; }
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
    String bgUrl = (gruppo.getLogo() != null && !gruppo.getLogo().isEmpty())
            ? gruppo.getLogo()
            : request.getContextPath() + "/images/default-pattern.jpg";
%>
<div class="ambient-background" style="--bg-image: url('<%= bgUrl %>');"></div>

<div class="group-header position-relative">
    <div class="cover-image d-flex align-items-end justify-content-end p-3" style="background: rgba(0,0,0,0.1);">
        <% if(isAdmin) { %>
        <button class="edit-cover-btn shadow-sm" data-bs-toggle="modal" data-bs-target="#modalEditImages">
            <i class="fa-solid fa-camera me-2"></i> Modifica Sfondo
        </button>
        <% } %>
    </div>
</div>

<div class="container">
    <div class="d-flex flex-column flex-md-row align-items-center align-items-md-end">
        <div class="group-avatar-container">
            <% if (gruppo.getLogo() != null && !gruppo.getLogo().isEmpty()) { %>
            <img src="<%= gruppo.getLogo() %>" class="group-avatar">
            <% } else { %>
            <div class="group-avatar d-flex align-items-center justify-content-center bg-light text-primary fs-1 fw-bold">
                <%= gruppo.getNome().substring(0,1) %>
            </div>
            <% } %>

            <% if(isAdmin) { %>
            <div class="edit-avatar-btn" data-bs-toggle="modal" data-bs-target="#modalEditImages" title="Cambia Logo">
                <i class="fa-solid fa-pencil"></i>
            </div>
            <% } %>
        </div>

        <div class="ms-md-4 mt-3 mt-md-0 mb-2 text-center text-md-start flex-grow-1">
            <h2 class="fw-bold mb-0" style="color: #1E3A5F;"><%= gruppo.getNome() %></h2>
            <p class="text-muted mb-1"><%= gruppo.getSlogan() != null ? gruppo.getSlogan() : "Nessuno slogan impostato" %></p>
            <span class="badge bg-light text-secondary border">
                <%= (gruppo instanceof ClubBean) ? "Club" : "Associazione" %> • <%= gruppo.getSettore() %>
            </span>
        </div>

        <div class="mb-3 ms-md-auto gap-2 d-flex">
            <% if (isAdmin) { %>
            <button class="btn btn-outline-admin" data-bs-toggle="modal" data-bs-target="#modalEditInfo">
                <i class="fa-solid fa-pencil me-2"></i> Modifica Dettagli
            </button>
            <form action="DashboardServlet">
                <input type="hidden" value="<%= gruppo.getId_gruppo()%>" name="idGruppo">
                <button type="submit" class="btn btn-outline-admin">
                    <i class="fa-solid fa-gear me-2"></i> Gestisci
                </button>
            </form>
            <% } else { %>
            <% if (isIscritto) { %>
            <button class="btn btn-outline-secondary rounded-pill fw-bold">
                <i class="fa-solid fa-check me-2"></i> Iscritto
            </button>
            <form action="IscrizioneGruppoServlet" method="POST" onsubmit="return confirm('Vuoi davvero lasciare il gruppo?');">
                <input type="hidden" name="action" value="leave">
                <input type="hidden" name="idGruppo" value="<%= gruppo.getId_gruppo() %>">
                <button type="submit" class="btn btn-outline-danger rounded-pill fw-bold ms-2">
                    <i class="fa-solid fa-right-from-bracket"></i>
                </button>
            </form>
            <% } else { %>
            <form action="IscrizioneGruppoServlet" method="POST">
                <input type="hidden" name="idGruppo" value="<%= gruppo.getId_gruppo() %>">
                <button type="submit" class="btn btn-join shadow-sm">
                    <i class="fa-solid fa-plus me-2"></i> Iscriviti
                </button>
            </form>
            <% } %>
            <% } %>
        </div>
    </div>

    <div class="mt-4 border-top pt-2">
        <ul class="nav nav-pills" id="groupTabs" role="tablist">
            <li class="nav-item">
                <a class="nav-link active rounded-pill fw-bold" id="home-tab" data-bs-toggle="tab" href="#home" role="tab">Bacheca</a>
            </li>
            <li class="nav-item">
                <a class="nav-link rounded-pill fw-bold" id="about-tab" data-bs-toggle="tab" href="#about" role="tab">Comunicazioni</a>
            </li>
            <li class="nav-item">
                <a class="nav-link rounded-pill fw-bold" id="events-tab" data-bs-toggle="tab" href="#events" role="tab">Eventi</a>
            </li>
        </ul>
    </div>
</div>

<br>

<div class="container">
    <div class="row g-4">

        <div class="col-lg-4">
            <div class="info-card d-flex justify-content-around py-3">
                <div class="stat-box">
                    <h5 class="fw-bold mb-0 text-primary">125</h5> <small class="text-muted">Membri</small>
                </div>
                <div class="stat-box">
                    <h5 class="fw-bold mb-0 text-primary">12</h5> <small class="text-muted">Eventi</small>
                </div>
            </div>

            <div class="info-card">
                <h5 class="fw-bold mb-3" style="color: #1E3A5F;">Dettagli</h5>

                <div class="mb-3">
                    <small class="text-muted d-block text-uppercase fw-bold" style="font-size: 0.7rem;">Descrizione</small>
                    <p class="small mb-0"><%= gruppo.getDescrizione() %></p>
                </div>

                <div class="mb-3">
                    <small class="text-muted d-block text-uppercase fw-bold" style="font-size: 0.7rem;">Sede</small>
                    <div class="d-flex align-items-center gap-2">
                        <i class="fa-solid fa-location-dot text-danger"></i>
                        <span><%= gruppo.getSede() %></span>
                    </div>
                </div>

                <% if (gruppo instanceof ClubBean) {
                    ClubBean club = (ClubBean) gruppo; %>
                <div class="p-3 bg-light rounded border mb-3">
                    <div class="d-flex justify-content-between mb-1">
                        <span class="small fw-bold">Quota Iscrizione:</span>
                        <span class="text-success fw-bold">€ <%= club.getImporto_retta() %></span>
                    </div>
                    <div class="d-flex justify-content-between">
                        <span class="small fw-bold">Frequenza:</span>
                        <span class="text-dark">
                        <%
                            int f = club.getFrequenza();
                            String freqLeggibile = (f == 1) ? "Settimanale" : (f == 2) ? "Mensile" : "Annuale";
                        %>
                        <%= freqLeggibile %>
                        </span>
                    </div>
                </div>
                <% } %>

                <div class="mt-3 pt-3 border-top">
                    <small class="text-muted d-block text-uppercase fw-bold mb-2" style="font-size: 0.7rem;">Regole</small>
                    <div class="alert alert-warning small p-2 mb-0">
                        <i class="fa-solid fa-triangle-exclamation me-1"></i> <%= gruppo.getRegole() %>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-lg-8">
            <div class="tab-content" id="myTabContent">

                <div class="tab-pane fade show active" id="home" role="tabpanel">

                    <% if (isAdmin || isIscritto) { %>
                    <div class="info-card p-4 mb-4" id="postCreationContainer">

                        <div id="default-post-view">
                            <div class="d-flex gap-3 align-items-center mb-3">
                                <img src="https://ui-avatars.com/api/?name=<%= utente.getNome() %>" class="rounded-circle" width="45" height="45">
                                <div class="flex-grow-1 cursor-pointer" onclick="openForm('notice')">
                                    <input type="text" class="form-control rounded-pill bg-light border-0" placeholder="Scrivi qualcosa al gruppo..." readonly style="cursor: pointer;">
                                </div>
                            </div>

                            <div class="d-flex justify-content-end gap-2 border-top pt-3">
                                <% if(isAdmin) { %>
                                <button type="button" class="btn btn-sm btn-light text-primary fw-bold" onclick="openForm('event')">
                                    <i class="fa-solid fa-calendar-plus me-1"></i> Crea Evento
                                </button>
                                <button type="button" class="btn btn-sm btn-light text-info fw-bold" onclick="openForm('notice')">
                                    <i class="fa-solid fa-bullhorn me-1"></i> Comunicazione
                                </button>
                                <% } else { %>
                                <button type="button" class="btn btn-sm btn-light text-secondary" onclick="openForm('notice')">
                                    <i class="fa-regular fa-image me-1"></i> Foto/Post
                                </button>
                                <% } %>
                            </div>
                        </div>

                        <% if(isAdmin) { %>
                        <div id="event-form-view" class="d-none animate-fade-in">
                            <div class="d-flex justify-content-between align-items-center mb-3">
                                <h5 class="fw-bold text-primary mb-0"><i class="fa-solid fa-calendar-days me-2"></i>Nuovo Evento</h5>
                                <button type="button" class="btn-close" onclick="closeForms()"></button>
                            </div>

                            <form action="CreaEventoServlet" method="POST" enctype="multipart/form-data">
                                <input type="hidden" name="idGruppo" value="<%= gruppo.getId_gruppo() %>">
                                <div class="row g-3">
                                    <div class="col-12">
                                        <label class="small fw-bold text-muted">Nome Evento</label>
                                        <input type="text" name="nomeEvento" class="form-control" required>
                                    </div>
                                    <div class="col-12">
                                        <label class="small fw-bold text-muted">Descrizione</label>
                                        <textarea name="descrizione" class="form-control" rows="3" required></textarea>
                                    </div>
                                    <div class="col-md-6">
                                        <label class="small fw-bold text-muted">Data e Ora</label>
                                        <input type="datetime-local" name="dataOraEvento" class="form-control" required>
                                    </div>
                                    <div class="col-md-6">
                                        <label class="small fw-bold text-muted">Costo (€)</label>
                                        <input type="number" name="costo" step="0.01" min="0" class="form-control">
                                    </div>
                                    <div class="col-md-6">
                                        <label class="small fw-bold text-muted">Capienza Max</label>
                                        <input type="number" name="capienza" min="1" class="form-control">
                                    </div>
                                    <div class="col-12">
                                        <label class="small fw-bold text-muted">Locandina</label>
                                        <input type="file" name="foto" class="form-control" accept="image/*">
                                    </div>
                                </div>
                                <div class="mt-4 d-flex justify-content-end gap-2">
                                    <button type="button" class="btn btn-light rounded-pill" onclick="closeForms()">Annulla</button>
                                    <button type="submit" class="btn btn-primary rounded-pill px-4">Pubblica</button>
                                </div>
                            </form>
                        </div>
                        <% } %>

                        <div id="notice-form-view" class="d-none animate-fade-in">
                            <div class="d-flex justify-content-between align-items-center mb-3">
                                <h5 class="fw-bold text-info mb-0"><i class="fa-solid fa-bullhorn me-2"></i>Nuova Comunicazione</h5>
                                <button type="button" class="btn-close" onclick="closeForms()"></button>
                            </div>
                            <form action="InviaComunicazioneGruppoServlet" method="POST" enctype="multipart/form-data">
                                <input type="hidden" name="idGruppo" value="<%= gruppo.getId_gruppo() %>">
                                <div class="mb-3">
                                    <input type="text" name="titolo" class="form-control fw-bold" placeholder="Titolo" required>
                                </div>
                                <div class="mb-3">
                                    <textarea name="contenuto" class="form-control bg-light" rows="4" placeholder="Messaggio..." required></textarea>
                                </div>
                                <div class="mb-3">
                                    <input type="file" name="foto" class="form-control" accept="image/*">
                                </div>
                                <div class="d-flex justify-content-end gap-2">
                                    <button type="button" class="btn btn-light rounded-pill" onclick="closeForms()">Annulla</button>
                                    <button type="submit" class="btn btn-info text-white rounded-pill px-4">Pubblica</button>
                                </div>
                            </form>
                        </div>
                    </div>
                    <% } %>

                    <%
                        // RECUPERO E MERGE LISTE
                        EventoDAO eventoDAO = new EventoDAO();
                        List<EventoBean> eventi = eventoDAO.doRetrievebyGroup(ConPool.getConnection(), gruppo.getId_gruppo());

                        ComunicazioneDAO postDAO = new ComunicazioneDAO();
                        List<ComunicazioniBean> posts = postDAO.doRetrievebyGruppo(ConPool.getConnection(), gruppo.getId_gruppo());

                        List<Object> feedItems = new ArrayList<>();
                        if(eventi != null) feedItems.addAll(eventi);
                        if(posts != null) feedItems.addAll(posts);

                        // ORDINAMENTO
                        Collections.sort(feedItems, new Comparator<Object>() {
                            @Override
                            public int compare(Object o1, Object o2) {
                                LocalDateTime d1 = (o1 instanceof EventoBean) ? ((EventoBean)o1).getData_ora() : ((ComunicazioniBean)o1).getDataPubblicazione();
                                LocalDateTime d2 = (o2 instanceof EventoBean) ? ((EventoBean)o2).getData_ora() : ((ComunicazioniBean)o2).getDataPubblicazione();
                                if (d1 == null) return 1; if (d2 == null) return -1;
                                return d2.compareTo(d1);
                            }
                        });

                    %>

                    <% if (feedItems.isEmpty()) { %>
                    <div class="text-center py-5 text-muted">
                        <i class="fa-regular fa-newspaper fs-1 mb-3 opacity-25"></i>
                        <p>Non ci sono ancora post in questo gruppo.</p>
                    </div>
                    <% } else { %>
                    <% for (Object item : feedItems) { %>

                    <% if (item instanceof EventoBean) {
                        EventoBean ev = (EventoBean) item;
                    %>

                    <div class="card border-0 shadow-sm rounded-4 mb-4 animate-fade-in">
                        <div class="card-body p-4">

                            <div class="d-flex justify-content-between align-items-center mb-3">
                                <div class="d-flex align-items-center gap-2">
                <span class="badge rounded-pill bg-primary bg-opacity-10 text-primary fw-bold px-3 py-2">
                    <i class="fa-solid fa-calendar-check me-1"></i> EVENTO
                </span>
                                    <small class="text-muted fw-bold">
                                        <%= (ev.getData_ora() != null)
                                                ? ev.getData_ora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                                                : "Data da definire" %>
                                    </small>
                                </div>

                                <% if(isAdmin) { %>
                                <div class="dropdown">
                                    <button class="btn btn-link text-muted p-0" data-bs-toggle="dropdown"><i class="fa-solid fa-ellipsis"></i></button>
                                    <ul class="dropdown-menu dropdown-menu-end border-0 shadow">
                                        <li><a class="dropdown-item text-danger" href="#">Elimina Evento</a></li>
                                    </ul>
                                </div>
                                <% } %>
                            </div>

                            <h4 class="fw-bold text-dark mb-2"><%= ev.getNome() %></h4>
                            <p class="text-muted mb-3"><%= ev.getDescrizione() %></p>

                            <% if (ev.getFoto() != null && !ev.getFoto().isEmpty()) { %>
                            <div class="mb-4 position-relative">
                                <img src="<%= ev.getFoto() %>"
                                     class="img-fluid w-100 rounded-4 shadow-sm border"
                                     style="max-height: 400px; object-fit: cover; cursor: zoom-in;"
                                     alt="<%= ev.getNome() %>"
                                     data-bs-toggle="modal"
                                     data-bs-target="#modalEv<%= ev.getId_evento() %>">
                            </div>

                            <div class="modal fade" id="modalEv<%= ev.getId_evento() %>" tabindex="-1" aria-hidden="true">
                                <div class="modal-dialog modal-dialog-centered modal-xl">
                                    <div class="modal-content bg-transparent border-0 text-center" style="pointer-events: none;">

                                        <button type="button" class="btn-close btn-close-white position-absolute top-0 end-0 m-3"
                                                data-bs-dismiss="modal" style="pointer-events: auto; z-index: 1060;"></button>

                                        <div class="modal-body p-0 d-flex justify-content-center align-items-center" style="min-height: 100vh;">
                                            <img src="<%= ev.getFoto() %>"
                                                 class="rounded-3 shadow-lg"
                                                 style="max-height: 90vh; max-width: 95vw; width: auto; height: auto; object-fit: contain; pointer-events: auto;"
                                                 alt="Ingrandimento">
                                        </div>

                                    </div>
                                </div>
                            </div>
                            <% } %>

                            <div class="bg-light p-3 rounded-4 mb-4 border">
                                <div class="row g-0 align-items-center">

                                    <div class="col-6 border-end pe-3">
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
                                            <i class="fa-solid fa-users me-2"></i>Capienza: <%= ev.getCapienza_massima() %>
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

                                <button type="submit" class="btn btn-club-teal w-100 text-green fw-bold">
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
                    </div>

                    <% } %>

                    <% if (item instanceof ComunicazioniBean) {
                        ComunicazioniBean post = (ComunicazioniBean) item; %>
                    <div class="info-card mb-4 border-start border-4 border-info">
                        <div class="d-flex align-items-center gap-2 mb-3">
                            <span class="badge bg-info bg-opacity-10 text-info rounded-pill">Comunicazione</span>
                            <small class="text-muted">
                                <%= (post.getDataPubblicazione() != null)
                                        ? post.getDataPubblicazione().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                                        : "" %>
                            </small>
                        </div>
                        <h5 class="fw-bold"><%= post.getTitolo() %></h5>
                        <p style="white-space: pre-line;"><%= post.getContenuto() %></p>

                        <% if (post.getFoto() != null && !post.getFoto().isEmpty()) { %>
                        <img src="<%= post.getFoto() %>" class="img-fluid rounded-3 w-100" style="max-height: 400px; object-fit: cover; cursor: zoom-in;" data-bs-toggle="modal" data-bs-target="#imgModalCom<%= post.getId_comunicazione() %>">

                        <div class="modal fade" id="imgModalCom<%= post.getId_comunicazione() %>" tabindex="-1">
                            <div class="modal-dialog modal-xl modal-dialog-centered">
                                <div class="modal-content bg-transparent border-0 text-center">
                                    <img src="<%= post.getFoto() %>" class="img-fluid rounded shadow" style="max-height: 90vh;">
                                </div>
                            </div>
                        </div>
                        <% } %>
                    </div>
                    <% } %>

                    <% } %>
                    <% } %>

                </div>

                <div class="tab-pane fade" id="about" role="tabpanel">

                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <h4 class="fw-bold mb-0 text-dark">
                            <i class="fa-solid fa-bullhorn me-2 text-info"></i>Bacheca Comunicazioni
                        </h4>
                    </div>

                    <%
                        // 1. Recupero la lista specifica delle comunicazioni dal request
                        // (Assicurati che la Servlet faccia request.setAttribute("comunicazioni", ...))
                        java.util.List<Application.GestioneComunicazioni.ComunicazioniBean> listaCom =
                                (java.util.List<Application.GestioneComunicazioni.ComunicazioniBean>) request.getAttribute("comunicazioni");

                        if (listaCom != null && !listaCom.isEmpty()) {
                            for (Application.GestioneComunicazioni.ComunicazioniBean post : listaCom) {
                    %>

                    <div class="info-card mb-4 border-start border-4 border-info bg-white shadow-sm p-4 rounded-3">

                        <div class="d-flex align-items-center justify-content-between mb-3">
                            <div class="d-flex align-items-center gap-2">
                <span class="badge bg-info bg-opacity-10 text-info rounded-pill px-3 py-2">
                    <i class="fa-solid fa-newspaper me-1"></i> News
                </span>
                            </div>
                            <small class="text-muted fw-bold">
                                <i class="fa-regular fa-clock me-1"></i>
                                <%= (post.getDataPubblicazione() != null)
                                        ? post.getDataPubblicazione().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                                        : "--/--/----" %>
                            </small>
                        </div>

                        <h5 class="fw-bold text-dark mb-3"><%= post.getTitolo() %></h5>

                        <p class="text-secondary lh-lg mb-3" style="white-space: pre-line;"><%= post.getContenuto() %></p>

                        <% if (post.getFoto() != null && !post.getFoto().isEmpty()) { %>
                        <div class="mt-3">
                            <img src="<%= post.getFoto() %>"
                                 class="img-fluid rounded-4 w-100 border"
                                 style="max-height: 350px; object-fit: cover; cursor: zoom-in;"
                                 alt="Allegato"
                                 data-bs-toggle="modal"
                                 data-bs-target="#modalCom<%= post.getId_comunicazione() %>">
                        </div>

                        <div class="modal fade" id="modalCom<%= post.getId_comunicazione() %>" tabindex="-1">
                            <div class="modal-dialog modal-xl modal-dialog-centered">
                                <div class="modal-content bg-transparent border-0 text-center">
                                    <div class="position-absolute top-0 end-0 m-3" style="z-index: 1055;">
                                        <button type="button" class="btn-close btn-close-white bg-white rounded-circle p-2" data-bs-dismiss="modal"></button>
                                    </div>
                                    <img src="<%= post.getFoto() %>" class="img-fluid rounded shadow-lg" style="max-height: 90vh;">
                                </div>
                            </div>
                        </div>
                        <% } %>

                    </div>

                    <%
                        } // Fine For
                    } else {
                    %>

                    <div class="text-center py-5 border rounded-4 bg-light">
                        <div class="mb-3 text-muted opacity-50">
                            <i class="fa-regular fa-comments fa-3x"></i>
                        </div>
                        <h6 class="fw-bold text-muted">Nessuna comunicazione</h6>
                        <p class="small text-muted mb-0">Non ci sono ancora avvisi in questa bacheca.</p>
                    </div>

                    <% } %>

                </div>

                <div class="tab-pane fade" id="events" role="tabpanel">
                    <div class="info-card">
                        <div class="d-flex justify-content-between align-items-center mb-4">
                            <h4 class="mb-0">Calendario Eventi</h4>
                        </div>

                        <% if (eventi == null || eventi.isEmpty()) { %>
                        <div class="text-center py-5 text-muted">
                            <i class="fa-regular fa-calendar-xmark fs-1 mb-3 opacity-25"></i>
                            <p>Nessun evento in programma.</p>
                        </div>
                        <% } else { %>
                        <% for(EventoBean ev : eventi) { %>
                        <div class="d-flex gap-3 border-bottom pb-3 mb-3">
                            <div class="text-center bg-light rounded p-2 border" style="min-width: 80px;">
                                <div class="small text-uppercase text-danger fw-bold"><%= ev.getData_ora().getMonth() %></div>
                                <div class="fs-4 fw-bold"><%= ev.getData_ora().getDayOfMonth() %></div>
                            </div>
                            <div>
                                <h5 class="fw-bold mb-1"><%= ev.getNome() %></h5>
                                <p class="text-muted small mb-1"><i class="fa-regular fa-clock me-1"></i> <%= ev.getData_ora().format(DateTimeFormatter.ofPattern("HH:mm")) %></p>
                                <a href="VisualizzaEventoServlet?id=<%= ev.getId_evento() %>" class="btn btn-sm btn-link p-0 text-decoration-none">Vedi dettagli &rarr;</a>
                            </div>
                        </div>
                        <% } %>
                        <% } %>
                    </div>
                </div> </div>
        </div>
    </div>
</div>

<% if(isAdmin) { %>
<div class="modal fade" id="modalEditInfo" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header border-0">
                <h5 class="modal-title fw-bold">Modifica Gruppo</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form action="ModificaGruppoServlet" method="POST">
                <input type="hidden" name="idGruppo" value="<%= gruppo.getId_gruppo() %>">
                <div class="modal-body">
                    <div class="mb-3">
                        <label class="form-label">Nome</label>
                        <input type="text" class="form-control" name="nome" value="<%= gruppo.getNome() %>">
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Descrizione</label>
                        <textarea class="form-control" name="descrizione"><%= gruppo.getDescrizione() %></textarea>
                    </div>
                </div>
                <div class="modal-footer border-0">
                    <button type="button" class="btn btn-light" data-bs-dismiss="modal">Annulla</button>
                    <button type="submit" class="btn btn-primary">Salva</button>
                </div>
            </form>
        </div>
    </div>
</div>

<div class="modal fade" id="modalEditImages" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header border-0">
                <h5 class="modal-title">Aggiorna Immagini</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form action="UploadImmagineServlet" method="POST" enctype="multipart/form-data">
                <input type="hidden" name="idGruppo" value="<%= gruppo.getId_gruppo() %>">
                <div class="modal-body">
                    <div class="mb-3">
                        <label class="form-label">Tipo</label>
                        <select name="tipoImg" class="form-select">
                            <option value="logo">Logo</option>
                            <option value="copertina">Copertina</option>
                        </select>
                    </div>
                    <input type="file" name="file" class="form-control" required>
                </div>
                <div class="modal-footer border-0">
                    <button type="submit" class="btn btn-primary">Carica</button>
                </div>
            </form>
        </div>
    </div>
</div>
<% } %>

<script>
    function openForm(type) {
        document.getElementById('default-post-view').classList.add('d-none');
        document.getElementById('event-form-view')?.classList.add('d-none');
        document.getElementById('notice-form-view').classList.add('d-none');

        if (type === 'event') document.getElementById('event-form-view').classList.remove('d-none');
        else if (type === 'notice') document.getElementById('notice-form-view').classList.remove('d-none');
    }

    function closeForms() {
        document.getElementById('event-form-view')?.classList.add('d-none');
        document.getElementById('notice-form-view').classList.add('d-none');
        document.getElementById('default-post-view').classList.remove('d-none');
    }
</script>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>