package Presentation.GestioneAccount;

import Application.GestioneAccount.AccountService;
import Application.GestioneAccount.UtenteBean;
import Application.GestionePagamenti.MetodoPagamentoBean;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name ="VisualizzaMetodidiPagamentoServlet", value ="/VisualizzaMetodidiPagamentoServlet")
public class VisualizzaMetodiPagamentoServlet extends HttpServlet {

    private AccountService accountService = new AccountService();

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("utente") == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }

            UtenteBean utente = (UtenteBean) session.getAttribute("utente");

            // Service
            List<MetodoPagamentoBean> metodipagamento = accountService.getMetodiPagamento(utente.getId_utente());

            request.setAttribute("metodipagamento", metodipagamento);
            request.getRequestDispatcher("gestioneUtente.jsp").forward(request,response);

        } catch (SQLException sql) {
            sql.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        }
    }
}