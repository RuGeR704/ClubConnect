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

        if (session.getAttribute("utente") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect("feedServlet");
            return;
        }

        try {
            int idGruppo = Integer.parseInt(idStr);
            GestioneGruppoBean gruppoService = new GestioneGruppoBean();

            GruppoBean gruppo = gruppoService.recuperaGruppo(idGruppo);
            if (gruppo == null) {
                response.sendRedirect("feedServlet");
                return;
            }

            // 1. Recupero la lista dei soci e la mappa dei ruoli
            List<UtenteBean> listaSoci = gruppoService.recuperaSociDelGruppo(idGruppo);
            Map<Integer, Boolean> mappaGestori = gruppoService.recuperaMappaRuoli(idGruppo);

            // 2. LOGICA DI ORDINAMENTO: Mettiamo i gestori in cima
            if (listaSoci != null && mappaGestori != null) {
                listaSoci.sort((u1, u2) -> {
                    boolean g1 = mappaGestori.getOrDefault(u1.getId_utente(), false);
                    boolean g2 = mappaGestori.getOrDefault(u2.getId_utente(), false);

                    // Ordine decrescente (i true prima dei false)
                    return Boolean.compare(g2, g1);
                });
            }

            // 3. Calcolo pagamenti (rimane uguale)
            Map<Integer, Boolean> statoPagamenti = new HashMap<>();
            if (gruppo instanceof ClubBean) {
                ClubBean club = (ClubBean) gruppo;
                GestionePagamentiBean pagamentiService = new GestionePagamentiBean();
                statoPagamenti = pagamentiService.getSituazionePagamenti(club.getId_gruppo(), listaSoci, club.getFrequenza());
            }

            // Imposto gli attributi
            request.setAttribute("gruppo", gruppo);
            request.setAttribute("listaSoci", listaSoci); // Ora è ordinata!
            request.setAttribute("statoPagamenti", statoPagamenti);
            request.setAttribute("mappaGestori", mappaGestori);

            request.getRequestDispatcher("WEB-INF/VisualizzaSoci.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect("feedServlet");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }
}