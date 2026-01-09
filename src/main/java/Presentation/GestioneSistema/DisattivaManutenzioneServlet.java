package Presentation.GestioneSistema;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneSistema.GestioneSistemaInterface;
import Application.GestioneSistema.GestioneSistemaProxy;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "DisattivaManutenzioneServlet", urlPatterns = {"/DisattivaManutenzioneServlet"})
public class DisattivaManutenzioneServlet extends HttpServlet {

    private GestioneSistemaInterface sistemaTest;

    public void setSistemaTest(GestioneSistemaInterface sistemaTest) {
        this.sistemaTest = sistemaTest;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UtenteBean utente = (UtenteBean) session.getAttribute("utente");

        try {
            GestioneSistemaInterface sistema = (sistemaTest != null)
                    ? sistemaTest
                    : new GestioneSistemaProxy(utente);

            sistema.disattivaManutenzione();

            // Rimuovo/Aggiorno il flag globale
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