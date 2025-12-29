package Presentation.GestioneEventi;

import Application.GestioneEventi.IscrizioneFacade;
import Application.GestioneAccount.UtenteBean;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/IscrizioneEventoServlet")
public class IscrizioneEventoServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 1. Controllo Login: Solo gli utenti loggati possono iscriversi
        HttpSession session = request.getSession();
        UtenteBean utente = (UtenteBean) session.getAttribute("utente");

        if (utente == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            // 2. Recupero Parametri
            int idEvento = Integer.parseInt(request.getParameter("idEvento"));

            // Se ci fosse un pagamento, recupereremmo qui i dati della carta
            // String metodoPagamento = request.getParameter("metodoPagamento");

            // 3. CHIAMATA AL FACADE (Come da SDD)
            // La servlet non sa nulla di DAO, pagamenti o email. Delega tutto.
            IscrizioneFacade facade = new IscrizioneFacade();

            boolean successo = facade.iscriviUtente(utente, idEvento);

            if (successo) {
                // 4a. Successo
                response.sendRedirect("visualizzaCalendario.jsp?msg=iscrizione_ok");
            } else {
                // 4b. Fallimento (es. posti esauriti)
                request.setAttribute("errore", "Impossibile iscriversi: posti esauriti o errore generico.");
                request.getRequestDispatcher("visualizzaEvento.jsp?id=" + idEvento).forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore durante l'iscrizione");
        }
    }

    // Gestisce anche le chiamate GET se l'iscrizione avviene tramite link diretto (meno sicuro ma comune)
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
