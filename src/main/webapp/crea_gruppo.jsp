<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Application.GestioneAccount.UtenteBean" %>
<%
  UtenteBean utente = (UtenteBean) session.getAttribute("utente");
  if(utente == null) { response.sendRedirect("login.jsp"); return; }
%>
<!DOCTYPE html>
<html lang="it">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Crea Gruppo | ClubConnect</title>

  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
  <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">

  <style>
    body { background-color: var(--club-bg); }

    .creation-container { max-width: 900px; margin: 40px auto; }

    /* Card personalizzate per la scelta del tipo */
    .type-selector-card {
      cursor: pointer;
      border: 2px solid transparent;
      transition: all 0.3s ease;
      text-align: center;
      padding: 2rem;
      height: 100%;
    }

    .type-selector-card:hover { transform: translateY(-5px); }

    input[type="radio"]:checked + .type-selector-card {
      border-color: var(--club-teal);
      background-color: rgba(38, 169, 188, 0.05);
    }

    .type-icon {
      font-size: 2.5rem;
      color: var(--club-blue);
      margin-bottom: 1rem;
    }

    /* Sezione Club - Nascosta di default */
    #clubDetails { display: none; border-left: 4px solid var(--club-teal); padding-left: 20px; }

    .form-section-title {
      font-size: 1.1rem;
      border-bottom: 2px solid #e2e8f0;
      padding-bottom: 10px;
      margin-bottom: 20px;
      margin-top: 30px;
    }
  </style>
</head>
<body>

<nav class="navbar navbar-expand-lg bg-white shadow-sm py-3">
  <div class="container">
    <a class="navbar-brand d-flex align-items-center gap-2 fw-bold fs-4" href="#">
      <div class="brand-icon" style="width: 40px; height: 40px;">
        <img src="./images/logo.png" alt="Logo" class="img-fluid" onerror="this.style.display='none'; this.parentElement.innerHTML='<i class=\'fa-solid fa-users text-primary fs-2\'></i>'">
      </div>
      <span style="color: #1E3A5F">Club<span style="color: #26A9BC">Connect</span></span>
    </a>
  </div>
</nav>

<div class="container creation-container">
  <div class="text-center mb-5">
    <h1 class="display-5 fw-bold">Crea la tua Community</h1>
    <p class="text-muted">Trasforma la tua associazione o il tuo club in una realtà digitale.</p>
  </div>

  <form action="CreazioneGruppoServlet" method="POST" enctype="multipart/form-data" class="card p-4 p-md-5 rounded-4 shadow-sm">

    <h4 class="form-section-title"><i class="fa-solid fa-layer-group me-2"></i>Tipo di Organizzazione</h4>
    <div class="row g-4 mb-4">
      <div class="col-md-6">
        <label class="w-100">
          <input type="radio" name="tipoGruppo" value="Associazione" class="btn-check" checked onclick="toggleClubFields(false)">
          <div class="card type-selector-card rounded-4">
            <div class="type-icon"><i class="fa-solid fa-hand-holding-heart"></i></div>
            <h5 class="fw-bold">Associazione</h5>
            <p class="small text-muted mb-0">No-profit, ideale per circoli culturali o volontariato.</p>
          </div>
        </label>
      </div>
      <div class="col-md-6">
        <label class="w-100">
          <input type="radio" name="tipoGruppo" value="Club" class="btn-check" onclick="toggleClubFields(true)">
          <div class="card type-selector-card rounded-4">
            <div class="type-icon"><i class="fa-solid fa-medal"></i></div>
            <h5 class="fw-bold">Club</h5>
            <p class="small text-muted mb-0">Include la gestione di rette e quote associative.</p>
          </div>
        </label>
      </div>
    </div>

    <h4 class="form-section-title"><i class="fa-solid fa-circle-info me-2"></i>Informazioni Principali</h4>
    <div class="row g-3">
      <div class="col-md-8">
        <div class="form-floating mb-3">
          <input type="text" class="form-control" id="nome" name="nome" placeholder="Nome" required>
          <label for="nome">Nome del Gruppo</label>
        </div>
      </div>
      <div class="col-md-4">
        <div class="form-floating mb-3">
          <select class="form-select" id="settore" name="settore" required>
            <option value="" selected disabled>Scegli...</option>
            <option value="Sport">Sport</option>
            <option value="Cultura">Cultura</option>
            <option value="Sociale">Sociale</option>
            <option value="Tecnologia">Tecnologia</option>
          </select>
          <label for="settore">Settore</label>
        </div>
      </div>
      <div class="col-12">
        <div class="form-floating mb-3">
          <input type="text" class="form-control" id="slogan" name="slogan" placeholder="Slogan">
          <label for="slogan">Slogan (motto breve)</label>
        </div>
      </div>
      <div class="col-12">
        <div class="form-floating mb-3">
          <textarea class="form-control" id="descrizione" name="descrizione" style="height: 120px" placeholder="Descrizione" required></textarea>
          <label for="descrizione">Descrizione dettagliata</label>
        </div>
      </div>
    </div>

    <h4 class="form-section-title"><i class="fa-solid fa-map-location-dot me-2"></i>Sede e Regolamento</h4>
    <div class="row g-3">
      <div class="col-md-6">
        <div class="form-floating mb-3">
          <input type="text" class="form-control" id="sede" name="sede" placeholder="Sede" required>
          <label for="sede">Indirizzo Sede</label>
        </div>
      </div>
      <div class="col-md-6">
        <div class="mb-3">
          <label for="logo" class="form-label small fw-bold text-muted">Logo del Gruppo</label>
          <input type="file" class="form-control rounded-3" id="logo" name="logo" accept="image/*">
        </div>
      </div>
      <div class="col-12">
        <div class="form-floating mb-3">
          <textarea class="form-control" id="regole" name="regole" style="height: 100px" placeholder="Regole"></textarea>
          <label for="regole">Regolamento interno (opzionale)</label>
        </div>
      </div>
    </div>

    <div id="clubDetails">
      <h4 class="form-section-title"><i class="fa-solid fa-hand-holding-dollar me-2"></i>Dettagli Retta</h4>
      <div class="row g-3">
        <div class="col-md-6">
          <div class="form-floating mb-3">
            <input type="number" step="0.01" class="form-control" id="importo" name="importo" placeholder="0.00">
            <label for="importo">Importo Retta (€)</label>
          </div>
        </div>
        <div class="col-md-6">
          <div class="form-floating mb-3">
            <select class="form-select" id="frequenza" name="frequenza">
              <option value="Mensile" selected>Mensile</option>
              <option value="Trimestrale">Trimestrale</option>
              <option value="Annuale">Annuale</option>
            </select>
            <label for="frequenza">Frequenza Pagamento</label>
          </div>
        </div>
      </div>
    </div>

    <div class="mt-5 d-flex gap-3">
      <a href="feed.jsp" class="btn btn-outline-secondary w-50 py-3 fw-bold rounded-pill">Annulla</a>
      <button type="submit" class="btn btn-primary w-50 py-3 fw-bold rounded-pill shadow">Crea Ora</button>
    </div>

  </form>
</div>

<script>
  function toggleClubFields(show) {
    const details = document.getElementById('clubDetails');
    const importoInput = document.getElementById('importo');
    details.style.display = show ? 'block' : 'none';
    importoInput.required = show;
  }
</script>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>