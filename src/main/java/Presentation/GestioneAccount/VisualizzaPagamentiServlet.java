package Presentation.GestioneAccount;

import Application.GestioneAccount.AccountService;
import Application.GestioneAccount.UtenteBean;
import Application.GestionePagamenti.DettagliPagamentoBean;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "VisualizzaPagamentiServlet", value = "/VisualizzaPagamentiServlet")
public class VisualizzaPagamentiServlet extends HttpServlet {

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

            // Service
            List<DettagliPagamentoBean> pagamenti = accountService.getStoricoPagamenti(utente.getId_utente());

            if (pagamenti != null && !pagamenti.isEmpty()) {
                request.setAttribute("Pagamenti", pagamenti);
            }
            RequestDispatcher rd = request.getRequestDispatcher("/PagamentiEffettuati.jsp");
            rd.forward(request, response);

        } catch(SQLException sql){
            sql.printStackTrace();
            request.setAttribute("errore", "Errore nel recupero pagamenti");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}