package Presentation.GestioneEventi;

import Application.GestioneEventi.EventoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/RimuoviEventoServlet")
public class RimuoviEventoServlet extends HttpServlet {

    private EventoService service = new EventoService();

    public void setService(EventoService service) {
        this.service = service;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String idStr = request.getParameter("idEvento");
            if(idStr == null) throw new IllegalArgumentException("ID mancante");

            try {
                service.rimuoviEvento(Integer.parseInt(idStr));
                // Aggiungo il parametro all'URL
                response.sendRedirect("VisualizzaCalendarioEventiServlet?esito=success");
            } catch (Exception e) {
                e.printStackTrace();
                // In caso di errore
                response.sendRedirect("VisualizzaCalendarioEventiServlet?esito=errore");
            }

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}