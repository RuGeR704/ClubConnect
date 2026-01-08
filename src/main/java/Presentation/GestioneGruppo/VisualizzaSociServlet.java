package Presentation.GestioneGruppo;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneGruppo.ClubBean;
import Application.GestioneGruppo.GruppoBean;
import Application.GestioneGruppo.GruppoService;
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

    // 1. CAMPI DELLA CLASSE
    private GruppoService gruppoService = new GruppoService();
    private GestionePagamentiBean pagamentiService = new GestionePagamentiBean();

    // 2. SETTERS PER I TEST
    public void setGruppoService(GruppoService gs) { this.gruppoService = gs; }
    public void setPagamentiService(GestionePagamentiBean ps) { this.pagamentiService = ps; }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("utente") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String idStr = request.getParameter("id");
        if (idStr == null) {
            response.sendRedirect("feedServlet");
            return;
        }

        try {
            int idGruppo = Integer.parseInt(idStr);

            // 3. USIAMO I CAMPI
            GruppoBean gruppo = gruppoService.recuperaGruppo(idGruppo);

            if (gruppo == null) {
                response.sendRedirect("feedServlet");
                return;
            }

            List<UtenteBean> listaSoci = gruppoService.recuperaSociDelGruppo(idGruppo);
            Map<Integer, Boolean> statoPagamenti = new HashMap<>();

            if (gruppo instanceof ClubBean) {
                ClubBean club = (ClubBean) gruppo;
                statoPagamenti = pagamentiService.getSituazionePagamenti(club.getId_gruppo(), listaSoci, club.getFrequenza());
            }

            request.setAttribute("gruppo", gruppo);
            request.setAttribute("listaSoci", listaSoci);
            request.setAttribute("statoPagamenti", statoPagamenti);

            request.getRequestDispatcher("WEB-INF/VisualizzaSoci.jsp").forward(request, response);

        } catch (Exception e) {
            response.sendRedirect("feedServlet");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}