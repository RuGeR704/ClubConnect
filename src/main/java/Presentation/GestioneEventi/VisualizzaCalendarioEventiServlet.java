package Presentation.GestioneEventi;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneEventi.EventoBean;
import Application.GestioneEventi.GestioneEventiBean;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/VisualizzaCalendarioEventiServlet")
public class VisualizzaCalendarioEventiServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();
        UtenteBean utente = (UtenteBean) session.getAttribute("utente");

        if (utente == null) {
            response.sendRedirect("login.jsp");
        }

        GestioneEventiBean service = new GestioneEventiBean();

        try {
            // Chiedo al service la lista di tutti gli eventi
            List<EventoBean> listaEventi = service.retrieveAllEventi();
            List<EventoBean> listaEventiUtente = service.retrieveEventiUtente(utente.getId_utente());

            // Salvo la lista nella request per passarla alla JSP
            request.setAttribute("eventi", listaEventi);
            request.setAttribute("eventiUtente", listaEventiUtente);

            // Inoltro alla pagina JSP che mostrerà la griglia/tabella
            request.getRequestDispatcher("WEB-INF/visualizzaCalendario.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile caricare il calendario: " + e.getMessage());
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
