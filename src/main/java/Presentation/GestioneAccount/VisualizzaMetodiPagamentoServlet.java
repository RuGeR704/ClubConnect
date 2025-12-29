package Presentation.GestioneAccount;

import Application.GestioneAccount.UtenteBean;
import Application.GestionePagamenti.MetodoPagamentoBean;
import Storage.PagamentoDAO;
import Storage.UtenteDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name ="VisualizzaMetodidiPagamentoServlet", value ="/VisualizzaMetodidiPagamentoServlet")
public class VisualizzaMetodiPagamentoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        try {
            HttpSession session = request.getSession(false);
            UtenteBean utente = null;
            // Quando sei sulla pagine utente cce un bottone modifica dati utente tipo
            if (session == null || session.getAttribute("utente") == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp"); //manda al login se la sessione non esiste
                return;
            }
            utente = (UtenteBean) session.getAttribute("utente");
            int id_utente = utente.getId_utente();
            UtenteDAO udao = new UtenteDAO();
            List<MetodoPagamentoBean> metodipagamento = udao.doRetrieveAllMetodiPagamento(id_utente);
            request.setAttribute("metodipagamento", metodipagamento);
            request.getRequestDispatcher("/gestioneUtente.jsp").forward(request,response);
        } catch (SQLException sql) {
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        }
    }
}
