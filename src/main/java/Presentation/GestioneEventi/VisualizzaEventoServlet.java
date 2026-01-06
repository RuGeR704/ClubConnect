package Presentation.GestioneEventi;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneEventi.EventoBean;
import Application.GestioneEventi.GestioneEventiBean;
import Application.GestioneGruppo.GruppoBean;
import Storage.ConPool;
import Storage.EventoDAO;
import Storage.GruppoDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;

@WebServlet("/VisualizzaEventoServlet")
public class VisualizzaEventoServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Recupero l'ID dell'evento dalla query string (es. visualizzaEvento?id=5)
        HttpSession session = request.getSession();
        UtenteBean utente = (UtenteBean) session.getAttribute("utente");
        String idStr = request.getParameter("id");

        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect("VisualizzaCalendarioEventiServlet"); // Se manca l'ID, torno alla lista
            return;
        }

        try (Connection con = ConPool.getConnection()){
            int idEvento = Integer.parseInt(idStr);
            GestioneEventiBean service = new GestioneEventiBean();

            // Chiedo al service l'evento specifico
            EventoBean evento = service.retrieveEvento(idEvento);

            if (evento == null) {
                // Se l'evento non esiste (es. id sbagliato), errore 404
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Evento non trovato");
                return;
            }

            GruppoDAO gruppoDAO = new GruppoDAO();
            GruppoBean gruppo = gruppoDAO.doRetrieveByid(con, evento.getId_gruppo());

            boolean isPartecipante = false;
            boolean isAdmin = false;
            if (utente != null) {
                EventoDAO eventoDAO = new EventoDAO();
                isPartecipante = eventoDAO.isUtentePartecipante(con, utente.getId_utente(), idEvento);

                if (gruppo != null && gruppo.isUtenteGestore(utente.getId_utente(), gruppo.getId_gruppo())) {
                    isAdmin = true;
                }

            }
            // Salvo l'evento nella request
            request.setAttribute("evento", evento);
            request.setAttribute("isPartecipante", isPartecipante);
            request.setAttribute("isAdmin", isAdmin);
            request.setAttribute("gruppo", gruppo);

            // Inoltro alla pagina di dettaglio
            request.getRequestDispatcher("WEB-INF/visualizzaEvento.jsp").forward(request, response);

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
