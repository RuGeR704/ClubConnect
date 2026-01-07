package Presentation.GestioneSistema;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneSistema.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "AttivaManutenzioneServlet", urlPatterns = {"/AttivaManutenzioneServlet"})
public class AttivaManutenzioneServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UtenteBean utente = (UtenteBean) session.getAttribute("utente");

        try {
            // Logica Proxy (Controllo Admin)
            GestioneSistemaInterface sistema = new GestioneSistemaProxy(utente);
            sistema.attivaManutenzione();

            // Imposto flag globale per tutto il sito
            getServletContext().setAttribute("manutenzione", true);

            // Successo: Torno alla lista clienti (dashboard admin)
            response.sendRedirect("VisualizzaListaClientiServlet?msg=ManutenzioneAttiva");

        } catch (SecurityException e) {
            // Errore Sicurezza: Non sei admin -> Feed
            response.sendRedirect("FeedServlet?error=Unauthorized");
        } catch (Exception e) {
            // Errore Generico -> Feed
            e.printStackTrace();
            response.sendRedirect("FeedServlet?error=GenericError");
        }
    }
}