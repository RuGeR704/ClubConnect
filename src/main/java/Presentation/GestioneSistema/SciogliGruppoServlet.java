package Presentation.GestioneSistema;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneSistema.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "SciogliGruppoServlet", urlPatterns = {"/SciogliGruppoServlet"})
public class SciogliGruppoServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UtenteBean utente = (UtenteBean) session.getAttribute("utente");
        String idStr = request.getParameter("idGruppo");

        if (idStr != null) {
            try {
                int idGruppo = Integer.parseInt(idStr);

                GestioneSistemaInterface sistema = new GestioneSistemaProxy(utente);
                sistema.sciogliGruppo(idGruppo);

                // Successo: Torno al Feed (il gruppo è eliminato)
                response.sendRedirect("FeedServlet?msg=GruppoSciolto");

            } catch (SecurityException e) {
                response.sendRedirect("FeedServlet?error=Unauthorized");
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect("FeedServlet?error=GenericError");
            }
        } else {
            response.sendRedirect("FeedServlet");
        }
    }
}