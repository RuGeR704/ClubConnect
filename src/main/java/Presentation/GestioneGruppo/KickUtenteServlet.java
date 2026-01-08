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

    // 1. CAMPO DELLA CLASSE
    private GruppoService service = new GruppoService();

    // 2. SETTER PER I TEST
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
        String idTargetStr = request.getParameter("idUtenteDaEspellere");

        if (idGruppoStr == null || idTargetStr == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            int idGruppo = Integer.parseInt(idGruppoStr);
            int idTarget = Integer.parseInt(idTargetStr);

            // 3. USIAMO IL CAMPO 'service'
            boolean successo = service.espelliUtente(idGruppo, idTarget, gestore.getId_utente());

            if (successo) {
                response.sendRedirect("VisualizzaGruppoServlet?id=" + idGruppo + "&msg=kickOk");
            } else {
                request.setAttribute("errore", "Impossibile rimuovere l'utente.");
                request.getRequestDispatcher("errorPage.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}