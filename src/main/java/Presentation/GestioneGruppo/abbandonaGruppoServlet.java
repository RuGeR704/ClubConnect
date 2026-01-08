package Presentation.GestioneGruppo;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneGruppo.GruppoService; // Usa il service
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/abbandonaGruppoServlet")
public class abbandonaGruppoServlet extends HttpServlet {

    private GruppoService gruppoService = new GruppoService(); // Injection

    public void setGruppoService(GruppoService gs) {
        this.gruppoService = gs;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        UtenteBean utente = (session != null) ? (UtenteBean) session.getAttribute("utente") : null;

        if (utente == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String idGruppoStr = request.getParameter("idGruppo");
        int idUtente = utente.getId_utente();

        if (idGruppoStr == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametri mancanti");
            return;
        }

        try {
            int idGruppo = Integer.parseInt(idGruppoStr);

            // Usa il service invece del DAO diretto.
            // Nota: Ho aggiunto un metodo wrapper 'abbandonaGruppo' al service per chiarezza,
            // oppure puoi usare 'espelliUtente' se la logica è identica (rimozione membro),
            // ma 'espelliUtente' fa controlli di admin che qui non servono (l'utente si rimuove da solo).
            // Dobbiamo aggiungere 'abbandonaGruppo' al Service o usare direttamente il DAO wrappato se c'è un metodo semplice.
            // Assumiamo che tu aggiunga 'rimuoviMembro(idGruppo, idUtente)' al Service che non fa controlli admin.
            boolean esito = gruppoService.rimuoviMembro(idGruppo, idUtente); // DA AGGIUNGERE AL SERVICE

            if (esito) {
                session.setAttribute("successMsg", "Hai abbandonato il gruppo correttamente.");
                response.sendRedirect(request.getContextPath() + "/VisualizzaIscrizioniGruppiServlet");
            } else {
                session.setAttribute("errorMsg", "Impossibile abbandonare il gruppo.");
                response.sendRedirect(request.getContextPath() + "/VisualizzaGruppoServlet?id=" + idGruppo);
            }

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID Gruppo non valido");
        } catch (Exception e) { // Catch generico per SQLException dal service
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        }
    }
}