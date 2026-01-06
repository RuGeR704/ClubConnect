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
import java.time.LocalDateTime;

@WebServlet(name = "InviaComunicazioneGlobaleServlet", urlPatterns = {"/InviaComunicazioneGlobaleServlet"})
public class InviaComunicazioneGlobaleServlet extends HttpServlet {

    // 1. Iniezione del Service (inizializzato di default per la produzione)
    private ComunicazioneService service = new ComunicazioneService();

    // 2. Setter per i Test (fondamentale per Mockito)
    public void setService(ComunicazioneService service) {
        this.service = service;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UtenteBean utente = (UtenteBean) session.getAttribute("utente");

        // Controllo Sicurezza: Solo l'ADMIN può inviare comunicazioni globali
        if (utente == null || !utente.isAdmin()) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Recupero parametri dal form
        String titolo = request.getParameter("titolo");
        String contenuto = request.getParameter("contenuto");
        String foto = request.getParameter("foto");

        // Validazione
        if (titolo == null || titolo.trim().isEmpty() || contenuto == null || contenuto.trim().isEmpty()) {
            request.setAttribute("errore", "Titolo e contenuto sono obbligatori.");
            request.getRequestDispatcher("formComunicazioneGlobale.jsp").forward(request, response);
            return;
        }

        try {
            // Creazione del Bean
            ComunicazioniBean nuovaComunicazione = new ComunicazioniBean();
            nuovaComunicazione.setTitolo(titolo);
            nuovaComunicazione.setContenuto(contenuto);
            nuovaComunicazione.setFoto(foto);
            nuovaComunicazione.setId_autore(utente.getId_utente());

            // IMPOSTAZIONI GLOBALI
            nuovaComunicazione.setIsglobal(true);
            nuovaComunicazione.setId_gruppo(-1); // -1 indica "Nessun Gruppo specifico"

            // Impostiamo la data corrente
            nuovaComunicazione.setDataPubblicazione(LocalDateTime.now());

            // 3. Salvataggio tramite Service (non più DAO diretto)
            service.creaComunicazione(nuovaComunicazione);

            // Redirect alla lista
            response.sendRedirect("VisualizzaComunicazioniGlobaliServlet");

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errore", "Errore nel salvataggio della comunicazione.");
            request.getRequestDispatcher("formComunicazioneGlobale.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("index.jsp");
    }
}