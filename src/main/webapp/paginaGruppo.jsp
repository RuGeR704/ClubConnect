<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Application.GestioneAccount.UtenteBean" %>
<%@ page import="Application.GestioneGruppo.GruppoBean" %>
<%@ page import="Application.GestioneGruppo.ClubBean" %>
<%@ page import="Application.GestioneGruppo.AssociazioneBean" %>

<%
    // 1. RECUPERO DATI DALLA SERVLET (GruppoServlet)
    UtenteBean utente = (UtenteBean) session.getAttribute("utente");

    // Il gruppo da visualizzare
    GruppoBean gruppo = (GruppoBean) request.getAttribute("gruppo");

    // Flag di permessi (calcolati dalla Servlet)
    boolean isAdmin = (request.getAttribute("isAdmin") != null) ? (boolean) request.getAttribute("isAdmin") : false;
    boolean isIscritto = (request.getAttribute("isIscritto") != null) ? (boolean) request.getAttribute("isIscritto") : false;

    // Fallback se si apre la pagina direttamente senza servlet (per test grafico)
    if (gruppo == null) {
        response.sendRedirect("feed.jsp"); // O mostra errore
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
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style-bootstrap.css">

    <style>
        body {
            background-color: transparent; /* Era #F4F6F9 */
        }

        .group-header {
            background-color: transparent; /* Era white <--- QUESTO COPRIVA TUTTO */
            padding-bottom: 1rem;
            /* Rimuovi l'ombra qui perché l'header è trasparente, starebbe male */
            /* box-shadow: 0 2px 10px rgba(0,0,0,0.05); */
            margin-bottom: 2rem;
        }

        .cover-image {
            height: 300px;
            width: 100%;
            /* background-color: #1E3A5F;  <-- RIMUOVI O COMMENTA QUESTA RIGA */
            /* background-image: ...;       <-- RIMUOVI O COMMENTA QUESTA RIGA */

            position: relative;
            /* Opzionale: un leggero gradiente per far risaltare il tasto modifica */
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
            position: relative;
            margin-top: -80px; /* Sovrapposizione */
            margin-left: 20px;
            width: 160px; height: 160px;
        }

        .group-avatar {
            width: 100%; height: 100%;
            border-radius: 50%;
            border: 5px solid white;
            background: white;
            object-fit: cover;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
        }

        .edit-avatar-btn {
            position: absolute; bottom: 5px; right: 5px;
            width: 40px; height: 40px;
            border-radius: 50%;
            background: #26A9BC; color: white;
            border: 3px solid white;
            display: flex; align-items: center; justify-content: center;
            cursor: pointer;
        }

        /* LAYOUT CONTENUTI */
        .info-card {
            background: white; border: none; border-radius: 16px;
            box-shadow: 0 2px 6px rgba(0,0,0,0.02); padding: 1.5rem; margin-bottom: 1.5rem;
        }

        .stat-box {
            text-align: center; padding: 10px;
            border-right: 1px solid #eee;
        }
        .stat-box:last-child { border-right: none; }

        .btn-join { background-color: #26A9BC; color: white; border: none; padding: 10px 30px; border-radius: 30px; font-weight: bold; }
        .btn-join:hover { background-color: #1e8a9b; color: white; }

        .btn-outline-admin { border: 2px solid #1E3A5F; color: #1E3A5F; border-radius: 30px; font-weight: bold; }
        .btn-outline-admin:hover { background: #1E3A5F; color: white; }

        /* 1. Stile di base per le tab NON selezionate (Grigie) */
        .nav-pills .nav-link {
            color: #6c757d; /* Grigio scuro (corrisponde a text-secondary) */
            background-color: transparent;
            transition: all 0.3s ease; /* Transizione fluida */
        }

        /* 2. Stile per la tab ATTIVA (Quella selezionata) */
        .nav-pills .nav-link.active {
            background-color: #26A9BC; /* Il tuo colore "Club Teal" (o usa #1E3A5F per il blu scuro) */
            color: white !important;   /* Testo bianco */
            box-shadow: 0 4px 6px rgba(38, 169, 188, 0.3); /* Un po' di ombretta carina */
        }

        /* 3. Opzionale: Effetto quando passi il mouse sopra una tab non attiva */
        .nav-pills .nav-link:hover:not(.active) {
            background-color: rgba(38, 169, 188, 0.1); /* Sfondo azzurrino chiarissimo */
            color: #26A9BC; /* Testo colorato */
        }

        /* SFONDO DINAMICO ADATTIVO */
        .ambient-background {
            position: fixed; /* Meglio di absolute, così resta fisso se scrolli */
            top: 0; left: 0;
            width: 100%; height: 100vh;
            z-index: -1;
            overflow: hidden;
            background-color: #f4f6f9;
        }

        .ambient-background::before {
            content: "";
            position: absolute;
            top: -50px; left: -50px; right: -50px; bottom: -50px;
            background-image: var(--bg-image); /* Variabile CSS dinamica */
            background-size: cover;
            background-position: center;

            /* LA MAGIA: Sfocatura pesante e saturazione */
            filter: blur(60px) saturate(150%) brightness(0.8);
            opacity: 0.6; /* Trasparenza per non renderlo troppo forte */

            /* Maschera per sfumare verso il basso e non avere un taglio netto */
            mask-image: linear-gradient(to bottom, black, transparent);
            mask-image: linear-gradient(to bottom, black, transparent);
        }
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
                <button class="btn btn-outline-danger rounded-pill fw-bold ms-2">
                    <i class="fa-solid fa-right-from-bracket"></i>
                </button>
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
                    <a class="nav-link rounded-pill fw-bold" id="about-tab" data-bs-toggle="tab" href="#about" role="tab">Informazioni</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link rounded-pill fw-bold" id="events-tab" data-bs-toggle="tab" href="#events" role="tab">Eventi</a>
                </li>
            </ul>
        </div>
    </div>
</div>

<br>

<div class="container">
    <div class="row g-4">

        <div class="col-lg-4">

            <div class="info-card d-flex justify-content-around py-3">
                <div class="stat-box">
                    <h5 class="fw-bold mb-0 text-primary">125</h5>
                    <small class="text-muted">Membri</small>
                </div>
                <div class="stat-box">
                    <h5 class="fw-bold mb-0 text-primary">12</h5>
                    <small class="text-muted">Eventi</small>
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
                            String freqLeggibile = "Sconosciuta";
                            if(f == 1) freqLeggibile = "Settimanale";
                            else if(f == 2) freqLeggibile = "Mensile";
                            else if(f == 3) freqLeggibile = "Annuale";
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
                    <div class="info-card p-3 mb-4">
                        <div class="d-flex gap-3">
                            <img src="https://ui-avatars.com/api/?name=<%= utente.getNome() %>" class="rounded-circle" width="45" height="45">
                            <input type="text" class="form-control rounded-pill bg-light border-0" placeholder="Scrivi qualcosa al gruppo...">
                        </div>
                        <div class="d-flex justify-content-end mt-2 gap-2">
                            <% if(isAdmin) { %>
                            <button class="btn btn-sm btn-light text-primary"><i class="fa-solid fa-calendar-plus me-1"></i> Evento</button>
                            <button class="btn btn-sm btn-light text-info"><i class="fa-solid fa-bullhorn me-1"></i> Avviso</button>
                            <% } %>
                            <button class="btn btn-sm btn-club-primary rounded-pill px-4">Pubblica</button>
                        </div>
                    </div>
                    <% } %>

                    <div class="text-center py-5 text-muted">
                        <i class="fa-regular fa-newspaper fs-1 mb-3 opacity-25"></i>
                        <p>Non ci sono ancora post in questo gruppo.</p>
                    </div>
                </div>

                <div class="tab-pane fade" id="about" role="tabpanel">
                    <div class="info-card">
                        <h4>Storia del gruppo</h4>
                        <p>Qui potresti mettere una descrizione più lunga...</p>
                    </div>
                </div>
            </div>
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
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label small fw-bold">Nome Gruppo</label>
                            <input type="text" class="form-control" name="nome" value="<%= gruppo.getNome() %>">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label small fw-bold">Slogan</label>
                            <input type="text" class="form-control" name="slogan" value="<%= gruppo.getSlogan() %>">
                        </div>
                        <div class="col-12">
                            <label class="form-label small fw-bold">Descrizione</label>
                            <textarea class="form-control" name="descrizione" rows="3"><%= gruppo.getDescrizione() %></textarea>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label small fw-bold">Sede</label>
                            <input type="text" class="form-control" name="sede" value="<%= gruppo.getSede() %>">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label small fw-bold">Settore</label>
                            <select class="form-select" name="settore">
                                <option value="<%= gruppo.getSettore() %>" selected><%= gruppo.getSettore() %></option>
                                <option value="Sport">Sport</option>
                                <option value="Cultura">Cultura</option>
                                <option value="Svago">Svago</option>
                            </select>
                        </div>
                        <div class="col-12">
                            <label class="form-label small fw-bold">Regole</label>
                            <textarea class="form-control" name="regole" rows="2"><%= gruppo.getRegole() %></textarea>
                        </div>

                        <% if(gruppo instanceof ClubBean) {
                            ClubBean c = (ClubBean) gruppo; %>
                        <div class="col-md-6">
                            <label class="form-label small fw-bold">Retta (€)</label>
                            <input type="number" step="0.01" class="form-control" name="retta" value="<%= c.getImporto_retta() %>">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label small fw-bold">Frequenza</label>
                            <select class="form-select" name="frequenza">
                                <option value="<%= c.getFrequenza() %>" selected><%= c.getFrequenza() %></option>
                                <option value="Mensile">Mensile</option>
                                <option value="Annuale">Annuale</option>
                            </select>
                        </div>
                        <% } %>
                    </div>
                </div>
                <div class="modal-footer border-0">
                    <button type="button" class="btn btn-light" data-bs-dismiss="modal">Annulla</button>
                    <button type="submit" class="btn btn-club-primary">Salva Modifiche</button>
                </div>
            </form>
        </div>
    </div>
</div>

<div class="modal fade" id="modalEditImages" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header border-0">
                <h5 class="modal-title fw-bold">Aggiorna Immagini</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form action="UploadImmagineServlet" method="POST" enctype="multipart/form-data">
                <input type="hidden" name="idGruppo" value="<%= gruppo.getId_gruppo() %>">
                <div class="modal-body text-center">
                    <p class="text-muted small">Carica una nuova immagine per il logo o la copertina.</p>

                    <div class="mb-3">
                        <label class="form-label fw-bold">Tipo Immagine</label>
                        <div class="btn-group w-100" role="group">
                            <input type="radio" class="btn-check" name="tipoImg" id="radioLogo" value="logo" checked>
                            <label class="btn btn-outline-primary" for="radioLogo">Logo (Avatar)</label>

                            <input type="radio" class="btn-check" name="tipoImg" id="radioCover" value="copertina">
                            <label class="btn btn-outline-primary" for="radioCover">Sfondo</label>
                        </div>
                    </div>

                    <div class="mb-3">
                        <input type="file" class="form-control" name="file" accept="image/*" required>
                    </div>
                </div>
                <div class="modal-footer border-0">
                    <button type="submit" class="btn btn-club-teal text-white w-100">Carica Immagine</button>
                </div>
            </form>
        </div>
    </div>
</div>

<% } %>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
