package Presentation.GestioneEventi;

import Application.GestioneEventi.EventoBean;
import Application.GestioneEventi.GestioneEventiBean;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/VisualizzaEventoServlet")
public class VisualizzaEventoServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Recupero l'ID dell'evento dalla query string (es. visualizzaEvento?id=5)
        String idStr = request.getParameter("id");

        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect("VisualizzaCalendarioEventiServlet"); // Se manca l'ID, torno alla lista
            return;
        }

        try {
            int idEvento = Integer.parseInt(idStr);
            GestioneEventiBean service = new GestioneEventiBean();

            // Chiedo al service l'evento specifico
            EventoBean evento = service.retrieveEvento(idEvento);

            if (evento == null) {
                // Se l'evento non esiste (es. id sbagliato), errore 404
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Evento non trovato");
                return;
            }

            // Salvo l'evento nella request
            request.setAttribute("evento", evento);

            // Inoltro alla pagina di dettaglio
            request.getRequestDispatcher("visualizzaEvento.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID Evento non valido");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore recupero evento");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
