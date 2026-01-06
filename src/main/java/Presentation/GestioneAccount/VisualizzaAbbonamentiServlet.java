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

@WebServlet(name = "VisualizzaAbbonamentiServlet", value = "/VisualizzaAbbonamentiServlet")
public class VisualizzaAbbonamentiServlet extends HttpServlet {

    private AccountService accountService = new AccountService();

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            if(session == null || session.getAttribute("utente") == null){
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }

            UtenteBean utente = (UtenteBean) session.getAttribute("utente");

            // Uso del Service
            List<GruppoBean> clubiscritti = accountService.getGruppiIscritto(utente.getId_utente());

            if(clubiscritti != null && !clubiscritti.isEmpty()){
                request.setAttribute("ClubIscrizioni", clubiscritti);
            }
            RequestDispatcher rd = request.getRequestDispatcher("/Abbonamenti.jsp");
            rd.forward(request, response);

        } catch(SQLException sql){
            sql.printStackTrace();
            request.setAttribute("errore", "Errore nel recupero abbonamenti");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}