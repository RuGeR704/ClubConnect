package Presentation.GestioneSistema;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneSistema.GestioneSistemaInterface;
import Application.GestioneSistema.GestioneSistemaProxy;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "BannaUtenteServlet", urlPatterns = {"/BannaUtenteServlet"})
public class BannaUtenteServlet extends HttpServlet {

    // Campo per iniettare il Mock durante i test
    private GestioneSistemaInterface sistemaTest;

    public void setSistemaTest(GestioneSistemaInterface sistemaTest) {
        this.sistemaTest = sistemaTest;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UtenteBean utente = (UtenteBean) session.getAttribute("utente");

        String idStr = request.getParameter("idUtente");
        String motivazione = request.getParameter("motivazione"); // Recupero parametro motivazione

        // Validazione ID
        if (idStr != null && !idStr.isEmpty()) {
            try {
                // TCS 3.2_1: Controllo Motivazione Vuota
                // Se la motivazione è nulla o vuota, restituisce errore specifico
                if (motivazione == null || motivazione.trim().isEmpty()) {
                    response.sendRedirect("VisualizzaListaClientiServlet?error=MotivazioneVuota");
                    return;
                }

                int idDaBannare = Integer.parseInt(idStr);

                // Logica Proxy: Usa il mock se presente (test), altrimenti crea oggetto reale
                GestioneSistemaInterface sistema = (sistemaTest != null)
                        ? sistemaTest
                        : new GestioneSistemaProxy(utente);

                // Esecuzione azione
                sistema.bannaUtente(idDaBannare);

                // Successo (TCS 3.2_1 Caso positivo)
                response.sendRedirect("VisualizzaListaClientiServlet?msg=UtenteBannato");

            } catch (SecurityException e) {
                // Errore Permessi (gestito dal Proxy)
                response.sendRedirect("FeedServlet?error=Unauthorized");

            } catch (IllegalArgumentException e) {
                // TCS 3.1_1: Errore Utente Già Bannato
                // Il Bean/Service deve lanciare questa eccezione se lo stato è già 'bannato'
                response.sendRedirect("VisualizzaListaClientiServlet?error=GiaBannato");

            } catch (Exception e) {
                // Errore generico di sistema
                e.printStackTrace();
                response.sendRedirect("FeedServlet?error=GenericError");
            }
        } else {
            // Nessun ID passato
            response.sendRedirect("VisualizzaListaClientiServlet");
        }
    }
}