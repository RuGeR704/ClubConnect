package Presentation.GestioneSistema;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneSistema.GestioneSistemaInterface;
import Application.GestioneSistema.GestioneSistemaProxy;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "AttivaManutenzioneServlet", urlPatterns = {"/AttivaManutenzioneServlet"})
public class AttivaManutenzioneServlet extends HttpServlet {

    // Campo per iniettare il Mock durante i test
    private GestioneSistemaInterface sistemaTest;

    public void setSistemaTest(GestioneSistemaInterface sistemaTest) {
        this.sistemaTest = sistemaTest;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UtenteBean utente = (UtenteBean) session.getAttribute("utente");

        try {
            // Logica Ternaria: Se stiamo testando usa il Mock, altrimenti crea il Proxy reale
            GestioneSistemaInterface sistema = (sistemaTest != null)
                    ? sistemaTest
                    : new GestioneSistemaProxy(utente);

            // 1. Azione
            sistema.attivaManutenzione();

            // 2. Imposto flag globale (Context) così tutta l'app sa che è in manutenzione
            getServletContext().setAttribute("manutenzione", true);

            // 3. Successo
            response.sendRedirect("VisualizzaListaClientiServlet?msg=ManutenzioneAttiva");

        } catch (SecurityException e) {
            // Errore: Utente non autorizzato (intercettato dal Proxy)
            response.sendRedirect("FeedServlet?error=Unauthorized");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("FeedServlet?error=GenericError");
        }
    }
}