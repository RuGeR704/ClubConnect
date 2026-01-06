package Presentation.GestioneAccount;

import Application.GestioneAccount.AccountService;
import Application.GestioneAccount.UtenteBean;
import Application.GestioneGruppo.GruppoBean;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "VisualizzaIscrizioniGruppiServlet", value = "/VisualizzaIscrizioniGruppiServlet")
public class VisualizzaIscrizioniGruppiServlet extends HttpServlet {

    private AccountService accountService = new AccountService();

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("utente") == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }

            UtenteBean utente = (UtenteBean) session.getAttribute("utente");

            // Uso del Service per entrambe le liste
            List<GruppoBean> iscrizioni = accountService.getGruppiIscritto(utente.getId_utente());
            List<GruppoBean> gruppiAdmin = accountService.getGruppiAdmin(utente.getId_utente());

            if (iscrizioni != null && !iscrizioni.isEmpty()) {
                request.setAttribute("gruppiIscritto", iscrizioni);
            }
            if (gruppiAdmin != null && !gruppiAdmin.isEmpty()) {
                request.setAttribute("gruppiAdmin", gruppiAdmin);
            }

            RequestDispatcher rd = request.getRequestDispatcher("/GruppiIscritto.jsp");
            rd.forward(request, response);

        } catch(SQLException sql){
            sql.printStackTrace();
            request.setAttribute("errore", "Errore nel recupero gruppi");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}