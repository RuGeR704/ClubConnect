package Presentation.GestioneSistema;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneSistema.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "BannaUtenteServlet", urlPatterns = {"/BannaUtenteServlet"})
public class BannaUtenteServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UtenteBean utente = (UtenteBean) session.getAttribute("utente");

        String idStr = request.getParameter("idUtente");

        if (idStr != null) {
            try {
                int idDaBannare = Integer.parseInt(idStr);

                // Proxy Check
                GestioneSistemaInterface sistema = new GestioneSistemaProxy(utente);
                sistema.bannaUtente(idDaBannare);

                // Successo: Ricarico la lista per vedere l'utente colorato di rosso
                response.sendRedirect("VisualizzaListaClientiServlet?msg=UtenteBannato");

            } catch (SecurityException e) {
                response.sendRedirect("FeedServlet?error=Unauthorized");
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect("FeedServlet?error=GenericError");
            }
        } else {
            // Nessun ID passato
            response.sendRedirect("VisualizzaListaClientiServlet");
        }
    }
}