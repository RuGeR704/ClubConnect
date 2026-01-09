package Presentation;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneEventi.EventoBean;
import Application.GestioneFeed.FeedService;
import Application.GestioneGruppo.GruppoBean;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/feedServlet")
public class FeedServlet extends HttpServlet {

    private FeedService feedService = new FeedService();

    public void setFeedService(FeedService feedService) {
        this.feedService = feedService;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();
        UtenteBean utente = (UtenteBean) session.getAttribute("utente");

        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            // 1. Controlliamo se ha iscrizioni
            List<GruppoBean> gruppiIscritti = feedService.getGruppiIscritto(utente.getId_utente());
            List<EventoBean> eventiPrenotati = feedService.getEventiPrenotati(utente.getId_utente());

            boolean hasIscrizioni = (gruppiIscritti != null && !gruppiIscritti.isEmpty());

            if (hasIscrizioni) {
                // 2A. Se iscritto -> Carichiamo il feed misto
                List<Object> feedMisto = feedService.getFeedMisto(utente.getId_utente());
                request.setAttribute("feedMisto", feedMisto);
                request.setAttribute("gruppiSuggeriti", new ArrayList<>()); // Vuoto per evitare null in JSP
            } else {
                // 2B. Se NON iscritto -> Carichiamo i suggerimenti
                List<GruppoBean> suggeriti = feedService.getSuggerimenti();
                request.setAttribute("gruppiSuggeriti", suggeriti);
                request.setAttribute("feedMisto", new ArrayList<>()); // Vuoto
            }

            // 3. Impostiamo gli attributi comuni
            request.setAttribute("hasIscrizioni", hasIscrizioni);
            request.setAttribute("eventiPrenotati", eventiPrenotati);

            RequestDispatcher view = request.getRequestDispatcher("feed.jsp");
            view.forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doGet(request, response);
    }
}