<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="it">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Ops! Qualcosa è andato storto | ClubConnect</title>

  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

  <style>
    body {
      background-color: #f4f6f9;
      height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    }

    .error-card {
      background: white;
      padding: 3rem;
      border-radius: 24px;
      box-shadow: 0 10px 40px rgba(0,0,0,0.08);
      text-align: center;
      max-width: 500px;
      width: 90%;
      border: 1px solid rgba(0,0,0,0.02);
      animation: float 6s ease-in-out infinite;
    }

    .brand-logo {
      width: 80px;
      height: 80px;
      margin-bottom: 1.5rem;
      object-fit: contain;
    }

    .error-code {
      font-size: 5rem;
      font-weight: 800;
      background: -webkit-linear-gradient(45deg, #1E3A5F, #26A9BC);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      line-height: 1;
      margin-bottom: 1rem;
    }

    .btn-home {
      background-color: #1E3A5F;
      color: white;
      padding: 12px 30px;
      border-radius: 50px;
      font-weight: bold;
      transition: transform 0.2s;
      text-decoration: none;
      display: inline-block;
      border: none;
    }

    .btn-home:hover {
      background-color: #152943;
      color: white;
      transform: translateY(-3px);
      box-shadow: 0 5px 15px rgba(30, 58, 95, 0.3);
    }

    @keyframes float {
      0% { transform: translateY(0px); }
      50% { transform: translateY(-10px); }
      100% { transform: translateY(0px); }
    }
  </style>
</head>
<body>

<div class="error-card">
  <img src="<%= request.getContextPath() %>/images/logo.png" alt="ClubConnect Logo" class="brand-logo">

  <%
    Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
    String errorMessage = "Si è verificato un errore imprevisto.";

    if (statusCode != null) {
      if (statusCode == 404) errorMessage = "La pagina che cerchi non esiste.";
      else if (statusCode == 500) errorMessage = "Errore interno del server.";
      else if (statusCode == 403) errorMessage = "Accesso negato.";
    } else {
      statusCode = 0; // Errore generico
      errorMessage = "Qualcosa non ha funzionato.";
    }
  %>

  <div class="error-code">
    <%= (statusCode != 0) ? statusCode : "Ops!" %>
  </div>

  <h4 class="fw-bold text-dark mb-3">Qualcosa è andato storto</h4>
  <p class="text-muted mb-4"><%= errorMessage %></p>

  <a href="feedServlet" class="btn-home">
    <i class="fa-solid fa-house me-2"></i>Torna alla Home
  </a>
</div>

</body>
</html>
