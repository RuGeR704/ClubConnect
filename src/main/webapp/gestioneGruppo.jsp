<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Application.GestioneGruppo.GruppoBean" %>
<%@ page import="Application.GestioneGruppo.ClubBean" %>
<%@ page import="Application.GestioneEventi.EventoBean" %>
<%@ page import="java.util.List" %>
<%
    // Recupero dati dalla request
    GruppoBean gruppo = (GruppoBean) request.getAttribute("gruppo");

    // Gestione null safe per i numeri
    Integer totMembriObj = (Integer) request.getAttribute("totaleMembri");
    int totaleMembri = (totMembriObj != null) ? totMembriObj : 0;

    Double entrateObj = (Double) request.getAttribute("entrateMensili");
    double entrate = (entrateObj != null) ? entrateObj : 0.0;

    Integer nuoviObj = (Integer) request.getAttribute("nuoviIscritti");
    int nuovi = (nuoviObj != null) ? nuoviObj : 0;

    boolean isClub = (gruppo instanceof ClubBean);

    // Controllo sicurezza base
    if(gruppo == null) {
        response.sendRedirect("feedServlet");
        return;
    }
%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Dashboard <%= gruppo.getNome() %> | ClubConnect</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

    <style>
        :root {
            --sidebar-width: 260px;
            --club-dark: #1E3A5F;
            --club-teal: #26A9BC;
        }

        body { background-color: #f8f9fa; }

        /* SIDEBAR LATERALE FISSA */
        .dashboard-sidebar {
            width: var(--sidebar-width);
            height: 100vh;
            position: fixed;
            top: 0; left: 0;
            background: white;
            border-right: 1px solid #eee;
            padding: 20px;
            z-index: 1000;
        }

        .main-content {
            margin-left: var(--sidebar-width);
            padding: 30px;
        }

        /* CARD STATISTICHE (KPI) */
        .stat-card {
            background: white;
            border-radius: 16px;
            padding: 25px;
            border: none;
            box-shadow: 0 2px 15px rgba(0,0,0,0.03);
            transition: transform 0.2s;
            height: 100%;
        }
        .stat-card:hover { transform: translateY(-3px); }

        .stat-icon {
            width: 50px; height: 50px;
            border-radius: 12px;
            display: flex; align-items: center; justify-content: center;
            font-size: 1.5rem;
            margin-bottom: 15px;
        }

        .bg-icon-primary { background: rgba(30, 58, 95, 0.1); color: var(--club-dark); }
        .bg-icon-success { background: rgba(25, 135, 84, 0.1); color: #198754; }
        .bg-icon-warning { background: rgba(255, 193, 7, 0.1); color: #ffc107; }

        /* TABELLA */
        .dashboard-table thead th {
            font-weight: 600;
            color: #6c757d;
            border-bottom-width: 1px;
            font-size: 0.85rem;
            text-transform: uppercase;
        }

        .nav-pills .nav-link.active {
            background-color: var(--club-dark);
        }
        .nav-pills .nav-link { color: #555; font-weight: 500; }
        .nav-pills .nav-link:hover { background-color: #f1f3f5; }

        /* Effetto Hover per la card cliccabile */
        .transition-hover {
            transition: transform 0.2s ease, box-shadow 0.2s ease;
        }
        .transition-hover:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 20px rgba(0,0,0,0.1) !important;
            cursor: pointer;
        }
    </style>
</head>
<body>

<div class="dashboard-sidebar d-flex flex-column">
    <a href="feedServlet" class="d-flex align-items-center gap-2 mb-5 text-decoration-none">
        <img src="./images/logo.png" width="35" alt="Logo">
        <span class="fs-4 fw-bold" style="color: #1E3A5F;">Club<span style="color: #26A9BC;">Connect</span></span>
    </a>

    <div class="d-flex align-items-center gap-3 mb-4 p-3 rounded-3 bg-light">
        <img src="<%= (gruppo.getLogo() != null && !gruppo.getLogo().isEmpty()) ? gruppo.getLogo() : "images/default.png" %>"
             class="rounded-circle object-fit-cover" width="40" height="40">
        <div class="overflow-hidden">
            <h6 class="mb-0 text-truncate fw-bold"><%= gruppo.getNome() %></h6>
            <small class="text-muted">Gestione</small>
        </div>
    </div>

    <ul class="nav nav-pills flex-column gap-2">
        <li class="nav-item">
            <a href="#" class="nav-link active"><i class="fa-solid fa-chart-line me-2 w-25"></i> Panoramica</a>
        </li>
        <li class="nav-item">
            <a href="VisualizzaSociServlet?id=<%= gruppo.getId_gruppo()%>" class="nav-link"><i class="fa-solid fa-users me-2 w-25"></i> Soci & Tessere</a>
        </li>
        <li class="nav-item mt-3">
            <a href="VisualizzaGruppoServlet?id=<%= gruppo.getId_gruppo() %>" class="nav-link text-primary border border-primary text-center">
                <i class="fa-solid fa-arrow-left me-2"></i> Torna al Gruppo
            </a>
        </li>
    </ul>

    <div class="mt-auto text-center small text-muted">
        &copy; 2026 ClubConnect
    </div>
</div>

<div class="main-content">

    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h3 class="fw-bold text-dark">Dashboard Analitica</h3>
            <p class="text-muted mb-0">Benvenuto, ecco cosa sta succedendo nel tuo gruppo.</p>
        </div>
        <div class="d-flex gap-2">
            <form action="generaReportServlet" method="GET" target="_blank">
                <input type="hidden" name="idGruppo" value="<%= gruppo.getId_gruppo() %>">

                <button type="submit" class="btn btn-light border shadow-sm fw-bold">
                    <i class="fa-solid fa-download me-2 text-danger"></i>Report PDF
                </button>
            </form>
        </div>
    </div>

    <div class="row g-4 mb-4">

        <div class="col-md-4">
            <div class="stat-card">
                <div class="d-flex justify-content-between">
                    <div>
                        <p class="text-muted small fw-bold text-uppercase mb-1">Totale Soci</p>
                        <h2 class="fw-bold mb-0"><%= totaleMembri %></h2>
                        <small class="text-success fw-bold"><i class="fa-solid fa-arrow-trend-up me-1"></i> +<%= nuovi %> questo mese</small>
                    </div>
                    <div class="stat-icon bg-icon-primary">
                        <i class="fa-solid fa-users"></i>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-md-4">
            <div class="stat-card">
                <div class="d-flex justify-content-between">
                    <div>
                        <p class="text-muted small fw-bold text-uppercase mb-1"><%= isClub ? "Entrate Mensili" : "Fondi Raccolti" %></p>
                        <h2 class="fw-bold mb-0">€ <%= String.format("%.2f", entrate) %></h2>
                        <small class="text-success fw-bold">Stabile rispetto al mese scorso</small>
                    </div>
                    <div class="stat-icon bg-icon-success">
                        <i class="fa-solid fa-wallet"></i>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-md-4">
            <a href="VisualizzaGruppoServlet?id=<%= gruppo.getId_gruppo() %>#eventi" class="text-decoration-none d-block h-100">
                <div class="stat-card p-0 overflow-hidden transition-hover h-100"> <div class="p-4 h-100 d-flex flex-column justify-content-center">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <p class="text-muted small fw-bold text-uppercase mb-1">Eventi in Programma</p>
                            <h2 class="fw-bold mb-0 text-primary">
                                <%
                                    List<EventoBean> listaEventiBox = (List<EventoBean>) request.getAttribute("eventi");
                                    int numeroEventi = (listaEventiBox != null) ? listaEventiBox.size() : 0;
                                %>
                                <%= numeroEventi %>
                            </h2>
                        </div>
                        <div class="rounded-circle bg-warning bg-opacity-10 p-3 d-flex align-items-center justify-content-center" style="width: 50px; height: 50px;">
                            <i class="fa-regular fa-calendar-check fs-4 text-warning"></i>
                        </div>
                    </div>
                    <small class="text-muted mt-3 d-block">
                        Clicca per gestire <i class="fa-solid fa-arrow-right ms-1 small"></i>
                    </small>
                </div>
                </div>
            </a>
        </div>
    </div>

    <div class="row g-4">

        <div class="col-lg-8">
            <div class="card border-0 shadow-sm rounded-4 p-4 h-100">
                <h5 class="fw-bold mb-4">Andamento Iscrizioni</h5>
                <canvas id="membershipChart" style="max-height: 300px;"></canvas>
            </div>
        </div>

        <div class="col-lg-4">
            <div class="card border-0 shadow-sm rounded-4 p-4 h-100">
                <h5 class="fw-bold mb-3">Ultimi Iscritti</h5>
                <div class="table-responsive">
                    <table class="table table-borderless dashboard-table align-middle">
                        <thead>
                        <tr>
                            <th>Socio</th>
                            <th class="text-end">Stato</th>
                        </tr>
                        </thead>
                        <tbody>

                        <%
                            // 1. Recupero la lista dei soci dal request
                            // Assicurati che la Servlet faccia request.setAttribute("membri", listaMembri);
                            List<Application.GestioneAccount.UtenteBean> listaSoci =
                                    (List<Application.GestioneAccount.UtenteBean>) request.getAttribute("membri");

                            if (listaSoci != null && !listaSoci.isEmpty()) {
                                int counter = 0;
                                for (Application.GestioneAccount.UtenteBean socio : listaSoci) {
                                    // 2. Interrompo il ciclo se ho già mostrato 5 utenti
                                    if (counter >= 5) break;
                                    counter++;
                        %>
                        <tr>
                            <td>
                                <div class="d-flex align-items-center gap-2">
                                    <img src="https://ui-avatars.com/api/?name=<%= socio.getNome() %>+<%= socio.getCognome() %>&background=random&color=fff"
                                         class="rounded-circle" width="30" height="30" alt="Avatar">

                                    <span class="fw-bold small text-truncate" style="max-width: 120px;">
                                    <%= socio.getNome() %> <%= socio.getCognome() %>
                                </span>
                                </div>
                            </td>
                            <td class="text-end">
                            <span class="badge bg-success bg-opacity-10 text-success rounded-pill px-2">
                                Iscritto
                            </span>
                            </td>
                        </tr>
                        <%
                            } // Fine For
                        } else {
                        %>
                        <tr>
                            <td colspan="2" class="text-center text-muted py-4 small">
                                <i class="fa-regular fa-user mb-2 d-block"></i>
                                Ancora nessun membro nel gruppo.
                            </td>
                        </tr>
                        <% } %>

                        </tbody>
                    </table>
                </div>

                <form action="VisualizzaSociServlet" method="GET">
                    <input type="hidden" name="id" value="<%= gruppo.getId_gruppo() %>">
                    <button type="submit" class="btn btn-outline-secondary w-100 btn-sm mt-2 rounded-pill transition-hover">
                        Vedi tutti i soci
                    </button>
                </form>
            </div>
        </div>
    </div>

</div>

<script>
    const ctx = document.getElementById('membershipChart').getContext('2d');
    const myChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: ['Gen', 'Feb', 'Mar', 'Apr', 'Mag', 'Giu'],
            datasets: [{
                label: 'Nuovi Soci',
                data: [12, 19, 3, 5, 2, 3],
                backgroundColor: 'rgba(38, 169, 188, 0.2)',
                borderColor: 'rgba(38, 169, 188, 1)',
                borderWidth: 2,
                tension: 0.4,
                fill: true
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: { display: false }
            },
            scales: {
                y: { beginAtZero: true, grid: { borderDash: [5, 5] } },
                x: { grid: { display: false } }
            }
        }
    });
</script>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
