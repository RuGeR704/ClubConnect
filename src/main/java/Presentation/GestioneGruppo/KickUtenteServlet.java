package Presentation.GestioneGruppo;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneGruppo.GestioneGruppoBean;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/KickUtenteServlet")
public class KickUtenteServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 1. Controllo Sessione
        HttpSession session = request.getSession(false);
        UtenteBean gestoreRichiedente = (session != null) ? (UtenteBean) session.getAttribute("utente") : null;

        if (gestoreRichiedente == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // 2. Recupero Parametri
        String idGruppoStr = request.getParameter("idGruppo");
        String idUtenteTargetStr = request.getParameter("idUtenteDaEspellere");

        if (idGruppoStr == null || idUtenteTargetStr == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametri mancanti");
            return;
        }

        try {
            int idGruppo = Integer.parseInt(idGruppoStr);
            int idUtenteDaEspellere = Integer.parseInt(idUtenteTargetStr);
            int idGestore = gestoreRichiedente.getId_utente();

            GestioneGruppoBean service = new GestioneGruppoBean();

            // 3. Chiamata al Service
            boolean successo = service.espelliUtente(idGruppo, idUtenteDaEspellere, idGestore);

            if (successo) {
                // Successo: Ricarica la pagina visualizzazione gruppo
                response.sendRedirect("VisualizzaGruppoServlet?id=" + idGruppo + "&msg=kickOk");
            } else {
                // Errore: Permessi negati o errore SQL
                request.setAttribute("errore", "Impossibile rimuovere l'utente. Verifica i permessi.");
                // Puoi decidere di rimandare alla pagina del gruppo con un errore o a una pagina di errore generica
                request.getRequestDispatcher("errorPage.jsp").forward(request, response);
            }

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID non validi");
        }
    }
}