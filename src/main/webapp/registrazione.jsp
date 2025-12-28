
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registrati - ClubConnect</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style-bootstrap.css">

    <style>
        /* FIX ALTEZZA: Assicuriamo che html e body siano alti 100% */
        html, body { height: 100%; margin: 0; overflow-x: hidden; }

        /* --- LATO SINISTRO (Form) --- */
        .login-side {
            min-height: 100vh; /* Occupa tutto lo schermo */
            display: flex; flex-direction: column; justify-content: center;
            padding: 3rem; background: white; position: relative; z-index: 2;
        }

        /* Input Moderni */
        .form-floating > .form-control { border: 2px solid #e2e8f0; border-radius: 12px; }
        .form-floating > .form-control:focus { border-color: #26A9BC; box-shadow: 0 0 0 4px rgba(38, 169, 188, 0.1); }
        .form-floating > label { color: #94a3b8; }

        /* --- LATO DESTRO (Visual) --- */
        .visual-side {
            height: 100%;
            background-color: #1E3A5F;
            background-image:
                    radial-gradient(circle at 80% 10%, rgba(38, 169, 188, 0.15) 0%, transparent 20%),
                    radial-gradient(circle at 20% 90%, rgba(38, 169, 188, 0.15) 0%, transparent 20%),
                    linear-gradient(135deg, #1E3A5F 0%, #0f2035 100%);
            position: relative; overflow: hidden; display: flex; align-items: center; justify-content: center;
        }

        /* Card Fluttuante (Stile Lista Vantaggi) */
        .glass-card {
            background: rgba(255, 255, 255, 0.1); backdrop-filter: blur(12px);
            border: 1px solid rgba(255, 255, 255, 0.2); padding: 3rem; border-radius: 24px;
            max-width: 450px; color: white;
            animation: float 6s ease-in-out infinite;
            box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
        }

        .benefit-item { display: flex; align-items: center; gap: 15px; margin-bottom: 20px; }
        .benefit-icon {
            width: 45px; height: 45px; background: rgba(38, 169, 188, 0.2);
            border-radius: 12px; display: flex; align-items: center; justify-content: center;
            color: #26A9BC; font-size: 1.2rem; flex-shrink: 0;
        }

        /* --- STILE INPUT AGGIORNATO --- */
        /* Applichiamo lo stile sia agli input che alle select */
        .form-floating > .form-control,
        .form-floating > .form-select {
            border: 2px solid #e2e8f0;
            border-radius: 12px;
            height: calc(3.5rem + 2px); /* Altezza uniforme */
            line-height: 1.25;
        }

        /* Effetto Focus (Bordo Teal) */
        .form-floating > .form-control:focus,
        .form-floating > .form-select:focus {
            border-color: #26A9BC;
            box-shadow: 0 0 0 4px rgba(38, 169, 188, 0.1);
            z-index: 3; /* Porta l'elemento in primo piano se si sovrappone */
        }

        .form-floating > label { color: #94a3b8; }

        @keyframes float { 0%, 100% { transform: translateY(0); } 50% { transform: translateY(-15px); } }

        @media (max-width: 991px) {
            .visual-side { display: none; }
            .login-side { padding: 2rem; min-height: 100vh; }
        }

        .btn-custom-primary { background-color: #1E3A5F; border-color: #1E3A5F; color: white; }
        .btn-custom-primary:hover { background-color: #152943; border-color: #152943; }
    </style>
</head>
<body>

<div class="container-fluid p-0 overflow-hidden">
    <div class="row g-0 min-vh-100">

        <div class="col-lg-6">
            <div class="login-side">

                <div class="mb-4">
                    <a href="index.jsp" class="d-inline-flex align-items-center gap-2 text-decoration-none">
                        <div class="brand-icon" style="width: 40px; height: 40px;">
                            <img src="./images/logo.png" alt="Logo" class="img-fluid">
                        </div>
                        <span class="fs-4 fw-bold" style="color: #1E3A5F;">Club<span style="color: #26A9BC;">Connect</span></span>
                    </a>
                </div>

                <div style="max-width: 450px; width: 100%; margin: 0 auto;">
                    <h1 class="h2 fw-bold mb-2" style="color: #1E3A5F;">Crea un account 🚀</h1>
                    <p class="text-muted mb-4">Unisciti a noi e inizia a gestire le tue passioni.</p>

                    <form action="RegistrazioneServlet" method="POST">

                        <div class="row g-2 mb-3">
                            <div class="col-6">
                                <div class="form-floating">
                                    <input type="text" class="form-control" id="nome" name="nome" placeholder="Mario" required>
                                    <label for="nome">Nome</label>
                                </div>
                            </div>
                            <div class="col-6">
                                <div class="form-floating">
                                    <input type="text" class="form-control" id="cognome" name="cognome" placeholder="Rossi" required>
                                    <label for="cognome">Cognome</label>
                                </div>
                            </div>
                        </div>

                        <div class="form-floating mb-3">
                            <input type="text" class="form-control" id="username" name="username" placeholder="mariorossi01" required>
                            <label for="username">Username</label>
                        </div>

                        <div class="form-floating mb-3">
                            <input type="date" class="form-control" id="dataNascita" name="dataNascita" placeholder="GG/MM/AAAA" required>
                            <label for="dataNascita">Data di Nascita</label>
                        </div>

                        <div class="form-floating mb-3">
                            <input type="email" class="form-control" id="email" name="email" placeholder="nome@esempio.it" required>
                            <label for="email">Indirizzo Email</label>
                        </div>

                        <div class="input-group mb-3">

                            <div class="form-floating" style="max-width: 110px;">
                                <select class="form-select bg-light" id="prefisso" name="prefisso" style="border-top-right-radius: 0; border-bottom-right-radius: 0; border-right: none;">
                                    <option value="+39" selected>🇮🇹 +39</option>
                                    <option value="+1">🇺🇸 +1</option>
                                    <option value="+44">🇬🇧 +44</option>
                                    <option value="+33">🇫🇷 +33</option>
                                    <option value="+49">🇩🇪 +49</option>
                                    <option value="+34">🇪🇸 +34</option>
                                    <option value="+41">🇨🇭 +41</option>
                                </select>
                                <label for="prefisso">Prefisso</label>
                            </div>

                            <div class="form-floating flex-grow-1">
                                <input type="tel" class="form-control" id="telefono" name="telefono" placeholder="333 1234567" style="border-top-left-radius: 0; border-bottom-left-radius: 0;">
                                <label for="telefono">Numero di cellulare</label>
                            </div>

                        </div>

                        <div class="form-floating mb-3">
                            <input type="password" class="form-control" id="password" name="password" placeholder="Password" required>
                            <label for="password">Password</label>
                        </div>

                        <div class="form-check mb-4">
                            <input class="form-check-input" type="checkbox" id="privacyCheck" required>
                            <label class="form-check-label text-muted small" for="privacyCheck">
                                Accetto i <a href="#" class="text-decoration-none" style="color: #26A9BC;">Termini di Servizio</a> e la Privacy Policy.
                            </label>
                        </div>

                        <button type="submit" class="btn btn-custom-primary w-100 py-3 rounded-3 fw-bold shadow-sm mb-3">
                            Crea Account
                        </button>

                    </form>

                    <p class="text-center text-muted mt-3 small">
                        Hai già un account?
                        <a href="login.jsp" class="fw-bold text-decoration-none" style="color: #1E3A5F;">Accedi qui</a>
                    </p>
                </div>
            </div>
        </div>

        <div class="col-lg-6 d-none d-lg-block p-0">
            <div class="visual-side">
                <div style="position: absolute; top: 10%; left: 10%; width: 20px; height: 20px; background: #F4C430; border-radius: 50%; opacity: 0.6;"></div>
                <div style="position: absolute; bottom: 20%; right: 10%; width: 15px; height: 15px; background: #E65142; border-radius: 50%; opacity: 0.6;"></div>

                <div class="glass-card">
                    <h3 class="fw-bold mb-4">Perché iscriverti?</h3>

                    <div class="benefit-item">
                        <div class="benefit-icon"><i class="fa-solid fa-users"></i></div>
                        <div>
                            <h6 class="mb-0 fw-bold">Community Unica</h6>
                            <small class="text-white-50">Connettiti con persone che condividono le tue passioni.</small>
                        </div>
                    </div>

                    <div class="benefit-item">
                        <div class="benefit-icon"><i class="fa-solid fa-calendar-check"></i></div>
                        <div>
                            <h6 class="mb-0 fw-bold">Eventi Esclusivi</h6>
                            <small class="text-white-50">Prenota il tuo posto agli eventi del club in un click.</small>
                        </div>
                    </div>

                    <div class="benefit-item mb-0">
                        <div class="benefit-icon"><i class="fa-solid fa-receipt"></i></div>
                        <div>
                            <h6 class="mb-0 fw-bold">Pagamenti Smart</h6>
                            <small class="text-white-50">Rinnova la tua quota e paga i corsi in sicurezza.</small>
                        </div>
                    </div>
                </div>
            </div>
        </div>

    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>

</html>
