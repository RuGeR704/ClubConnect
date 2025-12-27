
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ClubConnect</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style-bootstrap.css">

    <style>
        /* --- STILI GENERALI (Già presenti) --- */
        :root {
            --bs-primary: #1E3A5F;
            --bs-secondary: #26A9BC;
        }

        /* --- HERO SECTION AVANZATA --- */
        /* --- HERO SECTION & 3D STAGE --- */
        .hero-section {
            background-color: #1E3A5F;
            background-image:
                    radial-gradient(circle at 15% 50%, rgba(38, 169, 188, 0.08) 0%, transparent 25%),
                    radial-gradient(circle at 85% 30%, rgba(38, 169, 188, 0.08) 0%, transparent 25%),
                    linear-gradient(135deg, #1E3A5F 0%, #0f2035 100%);
            color: white !important;
            padding: 5rem 0;
            position: relative;
            overflow: hidden;
            perspective: 1500px; /* Prospettiva profonda per il 3D */
        }

        .hero-visual-container {
            position: relative;
            height: 450px; /* Più alto per ospitare la dashboard */
            display: flex;
            align-items: center;
            justify-content: center;
            transform-style: preserve-3d; /* Fondamentale per elementi sovrapposti */
        }

        /* --- LA MINI DASHBOARD (Base) --- */
        .dashboard-mockup {
            width: 550px;
            height: 380px;
            background: #F4F6F9; /* Sfondo grigio chiaro app */
            border-radius: 16px;
            box-shadow:
                    0 40px 60px -15px rgba(0, 0, 0, 0.4), /* Ombra profonda */
                    inset 0 0 0 1px rgba(255, 255, 255, 0.1); /* Bordo sottile */
            display: flex;
            overflow: hidden;
            position: relative;

            /* Posizione 3D Iniziale */
            transform: rotateY(-12deg) rotateX(6deg);
            transition: all 0.6s cubic-bezier(0.23, 1, 0.32, 1);
            animation: float-dashboard 8s ease-in-out infinite;
        }

        /* Effetto Hover sulla Dashboard: Si raddrizza */
        .hero-visual-container:hover .dashboard-mockup {
            transform: rotateY(0deg) rotateX(0deg) scale(1.02);
            box-shadow: 0 50px 80px -20px rgba(0, 0, 0, 0.5);
        }

        /* --- ELEMENTI INTERNI DASHBOARD (Skeleton UI) --- */
        /* Sidebar */
        .mock-sidebar {
            width: 80px;
            background: #1E3A5F;
            display: flex; flex-direction: column; align-items: center; padding-top: 20px; gap: 15px;
        }
        .mock-logo { width: 40px; height: 40px; background: rgba(255,255,255,0.2); border-radius: 8px; margin-bottom: 20px; }
        .mock-nav-item { width: 40px; height: 10px; background: rgba(255,255,255,0.1); border-radius: 4px; }
        .mock-nav-item.active { background: #26A9BC; width: 50px; }

        /* Contenuto */
        .mock-content { flex: 1; padding: 20px; display: flex; flex-direction: column; gap: 15px; }

        /* Header finto */
        .mock-header { display: flex; justify-content: space-between; margin-bottom: 10px; }
        .mock-title { width: 120px; height: 14px; background: #cbd5e1; border-radius: 4px; }
        .mock-user { width: 30px; height: 30px; background: #cbd5e1; border-radius: 50%; }

        /* KPI Row */
        .mock-kpi-row { display: flex; gap: 15px; }
        .mock-card { flex: 1; background: white; padding: 15px; border-radius: 10px; box-shadow: 0 2px 5px rgba(0,0,0,0.05); }
        .mock-line-sm { width: 50%; height: 8px; background: #e2e8f0; border-radius: 4px; margin-bottom: 8px; }
        .mock-line-lg { width: 80%; height: 12px; background: #1E3A5F; border-radius: 4px; }
        .mock-line-teal { width: 80%; height: 12px; background: #26A9BC; border-radius: 4px; }

        /* Grafico Finto */
        .mock-chart-area { flex: 1; background: white; border-radius: 10px; padding: 15px; display: flex; align-items: flex-end; justify-content: space-between; gap: 8px; }
        .mock-bar { width: 100%; background: #e2e8f0; border-radius: 4px 4px 0 0; opacity: 0.7; }
        .mock-bar.highlight { background: #26A9BC; }

        /* --- NOTIFICA POP-UP CENTRALE & PIÙ GRANDE --- */
        .floating-notification {
            position: absolute;

            /* 1. Posizionamento CENTRALE */
            top: 50%;
            left: 50%;

            /* 2. Stile più grande e visibile */
            background: white;
            padding: 1.5rem 2rem; /* Aumentato padding per renderla più grande */
            border-radius: 16px;  /* Bordi più morbidi */

            /* Ombra molto forte per staccarla dallo sfondo */
            box-shadow:
                    0 25px 50px -12px rgba(0, 0, 0, 0.4),
                    0 0 0 1px rgba(0,0,0,0.05);

            display: flex;
            align-items: center;
            gap: 15px;
            color: #1E3A5F;
            z-index: 20; /* Z-index alto per stare sopra */
            min-width: 260px; /* Larghezza minima per non schiacciarsi */

            /* 3. Trasformazione Combinata: Centratura + Profondità 3D */
            /* translate(-50%, -50%) la centra perfettamente. translateZ(80px) la porta "vicino" a noi */
            transform: translate(-50%, -50%) translateZ(80px);

            /* Animazione */
            animation: float-center 6s ease-in-out infinite;
        }

        /* Testi interni più grandi */
        .floating-notification .icon-box {
            width: 45px; height: 45px; /* Icona più grande */
            background: #d1fae5;
            border-radius: 50%;
            display: flex; align-items: center; justify-content: center;
            color: #10b981;
            font-size: 1.5rem;
            flex-shrink: 0;
        }

        .floating-notification text-box {
            display: flex; flex-direction: column;
        }

        /* --- NUOVA ANIMAZIONE PER L'ELEMENTO CENTRALE --- */
        @keyframes float-center {
            /* Manteniamo la centratura (-50%, -50%) e la profondità (80px) mentre muoviamo Y */
            0%, 100% {
                transform: translate(-50%, -50%) translateZ(80px) translateY(0px);
            }
            50% {
                transform: translate(-50%, -50%) translateZ(80px) translateY(-15px);
            }
        }

        /* Animazioni */
        @keyframes float-dashboard {
            0%, 100% { transform: rotateY(-12deg) rotateX(6deg) translateY(0); }
            50% { transform: rotateY(-12deg) rotateX(6deg) translateY(-15px); }
        }
        @keyframes float-notification {
            0%, 100% { transform: translateZ(50px) rotateY(-10deg) translateY(0); }
            50% { transform: translateZ(50px) rotateY(-10deg) translateY(-10px); }
        }

        /* Mobile Adjustments */
        @media (max-width: 991px) {
            .hero-visual-container { transform: scale(0.6); margin-top: -50px; margin-bottom: -50px; }
            .dashboard-mockup { width: 450px; }
            .floating-notification { right: 20px; bottom: 20px; }
        }
        /* 1. Sfondo Astratto (La "App" sfocata dietro) */
        .abstract-ui-bg {
            width: 320px;
            height: 420px;
            background: rgba(255, 255, 255, 0.05);
            border: 1px solid rgba(255, 255, 255, 0.1);
            border-radius: 20px;
            position: absolute;
            transform: rotateY(10deg) rotateZ(-2deg);
            backdrop-filter: blur(5px);
            z-index: 1;
            box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5);
        }

        /* 2. La Card Interattiva ("Iscrizione Rinnovata") */
        .interactive-card {
            background: white;
            color: #1E3A5F;
            padding: 1.5rem 2rem;
            border-radius: 16px;
            box-shadow:
                    0 20px 25px -5px rgba(0, 0, 0, 0.2),
                    0 10px 10px -5px rgba(0, 0, 0, 0.1);
            position: relative;
            z-index: 2;
            width: 280px;

            /* Stato Iniziale: Leggermente ruotato */
            transform: rotate(-6deg) translateY(0);
            transition: all 0.5s cubic-bezier(0.34, 1.56, 0.64, 1); /* Effetto "rimbalzo" elastico */
            cursor: pointer;

            /* Animazione automatica "Galleggiamento" */
            animation: float 6s ease-in-out infinite;
        }

        /* Stato Hover/Click: Si raddrizza e ingrandisce */
        .interactive-card:hover,
        .interactive-card:active {
            transform: rotate(0deg) scale(1.05) translateY(-5px);
            box-shadow:
                    0 25px 50px -12px rgba(38, 169, 188, 0.4), /* Ombra Teal luminosa */
                    0 0 0 4px rgba(255, 255, 255, 0.2); /* Bordo esterno */
        }

        /* Animazione Galleggiamento */
        @keyframes float {
            0% { transform: rotate(-6deg) translateY(0px); }
            50% { transform: rotate(-6deg) translateY(-15px); }
            100% { transform: rotate(-6deg) translateY(0px); }
        }

        /* Decorazione checkmark animato */
        .check-circle {
            width: 40px; height: 40px;
            background: #d1fae5; color: #059669;
            border-radius: 50%;
            display: flex; align-items: center; justify-content: center;
            font-size: 1.2rem;
            margin-right: 15px;
        }

        /* --- MEDIA QUERIES --- */
        @media (max-width: 991px) {
            .hero-visual-container { height: 300px; margin-top: 2rem; transform: scale(0.8); }
        }

        /* --- ALTRI STILI NECESSARI (Card clubs, ecc) --- */
        .card { overflow: hidden; transition: transform 0.3s ease; }
        .card:hover { transform: translateY(-5px); }
        .feature-icon { width: 70px; height: 70px; background: rgba(38,169,188,0.1); color:#26A9BC; border-radius:50%; display:flex; align-items:center; justify-content:center; font-size:2rem; margin:0 auto 1.5rem auto; }
        .locked-overlay { position:absolute; top:0; left:0; right:0; bottom:0; background:rgba(255,255,255,0.9); opacity:0; transition:0.3s; display:flex; flex-direction:column; align-items:center; justify-content:center; z-index:10; }
        .card:hover .locked-overlay { opacity:1; }
        .btn-secondary { color: white !important; background-color: #26A9BC; border-color: #26A9BC; }
        .btn-secondary:hover { background-color: #1e8a9b; border-color: #1e8a9b; }
    </style>

</head>
<body class="bg-light d-flex flex-column min-vh-100">

<nav class="navbar navbar-expand-lg navbar-light bg-white sticky-top shadow-sm py-3">
    <div class="container">
        <a class="navbar-brand d-flex align-items-center gap-2 fw-bold fs-4" href="#">
            <div class="brand-icon" style="width: 40px; height: 40px;">
                <img src="./images/logo.png" alt="Logo" class="img-fluid" onerror="this.style.display='none'; this.parentElement.innerHTML='<i class=\'fa-solid fa-users text-primary fs-2\'></i>'">
            </div>
            <span style="color: #1E3A5F">Club<span style="color: #26A9BC">Connect</span></span>
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse justify-content-end" id="navbarNav">
            <div class="d-flex gap-2 mt-3 mt-lg-0">
                <a href="login.jsp" class="btn btn-outline-primary">Accedi</a>
                <a href="registrazione.jsp" class="btn btn-secondary px-4">Registrati</a>
            </div>
        </div>
    </div>
</nav>

<header class="hero-section">
    <div class="container">
        <div class="row align-items-center">

            <div class="col-lg-6 text-center text-lg-start z-1">
                <h1 class="display-4 fw-bold mb-4 text-white">
                    Tutte le tue passioni,<br>
                    <span style="color: #26A9BC;">in un'unica app.</span>
                </h1>
                <p class="lead mb-5 fs-5 text-white-50" style="font-weight: 300;">
                    Gestisci le tue iscrizioni, paga le quote e partecipa agli eventi.
                    Semplice, digitale, connesso.
                </p>
                <div class="d-flex justify-content-center justify-content-lg-start gap-3">
                    <a href="registrazione.jsp" class="btn btn-secondary btn-lg px-4 py-3 fw-bold shadow">
                        <i class="fa-solid fa-rocket me-2"></i> Inizia Ora
                    </a>
                    <a href="#clubs" class="btn btn-outline-light btn-lg px-4 py-3">
                        Esplora
                    </a>
                </div>
            </div>

            <div class="col-lg-6">
                <div class="hero-visual-container">

                    <div class="dashboard-mockup">

                        <div class="mock-sidebar">
                            <div class="mock-logo"></div> <div class="mock-nav-item active"></div>
                            <div class="mock-nav-item"></div>
                            <div class="mock-nav-item"></div>
                            <div class="mock-nav-item"></div>
                        </div>

                        <div class="mock-content">
                            <div class="mock-header">
                                <div class="mock-title"></div>
                                <div class="mock-user"></div>
                            </div>

                            <div class="mock-kpi-row">
                                <div class="mock-card">
                                    <div class="mock-line-sm"></div>
                                    <div class="mock-line-lg"></div>
                                </div>
                                <div class="mock-card">
                                    <div class="mock-line-sm"></div>
                                    <div class="mock-line-teal"></div> </div>
                                <div class="mock-card">
                                    <div class="mock-line-sm"></div>
                                    <div class="mock-line-lg" style="width: 40%"></div>
                                </div>
                            </div>

                            <div class="mock-chart-area">
                                <div class="mock-bar" style="height: 40%"></div>
                                <div class="mock-bar" style="height: 60%"></div>
                                <div class="mock-bar highlight" style="height: 85%"></div> <div class="mock-bar" style="height: 55%"></div>
                                <div class="mock-bar" style="height: 30%"></div>
                                <div class="mock-bar" style="height: 70%"></div>
                                <div class="mock-bar" style="height: 50%"></div>
                            </div>
                        </div>
                    </div>

                    <div class="floating-notification">
                        <div class="icon-box">
                            <i class="fa-solid fa-check"></i>
                        </div>
                        <div class="text-box">
                            <span class="fw-bold fs-5">Iscrizione Rinnovata!</span> <br>
                            <span class="text-muted small">Pagamento registrato con successo</span>
                        </div>
                    </div>

                </div>
            </div>

        </div>
    </div>
</header>

<section id="clubs" class="py-5">
    <div class="container py-5">
        <div class="text-center mb-5">
            <h2 class="fw-bold display-6" style="color: #1E3A5F;">Associazioni in Evidenza</h2>
            <p class="text-muted fs-5">Esplora i gruppi più attivi. Accedi per interagire o iscriverti.</p>
        </div>

        <div class="row g-4">
            <div class="col-md-4">
                <div class="card h-100 border-0 shadow-sm">
                    <div class="p-4 position-relative" style="height: 180px; background: linear-gradient(135deg, #1E3A5F, #3b82f6);">
                        <span class="badge bg-warning text-dark position-absolute top-0 end-0 m-3 px-3 py-2">Sport</span>
                        <h3 class="text-white mt-4 pt-3 h4">Polisportiva Centrale</h3>
                    </div>
                    <div class="card-body p-4">
                        <p class="card-text text-muted mb-4">Il punto di riferimento per il basket e la pallavolo in città. Corsi per tutte le età.</p>
                        <div class="d-flex justify-content-between text-muted small fw-bold">
                            <span><i class="fa-solid fa-users me-2" style="color: #26A9BC"></i>1.2k Membri</span>
                            <span><i class="fa-solid fa-calendar me-2" style="color: #26A9BC"></i>3 Eventi/sett</span>
                        </div>
                    </div>
                    <div class="locked-overlay text-center p-4">
                        <div class="mb-3"><i class="fa-solid fa-lock" style="color: #1E3A5F; font-size: 2.5rem;"></i></div>
                        <h4 class="fw-bold" style="color: #1E3A5F">Riservato ai Soci</h4>
                        <p class="text-muted small mb-4">Devi essere registrato per iscriverti a questo club.</p>
                        <a href="registrazione.jsp" class="btn btn-secondary px-4 rounded-pill">Crea Account</a>
                    </div>
                </div>
            </div>

            <div class="col-md-4">
                <div class="card h-100 border-0 shadow-sm">
                    <div class="p-4 position-relative" style="height: 180px; background: linear-gradient(135deg, #26A9BC, #10B981);">
                        <span class="badge bg-white position-absolute top-0 end-0 m-3 px-3 py-2" style="color: #26A9BC">Cultura</span>
                        <h3 class="text-white mt-4 pt-3 h4">Circolo Letterario</h3>
                    </div>
                    <div class="card-body p-4">
                        <p class="card-text text-muted mb-4">Letture condivise, incontri con autori e workshop di scrittura creativa.</p>
                        <div class="d-flex justify-content-between text-muted small fw-bold">
                            <span><i class="fa-solid fa-users me-2" style="color: #26A9BC"></i>450 Membri</span>
                            <span><i class="fa-solid fa-calendar me-2" style="color: #26A9BC"></i>1 Evento/sett</span>
                        </div>
                    </div>
                    <div class="locked-overlay text-center p-4">
                        <div class="mb-3"><i class="fa-solid fa-lock" style="color: #1E3A5F; font-size: 2.5rem;"></i></div>
                        <h4 class="fw-bold" style="color: #1E3A5F">Riservato ai Soci</h4>
                        <p class="text-muted small mb-4">Devi essere registrato per iscriverti a questo club.</p>
                        <a href="registrazione.jsp" class="btn btn-secondary px-4 rounded-pill">Crea Account</a>
                    </div>
                </div>
            </div>

            <div class="col-md-4">
                <div class="card h-100 bg-transparent border-2 border-opacity-50 shadow-none" style="border-color: #26A9BC; border-style: dashed !important;">
                    <div class="card-body d-flex flex-column justify-content-center align-items-center text-center p-5">
                        <div class="feature-icon mb-4">
                            <i class="fa-solid fa-calendar-star"></i>
                        </div>
                        <h3 class="h4 fw-bold mb-3" style="color: #1E3A5F">Prossimi Eventi?</h3>
                        <p class="text-muted mb-4">Scopri cosa bolle in pentola nella tua zona. Unisciti alla community per partecipare.</p>
                        <a href="registrazione.jsp" class="btn btn-outline-primary rounded-pill px-4 py-2 fw-semibold">Crea Account Gratuito</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>

<footer class="bg-dark text-white py-5 mt-auto">
    <div class="container">
        <div class="row gy-4 justify-content-between">
            <div class="col-md-4">
                <div class="mb-3 d-flex align-items-center gap-2 fs-4 fw-bold">
                    <span style="color: white">Club<span style="color: #26A9BC">Connect</span></span>
                </div>
                <p class="text-white-50 small mb-0">La piattaforma digitale all-in-one per la gestione semplificata di associazioni sportive e culturali.</p>
            </div>
            <div class="col-md-3">
                <h5 class="text-white mb-3">Menu</h5>
                <ul class="list-unstyled text-white-50 small d-flex flex-column gap-2">
                    <li><a href="login.jsp" class="text-reset text-decoration-none hover-underline">Accedi</a></li>
                    <li><a href="registrazione.jsp" class="text-reset text-decoration-none hover-underline">Registrati</a></li>
                </ul>
            </div>
        </div>
        <div class="border-top border-secondary border-opacity-25 mt-5 pt-4 text-center text-white-50 small">
            &copy; 2025 ClubConnect. Tutti i diritti riservati.
        </div>
    </div>
</footer>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>