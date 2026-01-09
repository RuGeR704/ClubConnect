package Presentation.GestionePagamenti;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneGruppo.GruppoBean;
import Application.GestionePagamenti.PagamentoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/PagaRettaServlet")
public class PagaRettaServlet extends HttpServlet {

    private PagamentoService pagamentoService = new PagamentoService();

    public void setPagamentoService(PagamentoService service) {
        this.pagamentoService = service;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        UtenteBean utente = (session != null) ? (UtenteBean) session.getAttribute("utente") : null;

        if (utente == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String idGruppoStr = request.getParameter("idGruppo");
        String idMetodoStr = request.getParameter("idMetodo");
        String importoStr = request.getParameter("importo");

        if (idGruppoStr == null || idMetodoStr == null || importoStr == null) {
            response.sendRedirect("feedServlet");
            return;
        }

        try {
            int idGruppo = Integer.parseInt(idGruppoStr);
            int idMetodo = Integer.parseInt(idMetodoStr);
            double importo = Double.parseDouble(importoStr);

            // 1. Chiamata al Service
            pagamentoService.pagaRetta(idGruppo, idMetodo, importo);
            boolean isPagato = pagamentoService.isPagato(idGruppo, utente.getId_utente());

            request.setAttribute("hasPaid", isPagato);

            // 2. Successo
            response.sendRedirect("VisualizzaGruppoServlet?id=" + idGruppo + "&esito=pagamento_ok");

        } catch (Exception e) {
            e.printStackTrace();

            //errore:
            response.sendRedirect("VisualizzaGruppoServlet?id=" + idGruppoStr + "&esito=pagamento_errore");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("feedServlet");
    }
}