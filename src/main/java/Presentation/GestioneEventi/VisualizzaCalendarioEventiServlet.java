package Presentation.GestioneEventi;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneEventi.EventoBean;
import Application.GestioneEventi.EventoService;
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

    private EventoService service = new EventoService();

    public void setService(EventoService service) {
        this.service = service;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UtenteBean utente = (UtenteBean) session.getAttribute("utente");

        if (utente == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            List<EventoBean> listaEventi = service.retrieveAllEventi();
            List<EventoBean> listaEventiUtente = service.retrieveEventiUtente(utente.getId_utente());

            request.setAttribute("eventi", listaEventi);
            request.setAttribute("eventiUtente", listaEventiUtente);
            request.getRequestDispatcher("WEB-INF/visualizzaCalendario.jsp").forward(request, response);

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}