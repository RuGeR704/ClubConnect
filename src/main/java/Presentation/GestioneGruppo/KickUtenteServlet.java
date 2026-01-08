package Presentation.GestioneGruppo;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneGruppo.GruppoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/KickUtenteServlet")
public class KickUtenteServlet extends HttpServlet {

    private GruppoService service = new GruppoService();

    public void setGruppoService(GruppoService service) {
        this.service = service;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        UtenteBean gestore = (session != null) ? (UtenteBean) session.getAttribute("utente") : null;

        if (gestore == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String idGruppoStr = request.getParameter("idGruppo");
        String idTargetStr = request.getParameter("idUtente"); // Allineato alla tua JSP

        if (idGruppoStr == null || idTargetStr == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametri mancanti");
            return;
        }

        try {
            int idGruppo = Integer.parseInt(idGruppoStr);
            int idTarget = Integer.parseInt(idTargetStr);

            boolean successo = service.espelliUtente(idGruppo, idTarget, gestore.getId_utente());

            if (successo) {
                response.sendRedirect("VisualizzaSociServlet?id=" + idGruppo + "&success=kickOk");
            } else {
                response.sendRedirect("VisualizzaSociServlet?id=" + idGruppo + "&error=kickFail");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}