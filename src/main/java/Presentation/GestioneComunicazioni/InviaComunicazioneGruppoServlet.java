package Presentation.GestioneComunicazioni;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneComunicazioni.ComunicazioniBean;
import Application.GestioneComunicazioni.ComunicazioneService;
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

    // 1. Iniezione del Service
    private ComunicazioneService service = new ComunicazioneService();

    // 2. Setter per i Test
    public void setService(ComunicazioneService service) {
        this.service = service;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UtenteBean utente = (UtenteBean) session.getAttribute("utente");

        // Controllo Login base
        if (utente == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Recupero Parametri
        String idGruppoStr = request.getParameter("idGruppo");
        String titolo = request.getParameter("titolo");
        String contenuto = request.getParameter("contenuto");
        String foto = request.getParameter("foto");

        // Validazione
        if (idGruppoStr == null || idGruppoStr.isEmpty() || titolo == null || titolo.trim().isEmpty()) {
            request.setAttribute("errore", "Dati mancanti (ID Gruppo o Titolo).");
            // Se manca l'ID gruppo è difficile fare redirect preciso, proviamo a tornare indietro o home
            response.sendRedirect("index.jsp");
            return;
        }

        try {
            int idGruppo = Integer.parseInt(idGruppoStr);

            // Creazione Bean
            ComunicazioniBean nuovaComunicazione = new ComunicazioniBean();
            nuovaComunicazione.setTitolo(titolo);
            nuovaComunicazione.setContenuto(contenuto);
            nuovaComunicazione.setFoto(foto);
            nuovaComunicazione.setId_autore(utente.getId_utente());

            // IMPOSTAZIONI GRUPPO
            nuovaComunicazione.setId_gruppo(idGruppo);
            nuovaComunicazione.setIsglobal(false); // È privata del gruppo

            nuovaComunicazione.setDataPubblicazione(new Date(System.currentTimeMillis()));

            // 3. Salvataggio tramite Service
            service.creaComunicazione(nuovaComunicazione);

            // Redirect alla bacheca del gruppo
            response.sendRedirect("VisualizzaComunicazioniGruppoServlet?idGruppo=" + idGruppo);

        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.sendRedirect("index.jsp");
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errore", "Errore nel salvataggio della comunicazione.");
            // Qui idealmente faresti forward alla pagina del gruppo con messaggio di errore
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("index.jsp");
    }
}