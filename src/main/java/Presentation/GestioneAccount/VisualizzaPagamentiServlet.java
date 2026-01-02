package Presentation.GestioneAccount;
import Application.GestioneAccount.UtenteBean;
import Application.GestioneGruppo.GruppoBean;
import Application.GestionePagamenti.DettagliPagamentoBean;
import Storage.UtenteDAO;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "VisualizzaPagamentiServlet", value = "/VisualizzaPagamentiServlet")
public class VisualizzaPagamentiServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("utente") == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }else {
                UtenteBean utente = (UtenteBean) session.getAttribute("utente");
                UtenteDAO dao = new UtenteDAO();
                List<DettagliPagamentoBean> Pagamenti = dao.doRetrievePagamenti(utente.getId_utente());
                if (Pagamenti.size() > 0) {
                    request.setAttribute("Pagamenti", Pagamenti);
                }
                RequestDispatcher rd = request.getRequestDispatcher("/PagamentiEffettuati.jsp");
                rd.forward(request, response);
            }
        }catch(SQLException sql){
            sql.printStackTrace();
            request.setAttribute("errore", "errore dao"); // per vedere gli errori
            RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");
            rd.forward(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}