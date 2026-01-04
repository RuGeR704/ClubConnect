package Presentation.GestioneComunicazioni;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneComunicazioni.ComunicazioniBean;
import Application.GestioneComunicazioni.GestioneComunicazioniBean;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "EliminaComunicazioneGruppoServlet", urlPatterns = {"/EliminaComunicazioneGruppoServlet"})
public class EliminaComunicazioneGruppoServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UtenteBean utente = (UtenteBean) session.getAttribute("utente");

        // Controllo login base
        if (utente == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String idStr = request.getParameter("idComunicazione");

        if (idStr != null && !idStr.isEmpty()) {
            try {
                int idComunicazione = Integer.parseInt(idStr);
                GestioneComunicazioniBean service = new GestioneComunicazioniBean();

                // Recuperiamo la comunicazione PRIMA di eliminare
                // Serve per sapere:
                // Se esiste
                // Di quale gruppo fa parte (per il redirect)
                // Chi è l'autore (per il controllo permessi)
                ComunicazioniBean comunicazione = service.recuperaComunicazione(idComunicazione);

                if (comunicazione != null) {
                    // Controllo Permessi Avanzato
                    // L'utente può eliminare SOLO SE:
                    // È un Admin globale
                    // OPPURE È l'autore della comunicazione (cioè il Gestore che l'ha scritta)
                    if (utente.isAdmin() || utente.getId_utente() == comunicazione.getId_autore()) {

                        service.rimuoviComunicazione(idComunicazione);

                        // Successo: Torniamo alla pagina delle comunicazioni DI QUEL GRUPPO
                        response.sendRedirect("VisualizzaComunicazioniGruppoServlet?idGruppo=" + comunicazione.getId_gruppo());
                        return;

                    } else {
                        // Utente non autorizzato
                        request.setAttribute("errore", "Non hai i permessi per eliminare questa comunicazione.");
                    }
                } else {
                    request.setAttribute("errore", "Comunicazione non trovata.");
                }

            } catch (NumberFormatException e) {
                request.setAttribute("errore", "ID non valido.");
            } catch (SQLException e) {
                request.setAttribute("errore", "Errore del database.");
                e.printStackTrace();
            }
        } else {
            request.setAttribute("errore", "Parametro mancante.");
        }

        // Se qualcosa va storto, torniamo indietro o alla home
        response.sendRedirect("index.jsp"); // O una pagina di errore generica
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}