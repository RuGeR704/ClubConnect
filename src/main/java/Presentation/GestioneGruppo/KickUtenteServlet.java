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

        HttpSession session = request.getSession(false);
        UtenteBean gestoreRichiedente = (session != null) ? (UtenteBean) session.getAttribute("utente") : null;

        if (gestoreRichiedente == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Recupero Parametri (ora allineati ai nomi 'idGruppo' e 'idUtente' della JSP)
        String idGruppoStr = request.getParameter("idGruppo");
        String idUtenteTargetStr = request.getParameter("idUtente");

        if (idGruppoStr == null || idUtenteTargetStr == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametri mancanti");
            return;
        }

        try {
            int idGruppo = Integer.parseInt(idGruppoStr);
            int idUtenteDaEspellere = Integer.parseInt(idUtenteTargetStr);
            int idGestore = gestoreRichiedente.getId_utente();

            GestioneGruppoBean service = new GestioneGruppoBean();

            // Chiamata al Service
            boolean successo = service.espelliUtente(idGruppo, idUtenteDaEspellere, idGestore);

            if (successo) {
                // REDIRECT alla lista soci aggiornata invece che alla pagina gruppo
                response.sendRedirect("VisualizzaSociServlet?id=" + idGruppo + "&success=kickOk");
            } else {
                // In caso di errore, torniamo alla lista soci mostrando un messaggio
                response.sendRedirect("VisualizzaSociServlet?id=" + idGruppo + "&error=kickFail");
            }

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID non validi");
        }
    }
}