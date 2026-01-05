package Presentation.GestionePagamenti;
import Application.GestioneAccount.UtenteBean;
import Application.GestionePagamenti.MetodoPagamentoBean;
import Storage.ConPool;
import Storage.PagamentoDAO;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet(name = "RimuoviMetodoPagamentoServlet", value = "/RimuoviMetodoPagamentoServlet")
public class RimuoviMetodoPagamentoServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try (Connection con= ConPool.getConnection()){
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("utente") == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp"); //manda al login se la sessione non esiste
                return;
            }
            UtenteBean utente = (UtenteBean) session.getAttribute("utente");
            int id_utente=utente.getId_utente();
            String idParam = request.getParameter("id_metodo_pagamento");
            if(idParam == null || idParam.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/VisualizzaMetodiPagamentoServlet?error=missingId");
                return;
            }
            int id_metodo_pagamento = Integer.parseInt(idParam);
            PagamentoDAO dao = new PagamentoDAO();
            dao.doDeleteMetodoPagamento(con,id_metodo_pagamento,id_utente);
            response.sendRedirect(request.getContextPath() + "/VisualizzaMetodiPagamentoServlet");
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/VisualizzaMetodiPagamentoServlet");
        }
    }
}