package Presentation.GestioneEventi;

import Application.GestioneEventi.GestioneEventiBean;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/RimuoviEventoServlet")
public class RimuoviEventoServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handleDelete(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handleDelete(request, response);
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Recupero ID
            String idStr = request.getParameter("idEvento");
            if(idStr == null || idStr.isEmpty()) {
                throw new IllegalArgumentException("ID Evento mancante");
            }
            int idEvento = Integer.parseInt(idStr);

            // Chiamata al Service
            GestioneEventiBean service = new GestioneEventiBean();
            service.rimuoviEvento(idEvento);

            // Redirect al calendario (l'evento non esiste più)
            response.sendRedirect("visualizzaCalendario.jsp?msg=rimosso_ok");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile rimuovere evento: " + e.getMessage());
        }
    }
}
