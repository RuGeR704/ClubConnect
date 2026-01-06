package Presentation.GestioneComunicazioni;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneComunicazioni.ComunicazioniBean;
import Application.GestioneComunicazioni.ComunicazioneService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "EliminaComunicazioneGruppoServlet", urlPatterns = {"/EliminaComunicazioneGruppoServlet"})
public class EliminaComunicazioneGruppoServlet extends HttpServlet {

    private ComunicazioneService service = new ComunicazioneService();

    public void setService(ComunicazioneService service) {
        this.service = service;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UtenteBean utente = (UtenteBean) session.getAttribute("utente");

        if (utente == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String idStr = request.getParameter("idComunicazione");
        if (idStr != null) {
            try {
                int id = Integer.parseInt(idStr);
                // Recupera per controllare i permessi
                ComunicazioniBean com = service.recuperaComunicazione(id);

                if (com != null) {
                    if (utente.isAdmin() || utente.getId_utente() == com.getId_autore()) {
                        service.rimuoviComunicazione(id);
                        response.sendRedirect("VisualizzaComunicazioniGruppoServlet?idGruppo=" + com.getId_gruppo());
                        return;
                    } else {
                        request.setAttribute("errore", "Permessi negati.");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        response.sendRedirect("feedServlet");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}