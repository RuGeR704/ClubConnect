<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Application.GestioneGruppo.GruppoBean" %>
<%@ page import="Application.GestioneGruppo.ClubBean" %>
<%
    GruppoBean gruppo = (GruppoBean) request.getAttribute("gruppo");
    int totaleMembri = (int) request.getAttribute("totaleMembri");
    double entrate = (double) request.getAttribute("entrateMensili");
    int nuovi = (int) request.getAttribute("nuoviIscritti");
    boolean isClub = (gruppo instanceof ClubBean);
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
    </style>
</head>
<body>

<div class="dashboard-sidebar d-flex flex-column">
    <a href="feedServlet" class="d-flex align-items-center gap-2 mb-5 text-decoration-none">
        <img src="./images/logo.png" width="35" alt="Logo">
        <span class="fs-5 fw-bold" style="color: var(--club-dark);">ClubConnect</span>
    </a>

    <div class="d-flex align-items-center gap-3 mb-4 p-3 rounded-3 bg-light">
        <img src="<%= gruppo.getLogo() != null ? gruppo.getLogo() : "images/default.png" %>"
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
            <a href="#" class="nav-link"><i class="fa-solid fa-users me-2 w-25"></i> Soci & Tessere</a>
        </li>
        <li class="nav-item">
            <a href="#" class="nav-link"><i class="fa-solid fa-euro-sign me-2 w-25"></i> Contabilità</a>
        </li>
        <li class="nav-item">
            <a href="#" class="nav-link"><i class="fa-regular fa-calendar me-2 w-25"></i> Eventi</a>
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
            <button class="btn btn-light border shadow-sm"><i class="fa-solid fa-download me-2"></i>Report PDF</button>
            <button class="btn btn-club-primary text-white" style="background: var(--club-teal);"><i class="fa-solid fa-plus me-2"></i>Nuovo Socio</button>
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
            <div class="stat-card">
                <div class="d-flex justify-content-between">
                    <div>
                        <p class="text-muted small fw-bold text-uppercase mb-1">Tessere in Scadenza</p>
                        <h2 class="fw-bold mb-0 text-warning">5</h2> <small class="text-muted">Prossimi 30 giorni</small>
                    </div>
                    <div class="stat-icon bg-icon-warning">
                        <i class="fa-solid fa-clock"></i>
                    </div>
                </div>
            </div>
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
                <h5 class="fw-bold mb-3">In Scadenza</h5>
                <div class="table-responsive">
                    <table class="table table-borderless dashboard-table align-middle">
                        <thead>
                        <tr>
                            <th>Socio</th>
                            <th class="text-end">Giorni</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td>
                                <div class="d-flex align-items-center gap-2">
                                    <img src="https://ui-avatars.com/api/?name=Mario+Rossi" class="rounded-circle" width="30">
                                    <span class="fw-bold small">Mario Rossi</span>
                                </div>
                            </td>
                            <td class="text-end"><span class="badge bg-danger bg-opacity-10 text-danger">3 gg</span></td>
                        </tr>
                        <tr>
                            <td>
                                <div class="d-flex align-items-center gap-2">
                                    <img src="https://ui-avatars.com/api/?name=Luca+Verdi" class="rounded-circle" width="30">
                                    <span class="fw-bold small">Luca Verdi</span>
                                </div>
                            </td>
                            <td class="text-end"><span class="badge bg-warning bg-opacity-10 text-warning text-dark">12 gg</span></td>
                        </tr>
                        <tr>
                            <td>
                                <div class="d-flex align-items-center gap-2">
                                    <img src="https://ui-avatars.com/api/?name=Anna+Bianchi" class="rounded-circle" width="30">
                                    <span class="fw-bold small">Anna Bianchi</span>
                                </div>
                            </td>
                            <td class="text-end"><span class="badge bg-warning bg-opacity-10 text-warning text-dark">20 gg</span></td>
                        </tr>
                        </tbody>
                    </table>
                </div>
                <button class="btn btn-outline-secondary w-100 btn-sm mt-2 rounded-pill">Vedi tutti i soci</button>
            </div>
        </div>
    </div>

</div>

<script>
    const ctx = document.getElementById('membershipChart').getContext('2d');
    const myChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: ['Gen', 'Feb', 'Mar', 'Apr', 'Mag', 'Giu'], // Mesi mock
            datasets: [{
                label: 'Nuovi Soci',
                data: [12, 19, 3, 5, 2, 3], // Dati mock
                backgroundColor: 'rgba(38, 169, 188, 0.2)',
                borderColor: 'rgba(38, 169, 188, 1)',
                borderWidth: 2,
                tension: 0.4, // Curva morbida
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
