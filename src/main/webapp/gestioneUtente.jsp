<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Application.GestioneAccount.UtenteBean" %>
<%
    UtenteBean utente = (UtenteBean) session.getAttribute("utente");
    if(utente == null) { response.sendRedirect("login.jsp"); return; }

    String errorMsg = (String) session.getAttribute("errorMsg");
    if (errorMsg != null) {
        session.removeAttribute("errorMsg");
    }
%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Profilo - ClubConnect</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style-bootstrap.css">

    <style>
        body { background-color: #F4F6F9; }
        .navbar-main { background: white; box-shadow: 0 2px 10px rgba(0,0,0,0.05); position: sticky; top: 0; z-index: 1000; }
        .feed-card { background: white; border: none; border-radius: 16px; box-shadow: 0 2px 6px rgba(0,0,0,0.02); margin-bottom: 1.5rem; overflow: hidden; }

        /* Sidebar & Profilo (Copiati da feed.jsp) */
        .profile-card-header { background: linear-gradient(135deg, #1E3A5F, #26A9BC); height: 100px; }
        .profile-avatar { width: 80px; height: 80px; background: white; border-radius: 50%; padding: 4px; margin-top: -40px; margin-bottom: 10px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); }

        /* Stile selezione Navigazione (Esatto stile feed.jsp) */
        .nav-link-feed {
            display: flex;
            align-items: center;
            gap: 15px;
            padding: 10px;
            border-radius: 8px;
            transition: 0.2s;
            color: #26A9BC; /* Colore secondario di default */
            text-decoration: none;
            border: none;
            background: none;
            width: 100%;
            font-weight: 500;
        }
        .nav-link-feed.active {
            background-color: #f8fafc; /* bg-light */
            color: #1E3A5F !important; /* text-primary */
            font-weight: bold;
        }

        .content-card { background: white; border-radius: 16px; padding: 2.5rem; box-shadow: 0 2px 6px rgba(0,0,0,0.02); }

        .alert-custom-error {
            background-color: #fef2f2;
            border-left: 4px solid #E65142;
            color: #991b1b;
            border-radius: 12px;
        }
    </style>
</head>
<body>

<nav class="navbar navbar-expand-lg navbar-main py-2">
    <div class="container">
        <a href="feed.jsp" class="d-inline-flex align-items-center gap-2 text-decoration-none">
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
                    <li><a class="dropdown-item" href="ModificaDatiServlet"><i class="fa-solid fa-user-circle me-2"></i> Dati utente</a></li>
                    <li><hr class="dropdown-divider opacity-25"></li>
                    <li><a class="dropdown-item text-danger" href="LogoutServlet"><i class="fa-solid fa-right-from-bracket me-2"></i> Esci</a></li>
                </ul>
            </div>
        </div>
    </div>
</nav>

<div class="container py-4">
    <div class="row g-4">

        <div class="col-lg-3">
            <div class="feed-card text-center pb-3">
                <div class="profile-card-header"></div>
                <img src="https://ui-avatars.com/api/?name=<%= utente.getNome() %>+<%= utente.getCognome() %>&background=1E3A5F&color=fff" class="profile-avatar shadow-sm">
                <h5 class="fw-bold mb-0 text-primary"><%= utente.getNome() %> <%= utente.getCognome() %></h5>
                <p class="text-muted small">@<%= utente.getUsername() %></p>
            </div>

            <div class="feed-card p-3">
                <div class="nav flex-column gap-2">
                    <button class="nav-link active d-flex align-items-center gap-3 text-primary fw-bold rounded bg-light p-2">
                        <i class="fa-solid fa-address-card"></i> Dati Personali
                    </button>
                </div>
            </div>

            <a href="crea_gruppo.jsp" class="btn btn-club-teal w-100 py-3 rounded-4 shadow-sm fw-bold" style="background-color: #26A9BC; color: white; border: none;">
                <i class="fa-solid fa-plus me-2"></i> Crea Nuovo Gruppo
            </a>
        </div>

        <div class="col-lg-6">

            <% if (errorMsg != null) { %>
            <div class="alert alert-custom-error shadow-sm d-flex align-items-center mb-4" role="alert">
                <i class="fa-solid fa-circle-exclamation fs-4 me-3"></i>
                <div><span class="fw-bold">Attenzione:</span> <%= errorMsg %></div>
            </div>
            <% } %>

            <div class="content-card">
                <h4 class="fw-bold mb-4" style="color: #1E3A5F;">Modifica Profilo</h4>

                <form action="ModificaDatiServlet" method="POST">
                    <div class="row g-3">
                        <div class="col-md-6">
                            <div class="form-floating mb-3">
                                <input type="text" class="form-control" name="nome" value="<%= utente.getNome() %>" required>
                                <label>Nome</label>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-floating mb-3">
                                <input type="text" class="form-control" name="cognome" value="<%= utente.getCognome() %>" required>
                                <label>Cognome</label>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-floating mb-3">
                                <input type="text" class="form-control" name="username" value="<%= utente.getUsername() %>" required>
                                <label>Username</label>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-floating mb-3">
                                <input type="email" class="form-control" name="email" value="<%= utente.getEmail() %>" required>
                                <label>Email</label>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-floating mb-3">
                                <input type="tel" class="form-control" name="cellulare" value="<%= (utente.getCellulare() != null) ? utente.getCellulare() : "" %>">
                                <label>Cellulare</label>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-floating mb-3">
                                <input type="date" class="form-control" name="datanascita" value="<%= utente.getData_nascita() %>" required>
                                <label>Data di Nascita</label>
                            </div>
                        </div>
                        <div class="col-12 mt-3 pt-3 border-top">
                            <div class="form-floating mb-3">
                                <input type="password" class="form-control" name="password" placeholder="Conferma Password" required>
                                <label>Password (corrente o nuova) per confermare</label>
                            </div>
                        </div>
                    </div>
                    <div class="mt-4">
                        <button type="submit" class="btn btn-primary w-100 py-3 rounded-4 fw-bold" style="background-color: #1E3A5F; border: none;">
                            Salva Modifiche
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <div class="col-lg-3">
            <div class="feed-card p-3">
                <h6 class="fw-bold text-primary mb-3">Informazioni Account</h6>
                <p class="small text-muted mb-0">I tuoi dati personali sono utilizzati esclusivamente per la gestione delle iscrizioni ai club e per le comunicazioni ufficiali[cite: 25, 26].</p>
            </div>
        </div>

    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>