package Presentation.GestioneSistema;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneSistema.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "SbannaUtenteServlet", urlPatterns = {"/SbannaUtenteServlet"})
public class SbannaUtenteServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UtenteBean utente = (UtenteBean) session.getAttribute("utente");
        String idStr = request.getParameter("idUtente");

        if (idStr != null) {
            try {
                int idDaSbannare = Integer.parseInt(idStr);

                GestioneSistemaInterface sistema = new GestioneSistemaProxy(utente);
                sistema.sbannaUtente(idDaSbannare);

                response.sendRedirect("VisualizzaListaClientiServlet?msg=UtenteAttivato");

            } catch (SecurityException e) {
                response.sendRedirect("FeedServlet?error=Unauthorized");
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect("FeedServlet?error=GenericError");
            }
        } else {
            response.sendRedirect("VisualizzaListaClientiServlet");
        }
    }
}