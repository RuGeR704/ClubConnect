package Presentation.GestioneGruppo;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneGruppo.GruppoService;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "IscrizioneGruppoServlet", value = "/IscrizioneGruppoServlet")
public class IscrizioneGruppoServlet extends HttpServlet {

    // 1. CAMPO DELLA CLASSE (Non dentro il metodo!)
    private GruppoService service = new GruppoService();

    // 2. SETTER PER I TEST
    public void setGruppoService(GruppoService service) {
        this.service = service;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        UtenteBean utente = (session != null) ? (UtenteBean) session.getAttribute("utente") : null;

        if (utente == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String idGruppoStr = request.getParameter("id_gruppo");
        if (idGruppoStr == null || idGruppoStr.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID Gruppo mancante");
            return;
        }

        try {
            int idGruppo = Integer.parseInt(idGruppoStr);
            // 3. USIAMO IL CAMPO 'service'
            service.iscriviUtenteAlGruppo(utente.getId_utente(), idGruppo);
            response.sendRedirect("VisualizzaGruppoServlet?id=" + idGruppo);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID Gruppo non valido");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}