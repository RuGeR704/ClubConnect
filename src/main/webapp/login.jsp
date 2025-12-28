
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Accedi | ClubConnect</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">

    <style>
        /* Layout a due colonne full-height */
        .login-wrapper {
            min-height: 100vh;
            display: flex;
        }

        /* Form */
        .login-side {
            flex: 1;
            display: flex;
            flex-direction: column;
            justify-content: center;
            padding: 3rem;
            background: white;
            position: relative;
            z-index: 2;
        }

        .form-floating > .form-control {
            border: 2px solid #e2e8f0;
            border-radius: 12px;
        }
        .form-floating > .form-control:focus {
            border-color: #26A9BC; /* Teal */
            box-shadow: 0 0 0 4px rgba(38, 169, 188, 0.1);
        }
        .form-floating > label { color: #94a3b8; }

        /* Visual animato */
        .visual-side {
            flex: 1;
            background-color: #1E3A5F;
            /* Stesso background della Home */
            background-image:
                    radial-gradient(circle at 10% 20%, rgba(38, 169, 188, 0.1) 0%, transparent 20%),
                    radial-gradient(circle at 90% 80%, rgba(38, 169, 188, 0.1) 0%, transparent 20%),
                    linear-gradient(135deg, #1E3A5F 0%, #0f2035 100%);
            position: relative;
            overflow: hidden;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        /* Card Fluttuante */
        .glass-card {
            background: rgba(255, 255, 255, 0.1);
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.2);
            padding: 2.5rem;
            border-radius: 24px;
            max-width: 400px;
            color: white;
            text-align: center;
            animation: float 6s ease-in-out infinite;
        }

        @keyframes float {
            0%, 100% { transform: translateY(0); }
            50% { transform: translateY(-15px); }
        }

        /* Mobile Adjustments */
        @media (max-width: 991px) {
            .visual-side { display: none; } /* Nascondi visual su mobile */
            .login-side { padding: 2rem; }
        }
    </style>
</head>
<body>

<div class="container-fluid p-0">
    <div class="row g-0">

        <div class="col-lg-6">
            <div class="login-side">

                <div class="mb-5">
                    <a href="index.jsp" class="d-inline-flex align-items-center gap-2 text-decoration-none">
                        <div class="brand-icon" style="width: 32px; height: 32px;">
                            <img src="./images/logo.png" alt="Logo" class="img-fluid">
                        </div>
                        <span class="fs-4 fw-bold" style="color: #1E3A5F;">Club<span style="color: #26A9BC;">Connect</span></span>
                    </a>
                </div>

                <div style="max-width: 420px; width: 100%; margin: 0 auto;">
                    <h1 class="h2 fw-bold text-primary mb-2">Bentornato! 👋</h1>
                    <p class="text-muted mb-4">Inserisci le tue credenziali per accedere.</p>

                    <form action="LoginServlet" method="POST">

                        <div class="form-floating mb-3">
                            <input type="email" class="form-control" id="email" name="email" placeholder="nome@esempio.it" required>
                            <label for="email">Indirizzo Email</label>
                        </div>

                        <div class="form-floating mb-3">
                            <input type="password" class="form-control" id="password" name="password" placeholder="Password" required>
                            <label for="password">Password</label>
                        </div>

                        <div class="d-flex justify-content-between align-items-center mb-4">
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" id="rememberMe">
                                <label class="form-check-label text-muted small" for="rememberMe">Ricordami</label>
                            </div>
                            <a href="#" class="small text-secondary fw-semibold text-decoration-none">Password dimenticata?</a>
                        </div>

                        <%
                            String errore = (String) request.getAttribute("errore");
                            if (errore != null) {
                        %>
                        <div class="alert alert-danger d-flex align-items-center p-2 mb-3" role="alert">
                            <i class="fa-solid fa-circle-exclamation me-2"></i>
                            <div class="small fw-bold">
                                <%= errore %>
                            </div>
                        </div>
                        <% } %>

                        <button type="submit" class="btn btn-primary w-100 py-3 rounded-3 fw-bold shadow-sm mb-3">
                            Accedi
                        </button>

                    </form>

                    <p class="text-center text-muted mt-4 small">
                        Non hai ancora un account?
                        <a href="registrazione.jsp" class="text-primary fw-bold text-decoration-none">Registrati ora</a>
                    </p>
                </div>

                <br> <br> <br>
                <div class="mt-auto text-center text-muted small pt-5">
                    &copy; 2025 ClubConnect. Privacy & Termini.
                </div>
            </div>
        </div>

        <div class="col-lg-6 visual-side">

            <div style="position: absolute; top: -10%; right: -10%; width: 300px; height: 300px; background: #26A9BC; filter: blur(150px); opacity: 0.3; border-radius: 50%;"></div>
            <div style="position: absolute; bottom: -10%; left: -10%; width: 300px; height: 300px; background: #1E3A5F; filter: blur(150px); opacity: 0.5; border-radius: 50%;"></div>

            <div class="glass-card">
                <div class="mb-4">
                    <i class="fa-solid fa-users-viewfinder" style="font-size: 3rem; color: #26A9BC;"></i>
                </div>
                <h3 class="fw-bold mb-3" style="color: white;">Gestisci la tua Community</h3>
                <p class="text-white-50 mb-0">
                    "ClubConnect ha trasformato il modo in cui gestiamo le iscrizioni. Niente più fogli Excel, solo più tempo per lo sport."
                </p>
                <div class="mt-4 pt-4 border-top border-light border-opacity-25 d-flex align-items-center justify-content-center gap-3">
                    <div style="width: 40px; height: 40px; background: white; border-radius: 50%; padding: 2px;">
                        <img src="https://ui-avatars.com/api/?name=Marco+Rossi&background=random" class="rounded-circle w-100 h-100">
                    </div>
                    <div class="text-start lh-1">
                        <div class="small fw-bold">Marco Rossi</div>
                        <div style="font-size: 0.75rem; opacity: 0.7;">Presidente Polisportiva</div>
                    </div>
                </div>
            </div>

        </div>

    </div>
</div>


<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
