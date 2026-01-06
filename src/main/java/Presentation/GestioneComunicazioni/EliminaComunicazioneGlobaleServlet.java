package Presentation.GestioneComunicazioni;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneComunicazioni.ComunicazioneService; // Usa la classe refattorizzata
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "EliminaComunicazioneGlobaleServlet", urlPatterns = {"/EliminaComunicazioneGlobaleServlet"})
public class EliminaComunicazioneGlobaleServlet extends HttpServlet {

    private ComunicazioneService service = new ComunicazioneService();

    // Setter per i TEST
    public void setService(ComunicazioneService service) {
        this.service = service;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UtenteBean utente = (UtenteBean) session.getAttribute("utente");

        if (utente == null || !utente.isAdmin()) {
            response.sendRedirect("login.jsp");
            return;
        }

        String idStr = request.getParameter("idComunicazione");
        if (idStr != null && !idStr.isEmpty()) {
            try {
                int idComunicazione = Integer.parseInt(idStr);
                // Usa il campo service, non 'new'
                service.rimuoviComunicazione(idComunicazione);
                request.setAttribute("messaggio", "Comunicazione eliminata.");
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("errore", "Errore durante l'eliminazione.");
            }
        }
        response.sendRedirect("VisualizzaComunicazioniGlobaliServlet");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}