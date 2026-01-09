package Presentation.GestionePagamenti;

import Application.GestioneAccount.UtenteBean;
import Application.GestionePagamenti.PagamentoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/ModificaAbbonamentoServlet")
public class ModificaAbbonamentoServlet extends HttpServlet {

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
        String importoStr = request.getParameter("importo");
        String frequenzaStr = request.getParameter("frequenza");

        // Validazione Input
        if (idGruppoStr == null || importoStr == null || frequenzaStr == null) {
            request.setAttribute("errore", "Dati mancanti per la modifica.");
            request.getRequestDispatcher("feedServlet").forward(request, response);
            return;
        }

        try {
            int idGruppo = Integer.parseInt(idGruppoStr);
            double importo = Double.parseDouble(importoStr);
            int frequenza = Integer.parseInt(frequenzaStr);

            // 1. Controllo Permessi (tramite Service)
            if (!pagamentoService.isUtenteGestore(utente.getId_utente(), idGruppo)) {
                response.sendRedirect("feedServlet"); // O pagina "Accesso Negato"
                return;
            }

            // 2. Esecuzione Modifica
            // Nota: impostaAbbonamento e modificaAbbonamento fanno la stessa cosa (UPDATE)
            pagamentoService.impostaAbbonamento(idGruppo, importo, frequenza);

            // 3. Successo
            response.sendRedirect("VisualizzaGruppoServlet?id=" + idGruppo + "&msg=ModificaOk");

        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.sendRedirect("feedServlet");
        } catch (IllegalArgumentException e) {
            // Es. ID gruppo non valido
            request.setAttribute("errore", e.getMessage());
            request.getRequestDispatcher("VisualizzaGruppoServlet?id=" + idGruppoStr).forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("feedServlet");
    }
}