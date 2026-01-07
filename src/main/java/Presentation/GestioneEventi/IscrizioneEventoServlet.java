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

    private IscrizioneFacade facade = new IscrizioneFacade();

    public void setFacade(IscrizioneFacade facade) {
        this.facade = facade;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UtenteBean utente = (UtenteBean) session.getAttribute("utente");

        if (utente == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            int idEvento = Integer.parseInt(request.getParameter("idEvento"));
            String action = request.getParameter("action");
            boolean successo = false;

            if ("join".equals(action))
                successo = facade.iscriviUtente(utente, idEvento);
            else if ("leave".equals(action))
                successo = facade.disiscriviUtente(utente, idEvento);

            if (successo) {
                session.setAttribute("successo", "Operazione effettuata con successo!");
                response.sendRedirect("feedServlet");
            } else {
                request.setAttribute("errore", "Impossibile completare l'operazione.");
                request.getRequestDispatcher("visualizzaEvento.jsp?id=" + idEvento).forward(request, response);
            }

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore durante l'iscrizione");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}