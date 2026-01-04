package Presentation.GestioneComunicazioni;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneComunicazioni.GestioneComunicazioniBean;
import java.io.IOException;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "EliminaComunicazioneGlobaleServlet", urlPatterns = {"/EliminaComunicazioneGlobaleServlet"})
public class EliminaComunicazioneGlobaleServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Controllo Sicurezza: L'utente deve essere loggato e ADMIN
        HttpSession session = request.getSession();
        UtenteBean utente = (UtenteBean) session.getAttribute("utente");

        if (utente == null || !utente.isAdmin()) {
            // Se non è admin, viene mandato via (es. login)
            response.sendRedirect("login.jsp");
            return;
        }
        // Recupero Parametri
        String idStr = request.getParameter("idComunicazione");
        if (idStr != null && !idStr.isEmpty()) {
            try {
                int idComunicazione = Integer.parseInt(idStr);

                // Chiamata alla Logica di Business
                GestioneComunicazioniBean service = new GestioneComunicazioniBean();
                service.rimuoviComunicazione(idComunicazione);

                // Successo
                request.setAttribute("messaggio", "Comunicazione eliminata con successo.");

            } catch (NumberFormatException e) {
                request.setAttribute("errore", "ID Comunicazione non valido.");
                e.printStackTrace();
            } catch (SQLException e) {
                request.setAttribute("errore", "Errore database durante l'eliminazione.");
                e.printStackTrace();
            }
        } else {
            request.setAttribute("errore", "Parametro ID mancante.");
        }

        // Reindirizzamento
        // Torniamo alla lista delle comunicazioni globali per vedere l'aggiornamento
        response.sendRedirect("VisualizzaComunicazioniGlobaliServlet");
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Permettiamo di chiamare la cancellazione anche via GET (es. link <a href="...?id=5">)
        doPost(request, response);
    }
}
