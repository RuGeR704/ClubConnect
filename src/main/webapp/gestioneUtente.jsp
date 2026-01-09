<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Application.GestioneAccount.UtenteBean" %>
<%@ page import="Application.GestionePagamenti.MetodoPagamentoBean" %>
<%@ page import="java.util.List" %>
<%
    UtenteBean utente = (UtenteBean) session.getAttribute("utente");
    if(utente == null) { response.sendRedirect("login.jsp"); return; }

    String errorMsg = (String) session.getAttribute("errorMsg");
    if (errorMsg != null) { session.removeAttribute("errorMsg"); }

    // Recupero la lista caricata dalla VisualizzaMetodiPagamentoServlet
    List<MetodoPagamentoBean> metodipagamento = (List<MetodoPagamentoBean>) request.getAttribute("metodipagamento");

    // Capiamo quale tab mostrare (se i dati o i pagamenti)
    boolean showPagamenti = (metodipagamento != null);
%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Profilo - ClubConnect</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <style>
        body { background-color: #F4F6F9; }
        .navbar-main { background: white; box-shadow: 0 2px 10px rgba(0,0,0,0.05); position: sticky; top: 0; z-index: 1000; }
        .feed-card { background: white; border: none; border-radius: 16px; box-shadow: 0 2px 6px rgba(0,0,0,0.02); margin-bottom: 1.5rem; overflow: hidden; }
        .profile-card-header { background: linear-gradient(135deg, #1E3A5F, #26A9BC); height: 100px; }
        .profile-avatar { width: 80px; height: 80px; background: white; border-radius: 50%; padding: 4px; margin-top: -40px; margin-bottom: 10px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); }
        .content-card { background: white; border-radius: 16px; padding: 2.5rem; box-shadow: 0 2px 6px rgba(0,0,0,0.02); }
        .btn-club-teal { background-color: #26A9BC; color: white; border: none; }

        /* Stili Alert */
        .alert-custom-error { background-color: #fef2f2; border-left: 4px solid #E65142; color: #991b1b; border-radius: 12px; }
    </style>
</head>
<body>

<nav class="navbar navbar-expand-lg navbar-main py-2">
    <div class="container">
        <a href="feedServlet" class="d-inline-flex align-items-center gap-2 text-decoration-none">
            <div class="brand-icon" style="width: 40px; height: 40px;"><img src="./images/logo.png" alt="Logo" class="img-fluid"></div>
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
                <ul class="nav flex-column gap-2">
                    <li class="nav-item">
                        <a href="ModificaDatiServlet" class="nav-link <%= !showPagamenti ? "active text-primary fw-bold bg-light" : "text-secondary" %> d-flex align-items-center gap-3 rounded p-2 text-decoration-none">
                            <i class="fa-solid fa-house"></i> Dati Personali
                        </a>
                    </li>
                    <li class="nav-item">
                        <a href="VisualizzaMetodidiPagamentoServlet" class="nav-link <%= showPagamenti ? "active text-primary fw-bold bg-light" : "text-secondary" %> d-flex align-items-center gap-3 rounded p-2 text-decoration-none">
                            <i class="fa-solid fa-credit-card"></i> Metodi di pagamento
                        </a>
                    </li>
                </ul>
            </div>
        </div>

        <div class="col-lg-9">

            <% if (errorMsg != null) { %>
            <div class="alert alert-custom-error shadow-sm d-flex align-items-center mb-4"><i class="fa-solid fa-circle-exclamation fs-4 me-3"></i><div><%= errorMsg %></div></div>
            <% } %>

            <div class="content-card">
                <% if (!showPagamenti) { %>
                <h4 class="fw-bold mb-4" style="color: #1E3A5F;">Modifica Profilo</h4>
                <form action="ModificaDatiServlet" method="POST">
                    <div class="row g-3">
                        <div class="col-md-6"><div class="form-floating"><input type="text" class="form-control" name="nome" value="<%= utente.getNome() %>" required><label>Nome</label></div></div>
                        <div class="col-md-6"><div class="form-floating"><input type="text" class="form-control" name="cognome" value="<%= utente.getCognome() %>" required><label>Cognome</label></div></div>
                        <div class="col-md-6"><div class="form-floating"><input type="text" class="form-control" name="username" value="<%= utente.getUsername() %>" required><label>Username</label></div></div>
                        <div class="col-md-6"><div class="form-floating"><input type="email" class="form-control" name="email" value="<%= utente.getEmail() %>" required><label>Email</label></div></div>
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
                        <div class="col-12 mt-3 pt-3 border-top"><div class="form-floating"><input type="password" class="form-control" name="password" required><label>Password per confermare</label></div></div>
                    </div>
                    <button type="submit" class="btn btn-primary w-100 mt-4 py-3 rounded-4 fw-bold shadow-sm" style="background-color: #1E3A5F; border: none;">Salva Modifiche</button>
                </form>
                <% } else { %>
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <h4 class="fw-bold mb-0" style="color: #1E3A5F;">I Tuoi Metodi di Pagamento</h4>
                    <button class="btn btn-club-teal btn-sm rounded-pill px-3 fw-bold" data-bs-toggle="modal" data-bs-target="#modalPagamento">
                        <i class="fa-solid fa-plus me-1"></i> Aggiungi Metodo
                    </button>
                </div>

                <div class="row g-3">
                    <% if (metodipagamento != null && !metodipagamento.isEmpty()) { %>
                    <% for (MetodoPagamentoBean mp : metodipagamento) { %>
                    <div class="col-md-6">
                        <div class="p-3 border rounded-4 bg-light d-flex align-items-center justify-content-between shadow-sm">
                            <div class="d-flex align-items-center gap-3">
                                <i class="fa-solid fa-credit-card fs-3 text-primary"></i>
                                <div>
                                    <h6 class="mb-0 fw-bold">Carta **** <%= mp.getNumero_carta().substring(mp.getNumero_carta().length() - 4) %></h6>
                                    <small class="text-muted"><%= mp.getScadenza_carta() %></small>
                                </div>
                            </div>

                            <form action="RimuoviMetodoPagamentoServlet" method="POST" onsubmit="return confirm('Sei sicuro di voler rimuovere questa carta?');">
                                <input type="hidden" name="id_metodo_pagamento" value="<%= mp.getId_metodo() %>">
                                <button type="submit" class="btn btn-link text-danger p-0 border-0">
                                    <i class="fa-solid fa-trash-can fs-5"></i>
                                </button>
                            </form>
                        </div>
                    </div>
                    <% } %>
                    <% } else { %>
                    <div class="col-12 text-center py-5">

                        <div class="mb-3 text-muted opacity-50">
                            <i class="fa-regular fa-credit-card fa-3x"></i>
                        </div>
                        <h5 class="fw-bold text-muted">Nessun metodo di pagamento</h5>
                        <p class="text-muted small mb-0">Non hai ancora salvato nessuna carta.</p>
                        <button class="btn btn-link text-decoration-none fw-bold" data-bs-toggle="modal" data-bs-target="#modalPagamento">Aggiungine una ora</button>
                    </div>

                    <% } %>
                </div>
                <% } %>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="modalPagamento" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow rounded-4">
            <div class="modal-header border-0 pb-0">
                <h5 class="fw-bold" style="color: #1E3A5F;">Inserisci Dati Carta</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form action="AggiungiMetodoPagamentoServlet" method="POST">
                <div class="modal-body">
                    <div class="row g-2 mb-3">
                        <div class="col-6"><div class="form-floating"><input type="text" class="form-control" name="nome_intestatario" placeholder="Nome" required><label>Nome Intestatario</label></div></div>
                        <div class="col-6"><div class="form-floating"><input type="text" class="form-control" name="cognome_intestatario" placeholder="Cognome" required><label>Cognome Intestatario</label></div></div>
                    </div>
                    <div class="form-floating mb-3">
                        <input type="text" class="form-control" name="numero_carta" placeholder="Numero Carta" maxlength="16" required>
                        <label>Numero Carta</label>
                    </div>
                    <div class="form-floating mb-3">
                        <input type="text"
                               class="form-control"
                               name="scandenza_carta"
                               id="scadenzaInput"
                               placeholder="MM/YYYY"
                               maxlength="7"
                               pattern="(0[1-9]|1[0-2])\/20[2-9][0-9]"
                               title="Formato richiesto: MM/YYYY (es. 05/2026)"
                               required>
                        <label for="scadenzaInput">Data Scadenza (MM/YYYY)</label>
                        <div class="form-text">Inserisci il mese (01-12) e l'anno completo (es. 08/2027)</div>
                    </div>
                </div>
                <div class="modal-footer border-0">
                    <button type="submit" class="btn btn-club-teal rounded-pill px-5 py-2 fw-bold w-100">Salva Metodo di Pagamento</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    document.getElementById('scadenzaInput').addEventListener('input', function (e) {
        var input = e.target.value;
        if (input.length === 2 && !input.includes('/')) {
            e.target.value = input + '/';
        }
    });
</script>
</body>
</html>