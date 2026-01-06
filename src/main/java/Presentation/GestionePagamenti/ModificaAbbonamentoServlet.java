package Presentation.GestionePagamenti;

import Application.GestioneAccount.UtenteBean;
import Application.GestionePagamenti.GestionePagamentiBean;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(name = "ModificaAbbonamentoServlet", urlPatterns = {"/ModificaAbbonamentoServlet"})
public class ModificaAbbonamentoServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UtenteBean utente = (UtenteBean) session.getAttribute("utente");

        // Controllo Login
        if (utente == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Recupero Parametri
        String idGruppoStr = request.getParameter("idGruppo");
        String importoStr = request.getParameter("importo");
        String frequenzaStr = request.getParameter("frequenza");

        if (idGruppoStr == null || importoStr == null || frequenzaStr == null) {
            request.setAttribute("errore", "Dati mancanti per la modifica.");
            // response.sendRedirect("VisualizzaGruppoServlet?id=" + idGruppoStr);
            return;
        }

        try {
            int idGruppo = Integer.parseInt(idGruppoStr);
            double importo = Double.parseDouble(importoStr);
            int frequenza = Integer.parseInt(frequenzaStr);

            GestionePagamentiBean service = new GestionePagamentiBean();

            // Controllo Permessi: L'utente è il gestore?
            if (!service.isUtenteGestore(utente.getId_utente(), idGruppo)) {
                response.sendRedirect("feedServlet"); // O pagina di errore "Non autorizzato"
                return;
            }

            // Esecuzione Modifica
            // Chiamiamo modificaAbbonamento (o impostaAbbonamento, è uguale)
            boolean successo = service.modificaAbbonamento(idGruppo, importo, frequenza);

            if (successo) {
                // Redirect alla pagina del gruppo con messaggio di successo
                response.sendRedirect("VisualizzaGruppoServlet?id=" + idGruppo + "&msg=ModificaOk");
            } else {
                request.setAttribute("errore", "Errore durante la modifica (Il gruppo è un Club?)");
                request.getRequestDispatcher("feedServlet").forward(request, response);
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.sendRedirect("feedServlet"); // Errore formato numeri
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("feedServlet");
    }
}