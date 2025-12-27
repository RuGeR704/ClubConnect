<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String nomeUtente = (String) request.getAttribute("nome");
    if (nomeUtente == null) nomeUtente = "Utente"; // Fallback se il nome non è passato
%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Benvenuto | ClubConnect</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style-bootstrap.css">

    <style>
        /* SFONDO BRANDIZZATO (Lo stesso della login visual side) */
        body {
            background-color: #1E3A5F;
            background-image:
                    radial-gradient(circle at 15% 50%, rgba(38, 169, 188, 0.15) 0%, transparent 25%),
                    radial-gradient(circle at 85% 30%, rgba(38, 169, 188, 0.15) 0%, transparent 25%),
                    linear-gradient(135deg, #1E3A5F 0%, #0f2035 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: 'Poppins', sans-serif;
            overflow: hidden;
        }

        /* CARD DI BENVENUTO */
        .welcome-card {
            background: white;
            border-radius: 24px;
            padding: 3rem;
            max-width: 600px;
            width: 90%;
            text-align: center;
            position: relative;
            box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5);

            /* Animazione Ingresso */
            opacity: 0;
            transform: translateY(30px);
            animation: slideUpFade 0.8s cubic-bezier(0.2, 0.8, 0.2, 1) forwards;
        }

        /* ICONA SUCCESSO ANIMATA */
        .success-icon-wrapper {
            width: 80px; height: 80px;
            background: #d1fae5;
            color: #10b981;
            border-radius: 50%;
            display: flex; align-items: center; justify-content: center;
            font-size: 2.5rem;
            margin: -70px auto 20px auto; /* Esce leggermente dalla card */
            border: 5px solid #1E3A5F; /* Colore dello sfondo body per stacco netto */

            /* Animazione Pop */
            animation: popIn 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275) 0.3s forwards;
            transform: scale(0); /* Parte invisibile */
        }

        /* FEATURES BOXES (Le due opzioni) */
        .feature-box {
            background: #f8fafc;
            border: 2px solid #e2e8f0;
            border-radius: 16px;
            padding: 1.5rem;
            transition: all 0.3s ease;
            height: 100%;
        }

        .feature-box:hover {
            border-color: #26A9BC;
            background: white;
            transform: translateY(-5px);
            box-shadow: 0 10px 20px rgba(38, 169, 188, 0.1);
        }

        .feature-icon {
            width: 50px; height: 50px;
            border-radius: 12px;
            display: flex; align-items: center; justify-content: center;
            font-size: 1.5rem;
            margin: 0 auto 1rem auto;
        }

        /* BOTTONE CONTINUA */
        .btn-continue {
            background-color: #26A9BC;
            color: white;
            font-weight: 600;
            padding: 1rem 3rem;
            border-radius: 50px;
            border: none;
            font-size: 1.1rem;
            transition: all 0.3s ease;
            box-shadow: 0 4px 6px -1px rgba(38, 169, 188, 0.3);
        }

        .btn-continue:hover {
            background-color: #1e8a9b;
            transform: scale(1.05);
            box-shadow: 0 10px 15px -3px rgba(38, 169, 188, 0.4);
            color: white;
        }

        /* KEYFRAMES ANIMAZIONI */
        @keyframes slideUpFade {
            to { opacity: 1; transform: translateY(0); }
        }
        @keyframes popIn {
            to { transform: scale(1); }
        }

        /* Coriandoli CSS (Opzionale decorativo) */
        .confetti {
            position: absolute; width: 10px; height: 10px; background-color: #F4C430;
            animation: fall linear infinite; opacity: 0;
        }
    </style>
</head>
<body>

<div class="welcome-card">

    <div class="success-icon-wrapper">
        <i class="fa-solid fa-check"></i>
    </div>

    <h1 class="fw-bold mb-2" style="color: #1E3A5F;">Benvenuto a bordo!</h1>
    <p class="text-muted mb-5 fs-5">
        Ciao <strong><%= nomeUtente %></strong>, il tuo account è attivo.<br>
        Ecco cosa puoi fare con ClubConnect:
    </p>

    <div class="row g-4 mb-5">

        <div class="col-md-6">
            <div class="feature-box">
                <div class="feature-icon" style="background: rgba(30, 58, 95, 0.1); color: #1E3A5F;">
                    <i class="fa-solid fa-users-gear"></i>
                </div>
                <h5 class="fw-bold" style="color: #1E3A5F;">Gestisci Gruppi</h5>
                <p class="small text-muted mb-0">
                    Sei un amministratore? Crea il tuo gruppo, gestisci i soci e organizza eventi.
                </p>
            </div>
        </div>

        <div class="col-md-6">
            <div class="feature-box">
                <div class="feature-icon" style="background: rgba(38, 169, 188, 0.1); color: #26A9BC;">
                    <i class="fa-solid fa-heart"></i>
                </div>
                <h5 class="fw-bold" style="color: #1E3A5F;">Partecipa</h5>
                <p class="small text-muted mb-0">
                    Cerca i club nella tua zona, iscriviti alle attività e rimani sempre aggiornato.
                </p>
            </div>
        </div>
    </div>

    <a href="feed.jsp" class="btn btn-continue">
        Entra in ClubConnect <i class="fa-solid fa-arrow-right ms-2"></i>
    </a>

</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<script>
    function createConfetti() {
        const colors = ['#26A9BC', '#1E3A5F', '#F4C430', '#E65142'];
        const container = document.body;

        for(let i=0; i<30; i++) {
            const conf = document.createElement('div');
            conf.classList.add('confetti');
            conf.style.left = Math.random() * 100 + 'vw';
            conf.style.top = -10 + 'px';
            conf.style.backgroundColor = colors[Math.floor(Math.random() * colors.length)];
            conf.style.animation = `fall ${Math.random() * 3 + 2}s linear forwards`;
            conf.style.opacity = 1;

            // Animazione di caduta definita via JS per semplicità
            const styleSheet = document.createElement("style");
            styleSheet.innerText = `
                    @keyframes fall {
                        to { transform: translateY(100vh) rotate(720deg); opacity: 0; }
                    }
                `;
            document.head.appendChild(styleSheet);

            container.appendChild(conf);
        }
    }
    // Avvia coriandoli al caricamento
    window.onload = createConfetti;
</script>
</body>
</html>
