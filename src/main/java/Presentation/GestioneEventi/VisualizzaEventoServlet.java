package Presentation.GestioneEventi;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneEventi.EventoBean;
import Application.GestioneEventi.EventoService;
import Application.GestioneGruppo.GruppoBean;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/VisualizzaEventoServlet")
public class VisualizzaEventoServlet extends HttpServlet {

    private EventoService service = new EventoService();

    public void setService(EventoService service) {
        this.service = service;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UtenteBean utente = (UtenteBean) session.getAttribute("utente");
        String idStr = request.getParameter("id");

        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect("VisualizzaCalendarioEventiServlet");
            return;
        }

        try {
            int idEvento = Integer.parseInt(idStr);

            // 1. Recupero Evento
            EventoBean evento = service.retrieveEvento(idEvento);

            if (evento == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Evento non trovato");
                return;
            }

            // 2. Recupero Gruppo (CORREZIONE: Uso il service, non 'new')
            GruppoBean gruppo = service.retrieveGruppo(evento.getId_gruppo());

            // 3. Logica permessi (mockata nel test, reale qui)
            boolean isPartecipante = false; // Implementare logica reale nel service se necessario
            boolean isAdmin = false;

            if (utente != null && gruppo != null) {
                // Esempio logica: isAdmin = gruppo.isUtenteGestore(utente.getId());
            }

            request.setAttribute("evento", evento);
            request.setAttribute("isPartecipante", isPartecipante);
            request.setAttribute("isAdmin", isAdmin);
            request.setAttribute("gruppo", gruppo);

            request.getRequestDispatcher("WEB-INF/visualizzaEvento.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}