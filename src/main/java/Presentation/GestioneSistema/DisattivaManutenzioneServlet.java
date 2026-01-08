package Presentation.GestioneSistema;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneSistema.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet(name = "DisattivaManutenzioneServlet", urlPatterns = {"/DisattivaManutenzioneServlet"})
public class DisattivaManutenzioneServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UtenteBean utente = (UtenteBean) session.getAttribute("utente");

        try {
            GestioneSistemaInterface sistema = new GestioneSistemaProxy(utente);
            sistema.disattivaManutenzione();

            // Spengo il flag globale
            getServletContext().setAttribute("manutenzione", false);

            response.sendRedirect("VisualizzaListaClientiServlet?msg=ManutenzioneDisattivata");

        } catch (SecurityException e) {
            response.sendRedirect("FeedServlet?error=Unauthorized");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("FeedServlet?error=GenericError");
        }
    }
}