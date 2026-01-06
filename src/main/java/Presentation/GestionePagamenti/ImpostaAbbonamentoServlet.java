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

@WebServlet(name = "ImpostaAbbonamentoServlet", urlPatterns = {"/ImpostaAbbonamentoServlet"})
public class ImpostaAbbonamentoServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UtenteBean utente = (UtenteBean) session.getAttribute("utente");

        // CONTROLLO LOGIN
        // Se l'utente non è loggato, lo rimandiamo al login
        if (utente == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // RECUPERO PARAMETRI
        String idGruppoStr = request.getParameter("idGruppo");
        String importoStr = request.getParameter("importo");
        String frequenzaStr = request.getParameter("frequenza");

        // Validazione base: controlliamo che i dati ci siano tutti
        if (idGruppoStr == null || importoStr == null || frequenzaStr == null) {
            request.setAttribute("errore", "Tutti i campi sono obbligatori.");
            // Rimandiamo indietro (o alla home se non sappiamo dove andare)
            response.sendRedirect("feedServlet");
            return;
        }

        try {
            // Conversione dei parametri
            int idGruppo = Integer.parseInt(idGruppoStr);
            double importo = Double.parseDouble(importoStr);
            int frequenza = Integer.parseInt(frequenzaStr);

            // Istanzio il service (Application Layer)
            GestionePagamentiBean service = new GestionePagamentiBean();

            //CONTROLLO SICUREZZA (IL GESTORE)
            // Se il metodo restituisce false, significa che è un intruso o un utente normale.
            if (!service.isUtenteGestore(utente.getId_utente(), idGruppo)) {

                // Loggo l'evento (opzionale) o preparo il messaggio
                request.setAttribute("errore", "ACCESSO NEGATO: Solo il gestore può impostare l'abbonamento.");

                // Lo rispedisco alla pagina del gruppo senza effettuare modifiche
                request.getRequestDispatcher("VisualizzaGruppoServlet?id=" + idGruppo).forward(request, response);
                return;
            }

            //ESECUZIONE LOGICA DI BUSINESS
            // Se sono arrivato qui, l'utente è autorizzato. Procedo.
            boolean successo = service.impostaAbbonamento(idGruppo, importo, frequenza);

            if (successo) {
                // Successo: Ritorna alla pagina del gruppo con un messaggio positivo
                response.sendRedirect("VisualizzaGruppoServlet?id=" + idGruppo + "&msg=AbbonamentoImpostato");
            } else {
                // Errore Logico (es. l'ID gruppo non esiste o non è un Club)
                request.setAttribute("errore", "Errore: Impossibile impostare l'abbonamento (Il gruppo è un Club?).");
                request.getRequestDispatcher("feedServlet").forward(request, response);
            }

        } catch (NumberFormatException e) {
            // Gestione errore se importo o frequenza non sono numeri validi
            e.printStackTrace();
            request.setAttribute("errore", "Formato numeri non valido.");
            request.getRequestDispatcher("feedServlet").forward(request, response);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Impediamo l'accesso
        response.sendRedirect("feedServlet");
    }
}