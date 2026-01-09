package Presentation.GestionePagamenti;

import Application.GestioneAccount.UtenteBean;
import Application.GestionePagamenti.GestionePagamentiBean;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(name = "PagaRettaServlet", urlPatterns = {"/PagaRettaServlet"})
public class PagaRettaServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UtenteBean utente = (UtenteBean) session.getAttribute("utente");

        // Controllo Login
        if (utente == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Recupero Parametri
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

            // Esecuzione del pagamento tramite Bean
            GestionePagamentiBean service = new GestionePagamentiBean();
            boolean successo = service.pagaRetta(idGruppo, idMetodo, importo);

            if (successo) {
                // Pagamento riuscito: torniamo alla pagina del club
                response.sendRedirect("VisualizzaGruppoServlet?id=" + idGruppo + "&status=success");
            } else {
                // Errore durante il salvataggio
                request.setAttribute("error", "Errore tecnico durante l'elaborazione del pagamento.");
                request.getRequestDispatcher("paginaGruppo.jsp").forward(request, response);
            }

        } catch (NumberFormatException e) {
            response.sendRedirect("feedServlet");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("feedServlet");
    }
}