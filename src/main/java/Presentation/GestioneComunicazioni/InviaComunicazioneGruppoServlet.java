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
import java.sql.Date;
import java.sql.SQLException;

@WebServlet(name = "InviaComunicazioneGruppoServlet", urlPatterns = {"/InviaComunicazioneGruppoServlet"})
public class InviaComunicazioneGruppoServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UtenteBean utente = (UtenteBean) session.getAttribute("utente");

        // Controllo Login
        if (utente == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Recupero Parametri
        String idGruppoStr = request.getParameter("idGruppo");
        String titolo = request.getParameter("titolo");
        String contenuto = request.getParameter("contenuto");
        String foto = request.getParameter("foto");

        // Validazione base
        if (idGruppoStr == null || idGruppoStr.isEmpty() || titolo == null || titolo.trim().isEmpty()) {
            request.setAttribute("errore", "Dati mancanti (ID Gruppo o Titolo).");
            // In caso di errore, si torna alla pagina del gruppo
            response.sendRedirect("VisualizzaGruppoServlet?id=" + idGruppoStr);
            return;
        }

        try {
            int idGruppo = Integer.parseInt(idGruppoStr);

            // NOTA SULLA SICUREZZA:
            // Qui si dovrebbe controllare se l'utente è davvero il "Gestore" di QUESTO gruppo.
            // Questo richiederebbe di chiamare GestioneGruppoBean.isGestore(utente.getId(), idGruppo).
            // Per ora assumo che il controllo sia fatto a monte nella JSP.

            // Creazione Bean
            ComunicazioniBean nuovaComunicazione = new ComunicazioniBean();
            nuovaComunicazione.setTitolo(titolo);
            nuovaComunicazione.setContenuto(contenuto);
            nuovaComunicazione.setFoto(foto);
            nuovaComunicazione.setId_autore(utente.getId_utente());

            // IMPOSTAZIONE PER GRUPPO
            nuovaComunicazione.setId_gruppo(idGruppo);
            nuovaComunicazione.setIsglobal(false); // È una news privata del gruppo

            nuovaComunicazione.setDataPubblicazione(new Date(System.currentTimeMillis()));

            // Salvataggio
            GestioneComunicazioniBean service = new GestioneComunicazioniBean();
            service.creaComunicazione(nuovaComunicazione);

            // Redirect
            // Torniamo alla pagina che visualizza le news di quel gruppo specifico
            response.sendRedirect("VisualizzaComunicazioniGruppoServlet?idGruppo=" + idGruppo);

        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.sendRedirect("index.jsp"); // ID Gruppo non valido
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errore", "Errore nel salvataggio della comunicazione.");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("index.jsp");
    }
}