package Presentation.GestioneGruppo;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneGruppo.ClubBean;
import Application.GestioneGruppo.GestioneGruppoBean;
import Application.GestioneGruppo.GruppoBean;
import Application.GestionePagamenti.GestionePagamentiBean;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "VisualizzaSociServlet", urlPatterns = {"/VisualizzaSociServlet"})
public class VisualizzaSociServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        // Controllo login opzionale (se la pagina è riservata)
        if (session.getAttribute("utente") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect("index.jsp");
            return;
        }

        try {
            int idGruppo = Integer.parseInt(idStr);
            GestioneGruppoBean gruppoService = new GestioneGruppoBean();

            // Recupero il Gruppo
            GruppoBean gruppo = gruppoService.recuperaGruppo(idGruppo);
            if (gruppo == null) {
                response.sendRedirect("index.jsp"); // Gruppo non trovato
                return;
            }

            // Recupero la Lista dei Soci
            // (Nota: Devi aggiungere questo metodo nel tuo GestioneGruppoBean, vedi sotto)
            List<UtenteBean> listaSoci = gruppoService.recuperaSociDelGruppo(idGruppo);

            // Se è un CLUB, calcolo lo stato dei pagamenti
            Map<Integer, Boolean> statoPagamenti = new HashMap<>();

            if (gruppo instanceof ClubBean) {
                ClubBean club = (ClubBean) gruppo;
                GestionePagamentiBean pagamentiService = new GestionePagamentiBean();


                statoPagamenti = pagamentiService.getSituazionePagamenti(club.getId_gruppo(), listaSoci, club.getFrequenza());
            }

            // Imposto gli attributi per la JSP
            request.setAttribute("gruppo", gruppo);
            request.setAttribute("listaSoci", listaSoci);
            request.setAttribute("statoPagamenti", statoPagamenti); // Sarà vuota se non è un club, ma non null

            // Forward alla JSP
            request.getRequestDispatcher("VisualizzaSoci.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect("index.jsp");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errore", "Errore nel recupero dei soci.");
            request.getRequestDispatcher("index.jsp").forward(request, response); // O pagina errore
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}